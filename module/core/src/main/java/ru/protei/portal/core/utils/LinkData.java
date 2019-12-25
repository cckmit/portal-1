package ru.protei.portal.core.utils;

public class LinkData {
    public LinkData( String url, String linkName ) {
        this.url = url;
        this.linkName = linkName;
    }

    public final String url;
    public final String linkName;

    public String getUrl() {
        return url;
    }

    public String getLinkName() {
        return linkName;
    }
}
