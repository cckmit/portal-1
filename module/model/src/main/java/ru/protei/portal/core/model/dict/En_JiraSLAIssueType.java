package ru.protei.portal.core.model.dict;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum En_JiraSLAIssueType {
    SERVICE("Service", false, true),
    ERROR("Error", true, false),
    ;

    private String issueType;
    private boolean byJira;
    private boolean byPortal;

    En_JiraSLAIssueType(String issueType, boolean byJira, boolean byPortal) {
        this.issueType = issueType;
        this.byJira = byJira;
        this.byPortal = byPortal;
    }

    public String getIssueType() {
        return issueType;
    }

    public static En_JiraSLAIssueType forIssueType(String issueType) {
        for (En_JiraSLAIssueType item : values()) {
            if (Objects.equals(item.getIssueType(), issueType)) {
                return item;
            }
        }
        return null;
    }

    public static List<En_JiraSLAIssueType> byJira() {
        return Arrays.stream(values())
            .filter(it -> it.byJira)
            .collect(Collectors.toList());
    }

    public static List<En_JiraSLAIssueType> byPortal() {
        return Arrays.stream(values())
            .filter(it -> it.byPortal)
            .collect(Collectors.toList());
    }
}
