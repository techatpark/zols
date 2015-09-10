/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.query;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sathish_ku
 */
public class Query {

    private final List<Filter> filters;

    /**
     * Intialize with default values
     */
    public Query() {
        this.filters = new ArrayList<>();
    }

    /**
     * gets filters from the query
     * @return list of filters
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Adds a Filter to the query
     * @param filter filter to be added
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

}