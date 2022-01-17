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

/**
 * @author sathish
 */
public class SpringConverter {

    public static Page getPage(final org.zols.datastore.query.Page page,
                               final Pageable pageable) {
        return (page == null) ? null :
                new PageImpl<>(page.getContent(), pageable, page.getTotal());
    }

    public static SpringAggregatedResults getAggregatedResults(
            final AggregatedResults aggregatedResults, final Pageable pageable) {
        return aggregatedResults == null ? null :
                new SpringAggregatedResults(aggregatedResults, pageable);
    }

}
