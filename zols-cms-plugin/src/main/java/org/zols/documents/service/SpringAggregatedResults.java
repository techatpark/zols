/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.documents.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.zols.datastore.query.AggregatedResults;

public class SpringAggregatedResults {

    private final List<Map<String,Object>> buckets;
    private final Page<Map<String, Object>> page;

    public SpringAggregatedResults(AggregatedResults aggregatedResults,Pageable pageable) {
        buckets = aggregatedResults.getBuckets();
        page =  (aggregatedResults.getPage() == null) ? null : new PageImpl<>(aggregatedResults.getPage().getContent(), pageable, aggregatedResults.getPage().getTotal());
    }

    public List<Map<String, Object>> getBuckets() {
        return buckets;
    }

    public Page<Map<String, Object>> getPage() {
        return page;
    }
    
    
    

    

}