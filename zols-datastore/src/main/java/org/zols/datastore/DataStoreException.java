/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

/**
 * @author mshind
 */
public class DataStoreException extends Exception {

    /**
     * @param message
     */
    public DataStoreException(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DataStoreException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
