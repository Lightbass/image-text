package com.fnoz.dto;

public class DataDTO {

    private String text;

    public DataDTO(String text) {
        this.text = text;
    }

    public DataDTO() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
