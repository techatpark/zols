/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.query;

import java.util.List;
import java.util.Map;

public class AggregatedResults {

    private List<Map<String,Object>> buckets;
    private Page<Map<String, Object>> page;

    public List<Map<String, Object>> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Map<String, Object>> buckets) {
        this.buckets = buckets;
    }

    public Page<Map<String, Object>> getPage() {
        return page;
    }

    public void setPage(Page<Map<String, Object>> page) {
        this.page = page;
    }

}
