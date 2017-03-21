/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web.util;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public class AggregatedResults {

    private List<Map<String,Object>> buckets;
    private Page<List> page;

    public List<Map<String, Object>> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Map<String, Object>> buckets) {
        this.buckets = buckets;
    }

    public Page<List> getPage() {
        return page;
    }

    public void setPage(Page<List> page) {
        this.page = page;
    }

}
