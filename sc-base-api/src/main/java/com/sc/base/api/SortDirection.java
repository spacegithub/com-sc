package com.sc.base.api;

import java.io.Serializable;

public enum SortDirection implements Serializable {
    ASC("ASC"),
    DESC("DESC");

    private String code;

    private SortDirection(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return "[" + this.name() + " = " + this.getCode() + "]";
    }
}
