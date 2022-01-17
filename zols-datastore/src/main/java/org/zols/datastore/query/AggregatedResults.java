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

    private List<Map<String, Object>> buckets;
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
     * @param buckets the buckets
     */
    public void setBuckets(final List<Map<String, Object>> buckets) {
        this.buckets = buckets;
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
     *
     * @param page the page
     */
    public void setPage(final Page<Map<String, Object>> page) {
        this.page = page;
    }

}
