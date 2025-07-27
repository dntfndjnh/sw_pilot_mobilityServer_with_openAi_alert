package com.sw_pilot.mobility_server.domain;

public class ReportItem {
    private String name;
    private String description;
    private String value;

    public ReportItem(String name, String description, String value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getValue() {
        return value;
}
}
