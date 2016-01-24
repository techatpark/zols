/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore;

import java.util.List;

/**
 *
 * @author sathish
 * @param <T> type of the content
 */
public class Page<T> {

    private final Integer pageNumber;
    private final Integer pageSize;
    private final Long total;
    private final List<T> content;

    public Page(Integer pageNumber, Integer pageSize, Long total, List<T> content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.total = total;
        this.content = content;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public List<T> getContent() {
        return content;
    }

}
