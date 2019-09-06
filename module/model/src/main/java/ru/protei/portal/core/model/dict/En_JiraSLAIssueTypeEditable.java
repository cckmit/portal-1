package ru.protei.portal.core.model.dict;

import java.util.Objects;

/**
 * Список IssueType, для которых доступно обновление Severity
 */
public enum En_JiraSLAIssueTypeEditable {
    SERVICE("Service"),
    ;

    private String issueType;

    En_JiraSLAIssueTypeEditable(String issueType) {
        this.issueType = issueType;
    }

    public String getIssueType() {
        return issueType;
    }

    public static En_JiraSLAIssueTypeEditable forIssueType(String issueType) {
        for (En_JiraSLAIssueTypeEditable item : values()) {
            if (Objects.equals(item.getIssueType(), issueType)) {
                return item;
            }
        }
        return null;
    }
}
