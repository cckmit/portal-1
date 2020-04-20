package ru.protei.portal.core.utils;

import ru.protei.portal.core.model.ent.ExternalCaseAppData;

import java.util.regex.Pattern;

public class JiraUtils {

    public static JiraIssueData convert(ExternalCaseAppData appData) {
        return new JiraIssueData(extractEndpointId(appData), extractIssueKey(appData), extractIssueProjectId(appData));
    }

    public static String extractIssueKey(String ourStoredId) {
        return ourStoredId.substring(ourStoredId.indexOf('_') + 1);
    }

    public static String extractIssueKey(ExternalCaseAppData appData) {
        return extractIssueKey(appData.getExtAppCaseId());
    }

    public static String extractIssueProjectId(ExternalCaseAppData appData) {
        return appData.getExtAppData();
    }

    public static long extractEndpointId(ExternalCaseAppData appData) {
        return Long.parseLong(appData.getExtAppCaseId().substring(0, appData.getExtAppCaseId().indexOf('_')), 10);
    }

    public static class JiraIssueData {

        public long endpointId;
        public String key;
        public String projectId;

        public JiraIssueData(long endpointId, String key, String projectId) {
            this.endpointId = endpointId;
            this.key = key;
            this.projectId = projectId;
        }
    }

    public static Pattern getJiraImagePattern() {
        return Pattern.compile("\\![^\\!]*\\!");    // !*!
    }

    public static class ImageNode {
        public String link;
        public String alt;
    }

    static public ImageNode parseImageNode(String imageString) {
        ImageNode imageNode = new ImageNode();
        String[] split = imageString.split("\\|");
        for (String part : split) {
            if (!part.contains("=")) {
                imageNode.link = part;
            } else {
                String[] split1 = part.split("=");
                switch (split1[0].toUpperCase().trim()) {
                    case "ALT" : imageNode.alt = split1[1]; break;
                }
            }

        }
        return imageNode;
    }
}
