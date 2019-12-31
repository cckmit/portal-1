package ru.protei.portal.core.client.youtrack.api;

public class YoutrackUrlProvider {

    private final String baseUrl;

    public YoutrackUrlProvider(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String issue() {
        return baseUrl + "/issues";
    }

    public String issue(String id) {
        return baseUrl + "/issues/" + id;
    }
}
