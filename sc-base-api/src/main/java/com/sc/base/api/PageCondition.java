package com.sc.base.api;


import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PageCondition implements Serializable {
    @NotNull
    @Min(1L)
    private int pageIndex;
    @NotNull
    @Min(0L)
    private int pageSize;
    private Integer sortBy;
    private SortDirection direction;

    public PageCondition() {
        this.direction = SortDirection.ASC;
    }

    public PageCondition(int pageIndex, int pageSize) {
        this.direction = SortDirection.ASC;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public PageCondition(Integer sortBy, SortDirection direction) {
        this.direction = SortDirection.ASC;
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public PageCondition(int pageIndex, int pageSize, Integer sortBy, SortDirection direction) {
        this.direction = SortDirection.ASC;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.direction = direction;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getSortBy() {
        return this.sortBy;
    }

    public void setSortBy(Integer sortBy) {
        this.sortBy = sortBy;
    }

    public SortDirection getDirection() {
        return this.direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }
}