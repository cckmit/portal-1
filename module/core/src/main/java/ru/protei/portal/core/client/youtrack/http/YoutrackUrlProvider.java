package ru.protei.portal.core.client.youtrack.http;

public class YoutrackUrlProvider {

    private final String baseUrl;

    public YoutrackUrlProvider(String baseUrl) { this.baseUrl = baseUrl; }

    public String issues() { return baseUrl + "/issues"; }

    public String issue(String issueId) { return baseUrl + "/issues/" + issueId; }

    public String issueAttachments(String issueId) { return baseUrl + "/issues/" + issueId + "/attachments"; }

    public String issueAttachment(String issueId, String attachmentId) { return baseUrl + "/issues/" + issueId + "/attachments/" + attachmentId; }

    public String issueActivities(String issueId) { return baseUrl + "/issues/" + issueId + "/activities"; }

    public String issueActivity(String issueId, String itemId) { return baseUrl + "/issues/" + issueId + "/activities/" + itemId; }

    public String projects() { return baseUrl + "/admin/projects"; }

    public String project(String projectId) { return baseUrl + "/admin/projects/" + projectId; }
}
