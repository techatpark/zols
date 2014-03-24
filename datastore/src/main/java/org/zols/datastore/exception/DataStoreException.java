/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.datastore.exception;

/**
 *
 * @author sathish_ku
 */
public class DataStoreException extends RuntimeException {

	/**
	 * Unique ID for Serialized object
	 */
	private static final long serialVersionUID = 0x401e3ef897c2f404L;

	public DataStoreException(String msg) {
		super(msg);
	}

	public DataStoreException(String msg, Throwable t) {
		super(msg, t);
	}

}
