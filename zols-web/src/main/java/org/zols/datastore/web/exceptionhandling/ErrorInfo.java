/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.exceptionhandling;

/**
 * @author sathish
 */
public class ErrorInfo {

    /**
     * tells the field.
     */
    private final String field;
    /**
     * tells the message.
     */
    private final String message;

    /**
     * this is the constructor.
     * @param anField an field
     * @param anMessage an message
     */
    public ErrorInfo(final String anField, final String anMessage) {
        this.field = anField;
        this.message = anMessage;
    }

}
