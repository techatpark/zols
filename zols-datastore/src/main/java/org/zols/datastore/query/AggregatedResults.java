/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.query;

import java.util.List;
import java.util.Map;

/**
 * The type Aggregated results.
 */
public class AggregatedResults {

    /**
     * list of buckets.
     */
    private List<Map<String, Object>> buckets;

    /**
     * list of page.
     */
    private Page<Map<String, Object>> page;

    /**
     * Gets buckets.
     *
     * @return the buckets
     */
    public List<Map<String, Object>> getBuckets() {
        return buckets;
    }

    /**
     * Sets buckets.
     *
     * @param theBuckets the buckets
     */
    public void setBuckets(final List<Map<String, Object>> theBuckets) {
        this.buckets = theBuckets;
    }

    /**
     * Gets page.
     *
     * @return the page
     */
    public Page<Map<String, Object>> getPage() {
        return page;
    }

    /**
     * Sets page.
     * @param thePage the page
     */
    public void setPage(final Page<Map<String, Object>> thePage) {
        this.page = thePage;
    }

}
