package org.zols.starter.payload.response;

public class MessageResponse {
    private String message;

    public MessageResponse(final String anMessage) {
        this.message = anMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
