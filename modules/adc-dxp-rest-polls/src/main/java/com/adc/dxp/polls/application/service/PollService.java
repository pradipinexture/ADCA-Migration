package com.adc.dxp.polls.application.service;

import com.adc.dxp.polls.application.dto.FormRecordDTO;
import com.adc.dxp.polls.application.dto.PollAnalyticsDTO;
import com.adc.dxp.polls.application.dto.PollDTO;
import com.liferay.dynamic.data.mapping.model.*;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.osgi.service.component.annotations.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component(service = PollService.class, immediate = true)
public class PollService {

    Locale defaultLocale = LocaleUtil.getSiteDefault();

    public List<PollDTO> getPolls(long groupId, String languageId) {
        List<PollDTO> pollDTOList = new ArrayList<>();
        Locale locale = getLocale(languageId);

        try {
            List<DDMFormInstance> forms = DDMFormInstanceLocalServiceUtil.getFormInstances(groupId);

            for (DDMFormInstance form : forms) {
                Map<String, DDMFormField> fieldsMap = form.getDDMForm().getDDMFormFieldsMap(true);

                Optional<DDMFormField> firstRadioField = fieldsMap.values().stream()
                        .filter(field -> "radio".equalsIgnoreCase(field.getType()))
                        .findFirst();

                if (firstRadioField.isPresent()) {
                    String questionLabel = firstRadioField.get().getLabel().getString(locale);

                    pollDTOList.add(new PollDTO(
                            form.getFormInstanceId(),
                            form.getName(locale),
                            questionLabel
                    ));
                }
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }

        return pollDTOList;
    }

    public List<FormRecordDTO> getFormRecords(long formId, long userId, String languageId) {
        List<FormRecordDTO> result = new ArrayList<>();
        Locale locale = getLocale(languageId);
        try {

            List<DDMFormInstanceRecord> records = DDMFormInstanceRecordLocalServiceUtil.getFormInstanceRecords(
                    formId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

            List<DDMFormInstanceRecord> approvedRecords = records.stream()
                    .filter(record -> {
                        try {return record.getStatus() == WorkflowConstants.STATUS_APPROVED;
                        } catch (PortalException e) {return false;}
                    })
                    .collect(Collectors.toList());

            if (approvedRecords.isEmpty()) return result;

            DDMFormInstanceRecord latest = approvedRecords.get(0); // Assume latest if multiple

            DDMFormValues formValues = latest.getFormInstanceRecordVersion().getDDMFormValues();
            Map<String, DDMFormField> fieldMap = formValues.getDDMForm().getDDMFormFieldsMap(true);

            for (DDMFormFieldValue fieldValue : formValues.getDDMFormFieldValues()) {
                String fieldName = fieldValue.getName();
                DDMFormField field = fieldMap.get(fieldName);

                if (field == null) continue;

                String label = field.getLabel().getString(locale);
                //String rawValue = fieldValue.getValue().getString(locale);
                String rawValue = fieldValue.getValue().getString(null);

                String finalValue = rawValue;

                if (Arrays.asList("radio", "select", "checkbox_multiple").contains(field.getType())) {
                    if (rawValue != null && rawValue.startsWith("[") && rawValue.endsWith("]")) {
                        String[] cleaned = rawValue.replace("[", "").replace("]", "")
                                .replace("\"", "").split(",");
                        finalValue = Arrays.stream(cleaned)
                                .map(String::trim)
                                .map(opt -> {
                                    LocalizedValue optLabels = field.getDDMFormFieldOptions().getOptionLabels(opt);
                                    return optLabels != null ? optLabels.getString(locale) : opt;
                                })
                                .collect(Collectors.joining(", "));
                    } else {
                        LocalizedValue optLabel = field.getDDMFormFieldOptions().getOptionLabels(rawValue);
                        finalValue = (optLabel != null) ? optLabel.getString(locale) : rawValue;
                    }
                }

                result.add(new FormRecordDTO(label, finalValue));
            }

        } catch (Exception e) {
            return new ArrayList<>();
        }

        return result;
    }

    private Locale getLocale(String languageId) {
        if(languageId == null || languageId.isEmpty()) {
            return Locale.forLanguageTag(defaultLocale.getLanguage());
        } else {
            return Locale.forLanguageTag(languageId);
        }
    }


    public PollDTO getPollAnalytics(long formId, String languageId) {
        List<PollAnalyticsDTO> results = new ArrayList<>();
        PollDTO pollDTO = new PollDTO();

        Locale locale = getLocale(languageId);

        try {
            DDMFormInstance formInstance = DDMFormInstanceLocalServiceUtil.getDDMFormInstance(formId);
            //Locale locale = formInstance.getDDMForm().getDefaultLocale();
            DDMForm form = formInstance.getDDMForm();
            Map<String, DDMFormField> fieldMap = form.getDDMFormFieldsMap(true);

            Optional<DDMFormField> firstRadioField = fieldMap.values().stream()
                    .filter(field -> "radio".equalsIgnoreCase(field.getType()))
                    .findFirst();

            if (!firstRadioField.isPresent()) {
                return null;
            }

            DDMFormField radioField = firstRadioField.get();
            String radioFieldName = fieldMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == radioField)
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            String questionLabel = radioField.getLabel().getString(locale);

            pollDTO.setFormInstanceId(formInstance.getFormInstanceId());
            pollDTO.setName(formInstance.getName(locale));
            pollDTO.setQuestion(questionLabel);

            // Step 1: Count responses
            Map<String, Integer> countMap = new HashMap<>();
            List<DDMFormInstanceRecord> records = DDMFormInstanceRecordLocalServiceUtil.getFormInstanceRecords(formId);

            for (DDMFormInstanceRecord record : records) {
                try {
                    if (record.getStatus() != WorkflowConstants.STATUS_APPROVED) continue;

                    DDMFormValues formValues = record.getDDMFormValues();

                    for (DDMFormFieldValue fieldValue : formValues.getDDMFormFieldValues()) {
                        if (fieldValue.getName().equals(radioFieldName)) {
                            String rawValue = fieldValue.getValue().getString(locale);
                            if (rawValue != null && !rawValue.isEmpty()) {
                                countMap.put(rawValue, countMap.getOrDefault(rawValue, 0) + 1);
                            }
                        }
                    }

                } catch (Exception ignored) {
                    // skip invalid record
                }
            }

            // Step 2: Build result with ALL options, including 0-vote ones
            radioField.getDDMFormFieldOptions().getOptionsValues().forEach(optionKey -> {
                String label = radioField.getDDMFormFieldOptions().getOptionLabels(optionKey).getString(locale);
                int count = countMap.getOrDefault(optionKey, 0);
                results.add(new PollAnalyticsDTO(label != null ? label : optionKey, count));
            });

        } catch (Exception e) {
            return null;
        }

        pollDTO.setPollAnalyticsDTOS(results);
        return pollDTO;
    }


}
