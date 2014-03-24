/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.domain;

/**
 *
 * @author sathish_ku
 */
public class Criteria {

    public enum Type {
        GREATER_THAN,GREATER_THAN_EQUALS,LESSER_THAN_EQUALS,LESSER_THAN,IS,IS_NULL,IS_NOTNULL
    }

    private final String fieldName;
    private final Type type;
    private final Object value;

    public Criteria(String fieldName, Type type, Object value) {
        this.fieldName = fieldName;
        this.type = type;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
    
    
    
    
    
    

}
