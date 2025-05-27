package com.adc.dxp.polls.application.dto;

public class FormRecordDTO {
    private String question;
    private String value;

    public FormRecordDTO() {
    }

    public FormRecordDTO(String question, String value) {
        this.question = question;
        this.value = value;
    }

    public String getQuestion() {
        return question;
    }

    public String getValue() {
        return value;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
