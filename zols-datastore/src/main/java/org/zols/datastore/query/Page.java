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

    /**
     * HASH.
     */
    public static final int HASH = 5;
    /**
     * INT.
     */
    public static final int INT = 83;
    /**
     * declares variable pageNumber.
     */
    private final Integer pageNumber;
    /**
     * declares variable pageSize.
     */
    private final Integer pageSize;
    /**
     * declares variable total.
     */
    private final Long total;
    /**
     * declares variable content.
     */
    private final List<T> content;

    /**
     * Instantiates a new Page.
     *
     * @param thePageNumber the PageNumber
     * @param thePageSize the PageSize
     * @param theTotal the Total
     * @param theContent the Content
     */
    public Page(final Integer thePageNumber, final Integer thePageSize,
                final Long theTotal,
                final List<T> theContent) {
        this.pageNumber = thePageNumber;
        this.pageSize = thePageSize;
        this.total = theTotal;
        this.content = theContent;
    }

    /**
     * gets the pageNumber.
     *
     * @return pageNumber
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * gets the pageSize.
     *
     * @return pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * gets the total.
     *
     * @return total
     */
    public Long getTotal() {
        return total;
    }

    /**
     * gets the content.
     *
     * @return content
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * HASH.
     * INT.
     * @return hash
     */
    @Override
    public int hashCode() {
        int hash = HASH;
        hash = INT * hash + Objects.hashCode(this.pageNumber);
        hash = INT * hash + Objects.hashCode(this.pageSize);
        hash = INT * hash + Objects.hashCode(this.total);
        hash = INT * hash + Objects.hashCode(this.content);
        return hash;
    }

    /**
     * Chcks equality.
     * @param obj
     * @return flag
     */
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
