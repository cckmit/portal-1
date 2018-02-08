package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;

public interface RedmineEventHandler {
    void handle(Issue issue);
}
