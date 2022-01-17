/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.violations;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Map;

/**
 * The type Json schema constraint violation.
 *
 * @author WZ07
 */
public class JsonSchemaConstraintViolation implements ConstraintViolation {

    private final String message;

    private final Map<String, Object> jsonData;

    /**
     * Instantiates a new Json schema constraint violation.
     *
     * @param message  the message
     * @param jsonData the json data
     */
    public JsonSchemaConstraintViolation(final String message,
                                         final Map<String, Object> jsonData) {
        this.message = message;
        this.jsonData = jsonData;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return message;
    }

    @Override
    public Object getRootBean() {
        return jsonData;
    }

    @Override
    public Class getRootBeanClass() {
        return Map.class;
    }

    @Override
    public Object getLeafBean() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] getExecutableParameters() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getExecutableReturnValue() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Path getPropertyPath() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getInvalidValue() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ConstraintDescriptor getConstraintDescriptor() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object unwrap(final Class type) {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

}
