package ru.protei.portal.core.utils;

import ru.protei.portal.core.model.ent.ExternalCaseAppData;

import java.util.regex.Matcher;
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
        return Pattern.compile("(^|\\s)![^!\\t\\n\\r]*[!]($|\\s)");    // !*!
    }

    public static class ImageNode {
        public String link;
        public String alt;
    }

    static public ImageNode parseImageNode(String imageString) {
        ImageNode imageNode = new ImageNode();
        Pattern p = Pattern.compile("![^!]*!");
        Matcher m = p.matcher(imageString);
        m.find();
        imageString = m.group();
        String[] split = imageString.substring(1, imageString.length()-1).split("\\|");
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

    public static class JiraImageNode {
        public String space;
        public String page;
        public String filename;
        public int oldFilenameStart;
        public int oldFilenameEnd;
        public String alt;
        public int oldAltStart;
        public int oldAltEnd;
    }

    static public JiraImageNode parseJiraImageNode(String originalString) {
        JiraImageNode node = new JiraImageNode();
        if (originalString.length() >= 5 && originalString.charAt(0) != ')') {
            int index = originalString.indexOf(124);
            String resourceString;
            String propertiesString;
            if (index == -1) {
                resourceString = originalString;
                propertiesString = "";
            } else {
                resourceString = originalString.substring(0, index);
                propertiesString = originalString.substring(index + 1);
            }

            if (resourceString.indexOf(58) != -1) {
                node.space = resourceString.substring(0, resourceString.indexOf(58));
                resourceString = resourceString.substring(resourceString.indexOf(58) + 1);
            }

            if (resourceString.indexOf(94) != -1) {
                node.page = resourceString.substring(0, resourceString.indexOf(94));
                resourceString = resourceString.substring(resourceString.indexOf(94) + 1);
            }

            node.filename = resourceString;

            int dot_pos = resourceString.lastIndexOf(".");
            if (dot_pos < 0) {
                return null;
            } else {
                String file_ext = resourceString.substring(dot_pos + 1);
                if (file_ext.length() == 0) {
                    return null;
                } else {
                    if (!file_ext.toLowerCase().matches("jpg|jpeg|png|gif")) {
                        return null;
                    }
                }
            }

            for (String part : propertiesString.split("\\|")) {
                String[] split1 = part.split("=");
                switch (split1[0].toUpperCase().trim()) {
                    case "ALT":
                        node.alt = split1[1];
                        break;
                }
            }
            return node;
        } else {
            return null;
        }
    }
}
