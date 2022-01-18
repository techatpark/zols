/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.query;

import java.util.List;
import java.util.Objects;

/**
 * @param <T> type of the content
 * @author sathish
 */
public class Page<T> {

    private final Integer pageNumber;
    private final Integer pageSize;
    private final Long total;
    private final List<T> content;

    public Page(final Integer thePageNumber, final Integer thePageSize,
                final Long theTotal,
                final List<T> theContent) {
        this.pageNumber = thePageNumber;
        this.pageSize = thePageSize;
        this.total = theTotal;
        this.content = theContent;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.pageNumber);
        hash = 89 * hash + Objects.hashCode(this.pageSize);
        hash = 89 * hash + Objects.hashCode(this.total);
        hash = 89 * hash + Objects.hashCode(this.content);
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
        final Page<?> other = (Page<?>) obj;
        if (!Objects.equals(this.pageNumber, other.pageNumber)) {
            return false;
        }
        if (!Objects.equals(this.pageSize, other.pageSize)) {
            return false;
        }
        if (!Objects.equals(this.total, other.total)) {
            return false;
        }
        return Objects.equals(this.content, other.content);
    }


}
