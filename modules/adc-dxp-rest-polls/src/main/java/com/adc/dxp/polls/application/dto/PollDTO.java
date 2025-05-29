package com.adc.dxp.polls.application.dto;

import java.util.List;

public class PollDTO {
    private long formInstanceId;
    private String name;
    private String question;
    private List<PollAnalyticsDTO> pollAnalyticsDTOS;

    public List<PollAnalyticsDTO> getPollAnalyticsDTOS() {
        return pollAnalyticsDTOS;
    }

    public void setPollAnalyticsDTOS(List<PollAnalyticsDTO> pollAnalyticsDTOS) {
        this.pollAnalyticsDTOS = pollAnalyticsDTOS;
    }

    public PollDTO(long formInstanceId, String name, String question) {
        this.formInstanceId = formInstanceId;
        this.name = name;
        this.question = question;
    }

    public PollDTO() {
    }

    public long getFormInstanceId() {
        return formInstanceId;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public void setFormInstanceId(long formInstanceId) {
        this.formInstanceId = formInstanceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "PollDTO{" +
                "formInstanceId=" + formInstanceId +
                ", name='" + name + '\'' +
                ", question='" + question + '\'' +
                '}';
    }
}
