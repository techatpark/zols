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

    /**
     * declares variable message.
     */
    private final String message;
    /**
     * declares variable jsonData.
     */
    private final Map<String, Object> jsonData;

    /**
     * Instantiates a new Json schema constraint violation.
     *
     * @param theMessage  the message
     * @param theJsonData the json data
     */
    public JsonSchemaConstraintViolation(final String theMessage,
                               final Map<String, Object> theJsonData) {
        this.message = theMessage;
        this.jsonData = theJsonData;
    }

    /**
     * gets the message.
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * gets the message template.
     *
     * @return message
     */
    @Override
    public String getMessageTemplate() {
        return message;
    }

    /**
     * gets the jsonData.
     *
     * @return jsonData
     */
    @Override
    public Object getRootBean() {
        return jsonData;
    }

    /**
     * gets the RootBean Class.
     *
     * @return map of class
     */
    @Override
    public Class getRootBeanClass() {
        return Map.class;
    }

    /**
     * gets the LeafBean.
     *
     */
    @Override
    public Object getLeafBean() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the ExecutableParameters of an Object.
     */
    @Override
    public Object[] getExecutableParameters() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the Executable ReturnValue of an Object.
     */
    @Override
    public Object getExecutableReturnValue() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the PropertyPath.
     */
    @Override
    public Path getPropertyPath() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the Invalid Value.
     */
    @Override
    public Object getInvalidValue() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the Constraint Descriptor.
     */
    @Override
    public ConstraintDescriptor getConstraintDescriptor() {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the unwrap Object.
     */
    @Override
    public Object unwrap(final Class type) {
        throw new UnsupportedOperationException(
                "Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

}
