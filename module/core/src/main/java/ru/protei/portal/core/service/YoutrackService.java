package ru.protei.portal.core.service;

import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.WorkItem;

import java.util.List;

public interface YoutrackService {
    List<Issue> getTasks(String ytLogin, String date, DateType dateType);

    Issue getIssueDetails(String issue);

    String getIssueChangesRaw(String issue);

    ChangeResponse getIssueChanges(String issue);

    String getIssueWorkItemsRaw(String issue);

    WorkItem[] getIssueWorkItems(String issue);

    String createIssue(String project, String summary, String description);

    public enum DateType {
        COMPLETE, CREATED, UPDATED, WORK_ITEMS
    }
}
