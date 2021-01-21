package ru.protei.portal.core.model.dict;

import java.util.Arrays;
import java.util.List;

public enum En_ReportAdditionalParamType {
    IMPORTANCE_HISTORY,
    DESCRIPTION,
    TAGS,
    LINKED_ISSUES,
    HUMAN_READABLE,

    PROJECT_LIMIT_COMMENTS;

    public static List<En_ReportAdditionalParamType> getCaseObjectType() {
        return Arrays.asList(
                IMPORTANCE_HISTORY,
                DESCRIPTION,
                TAGS,
                LINKED_ISSUES,
                HUMAN_READABLE
        );
    }

    public static List<En_ReportAdditionalParamType> getReportType() {
        return Arrays.asList(
                PROJECT_LIMIT_COMMENTS
        );
    }
}
