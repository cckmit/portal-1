package ru.protei.portal.core.utils;

import ru.protei.portal.core.model.ent.ExternalCaseAppData;

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
}
