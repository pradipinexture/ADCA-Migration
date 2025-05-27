package com.adc.dxp.polls.application.dto;

public class PollDTO {
    private long formInstanceId;
    private String name;
    private String question;

    public PollDTO(long formInstanceId, String name, String question) {
        this.formInstanceId = formInstanceId;
        this.name = name;
        this.question = question;
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
}
