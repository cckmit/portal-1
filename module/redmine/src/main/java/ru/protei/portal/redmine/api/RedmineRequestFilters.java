package ru.protei.portal.redmine.api;

public enum RedmineRequestFilters {
    CREATED_ON("created_on");

    RedmineRequestFilters(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    private final String filter;
}
