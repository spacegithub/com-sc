package com.sc.base.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PageRequest extends BaseRequest {
    @Valid
    @NotNull
    private PageCondition page;

    public PageRequest() {
    }

    public PageCondition getPage() {
        return this.page;
    }

    public void setPage(PageCondition page) {
        this.page = page;
    }
}
