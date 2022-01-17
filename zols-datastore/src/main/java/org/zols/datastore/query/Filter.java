/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.query;

import java.util.Objects;

/**
 * @param <T> type of the object for which this filter has to be applied
 * @author sathish_ku
 */
public class Filter<T> {

    private final String name;
    private final Operator operator;
    private final Object value;
    /**
     * Intialize the Filter
     *
     * @param name     name of the filter
     * @param anOperator an operator of the filter
     * @param value    value of the filter
     */
    public Filter(final String name, final Operator anOperator, final T value) {
        this.name = name;
        this.operator = anOperator;
        this.value = value;
    }

    /**
     * Intialize the Filter
     *
     * @param operator operator of the filter
     * @param value    value of the filter
     */
    public Filter(final Operator operator, final T value) {
        this.name = null;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Intialize the Filter
     *
     * @param name     name of the filter
     * @param operator operator of the filter
     * @param value
     */
    public Filter(final String name, final Operator operator, final T... value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Intialize the Filter without value
     *
     * @param name     name of the filter
     * @param operator operator of the filter
     */
    public Filter(final String name, final Operator operator) {
        this.name = name;
        this.operator = operator;
        this.value = null;
    }

    /**
     * gets name of the filter
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets operator of the Filter
     *
     * @return operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * gets value of the Filter
     *
     * @return value object
     */
    public Object getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.operator);
        hash = 83 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Filter<?> other = (Filter<?>) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.operator != other.operator) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }

    /**
     * Describes Operator for Filters
     */
    public enum Operator {

        /**
         * Search Text in Full content of Document
         */
        FULL_TEXT_SEARCH,
        /**
         * Compare with =
         */
        EQUALS,
        /**
         * Compare with =
         */
        NOT_EQUALS,
        /**
         * Compare with
         */
        GREATER_THAN,
        /**
         * Compare with
         */
        GREATER_THAN_EQUALS,
        /**
         * Compare with
         */
        LESSER_THAN,
        /**
         * Compare with
         */
        LESSER_THAN_EQUALS,
        /**
         * Compare with Null value
         */
        IS_NULL,
        /**
         * Check not Null
         */
        IS_NOTNULL,
        /**
         * Exists in given values
         */
        EXISTS_IN,
        /**
         * Not Exists in given values
         */
        NOT_EXISTS_IN,
        /**
         * Checks value in between
         */
        IN_BETWEEN
    }


}
