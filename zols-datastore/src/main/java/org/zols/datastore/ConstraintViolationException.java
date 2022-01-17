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
    private final Set<ConstraintViolation> violations;

    private final Object object;

    public <T> ConstraintViolationException(final T object,
                                            final Set<ConstraintViolation> violations) {
        super("Bean validation failed for " + object.getClass().getName());
        this.object = object;
        this.violations = violations;
    }

    public Set<ConstraintViolation> getViolations() {
        return violations;
    }

    public Object getObject() {
        return object;
    }


}
