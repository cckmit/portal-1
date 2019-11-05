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

    public static String extractDigitsFromName(String name) {
        Matcher m = pattern.matcher(name);
        return m.matches() ? m.group(1) : null;
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

    private static Pattern pattern = Pattern.compile(".*([0-9]{1,2}).*");
}
