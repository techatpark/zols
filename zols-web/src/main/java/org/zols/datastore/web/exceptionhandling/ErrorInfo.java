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

    private final String field;
    private final String message;

    public ErrorInfo(final String field, final String message) {
        this.field = field;
        this.message = message;
    }

}
