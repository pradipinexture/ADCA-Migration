package com.adc.dxp.polls.application.dto;

public class ResponseDTO<T> {
    private String status;
    private String message;
    private T data;
    private int totalCount;

    public ResponseDTO(String status, String message, T data, int totalCount) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.totalCount = totalCount;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
