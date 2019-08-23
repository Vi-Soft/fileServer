package com.visoft.file.service.dto;

public enum Type {
    DEFAULT("Default"),
    CHECKLIST("Checklist");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equalsName(String otherValue) {
        return value.equals(otherValue);
    }

    public String toString() {
        return this.value;
    }
}