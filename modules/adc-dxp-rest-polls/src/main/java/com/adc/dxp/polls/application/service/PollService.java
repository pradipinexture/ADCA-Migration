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

    Locale locale = LocaleUtil.getSiteDefault();

    public List<PollDTO> getPolls(long groupId) {
        List<PollDTO> pollDTOList = new ArrayList<>();

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

    public List<FormRecordDTO> getFormRecords(long formId, long userId) {
        List<FormRecordDTO> result = new ArrayList<>();

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
                String rawValue = fieldValue.getValue().getString(locale);
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

    public List<PollAnalyticsDTO> getPollAnalytics(long formId) {
        List<PollAnalyticsDTO> results = new ArrayList<>();

        try {
            DDMFormInstance formInstance = DDMFormInstanceLocalServiceUtil.getDDMFormInstance(formId);
            DDMForm form = formInstance.getDDMForm();
            Map<String, DDMFormField> fieldMap = form.getDDMFormFieldsMap(true);

            Optional<Map.Entry<String, DDMFormField>> radioFieldEntryOpt = fieldMap.entrySet().stream()
                    .filter(entry -> "radio".equalsIgnoreCase(entry.getValue().getType()))
                    .findFirst();

            if (!radioFieldEntryOpt.isPresent()) {
                return results;
            }

            String radioFieldName = radioFieldEntryOpt.get().getKey();
            DDMFormField radioField = radioFieldEntryOpt.get().getValue();

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
                    // Skip invalid records
                }
            }

            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                String optionKey = entry.getKey();
                int count = entry.getValue();

                String label = radioField.getDDMFormFieldOptions()
                        .getOptionLabels(optionKey)
                        .getString(locale);

                results.add(new PollAnalyticsDTO(label != null ? label : optionKey, count));
            }

        } catch (Exception e) {
            return new ArrayList<>();
        }

        return results;
    }

}
