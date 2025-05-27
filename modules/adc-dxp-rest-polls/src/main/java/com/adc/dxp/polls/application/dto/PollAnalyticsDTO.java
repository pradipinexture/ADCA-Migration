package com.adc.dxp.polls.application.dto;

public class PollAnalyticsDTO {
    private String question;
    private int numberOfVotes;

    public PollAnalyticsDTO(String question, int numberOfVotes) {
        this.question = question;
        this.numberOfVotes = numberOfVotes;
    }

    public String getQuestion() {
        return question;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }
}
