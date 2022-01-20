/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.zols.datastore.query.AggregatedResults;

import java.util.List;
import java.util.Map;

public class SpringAggregatedResults {

    /**
     * declares variable list of buckets.
     */
    private final List<Map<String, Object>> buckets;
    /**
     * declares variable page.
     */
    private final Page<Map<String, Object>> page;

    /**
     * @param pageable the pageable
     * @param aggregatedResults the aggregatedResults
     */
    public SpringAggregatedResults(final AggregatedResults aggregatedResults,
                                   final Pageable pageable) {
        buckets = aggregatedResults.getBuckets();
        page = (aggregatedResults.getPage() == null) ? null
                : new PageImpl<>(aggregatedResults.getPage().getContent(),
                pageable, aggregatedResults.getPage().getTotal());
    }

    /**
     * gets the buckets.
     *
     * @return buckets
     */
    public List<Map<String, Object>> getBuckets() {
        return buckets;
    }

    /**
     * gets the page.
     *
     * @return page
     */
    public Page<Map<String, Object>> getPage() {
        return page;
    }


}