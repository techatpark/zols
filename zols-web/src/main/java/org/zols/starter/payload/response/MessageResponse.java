package org.zols.starter.payload.response;

public class MessageResponse {

    /**
     * declares variable message.
     */
    private String message;

    /**
     * Instantiates a new MessageResponse.
     *
     * @param anMessage message
     */
    public MessageResponse(final String anMessage) {
        this.message = anMessage;
    }

    /**
     * gets the message.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }
    /**
     * Sets message.
     *
     * @param anMessage  message
     */
    public void setMessage(final String anMessage) {
        this.message = anMessage;
    }
}
