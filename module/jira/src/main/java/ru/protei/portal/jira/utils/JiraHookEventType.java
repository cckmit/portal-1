package ru.protei.portal.jira.utils;

public enum JiraHookEventType {
    COMMENT_CREATED("comment_created"),
    COMMENT_UPDATED("comment_updated"),
    ISSUE_CREATED("jira:issue_created"),
    ISSUE_UPDATED("jira:issue_updated"),
    ISSUE_LINK_CREATED("issuelink_created"),
    ISSUE_LINK_DELETED("issuelink_deleted")
    ;

    JiraHookEventType(String code) {
        this.code = code;
    }

    private String code;


    public String getCode() {
        return code;
    }


    public static JiraHookEventType byCode (String code) {
        if (code == null || code.isEmpty())
            return null;

        for (JiraHookEventType it : JiraHookEventType.values())
            if (it.code.equals(code))
                return it;

        return null;
    }
}

