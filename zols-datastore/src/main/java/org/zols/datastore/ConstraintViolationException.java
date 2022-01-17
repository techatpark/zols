/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author sathish
 */
public class ConstraintViolationException extends DataStoreException {

    /**
     * violations.
     */
    private final Set<ConstraintViolation> violations;

    /**
     * object.
     */
    private final Object object;

    /**
     * this is the constructor.
     * @param anObject an Object
     * @param anViolations anViolations
     * @param <T>
     */
    public <T> ConstraintViolationException(final T anObject,
                          final Set<ConstraintViolation> anViolations) {
        super("Bean validation failed for " + anObject.getClass().getName());
        this.object = anObject;
        this.violations = anViolations;
    }

    /**
     * sets violations.
     * @return violations
     */
    public final Set<ConstraintViolation> getViolations() {
        return violations;
    }

    /**
     * gets object.
     *
     * @return object
     */
    public Object getObject() {
        return object;
    }


}
