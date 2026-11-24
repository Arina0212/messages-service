package ru.urfu.messages.dto;

public class UpdateMessageRequest {

    private String text;

    public UpdateMessageRequest() {
    }

    public UpdateMessageRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
