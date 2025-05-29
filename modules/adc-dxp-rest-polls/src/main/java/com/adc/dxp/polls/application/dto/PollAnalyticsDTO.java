package com.adc.dxp.polls.application.dto;

public class PollAnalyticsDTO {
    private String optionName;
    private int numberOfVotes;

    public PollAnalyticsDTO(String question, int numberOfVotes) {
        this.optionName = question;
        this.numberOfVotes = numberOfVotes;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }
}
