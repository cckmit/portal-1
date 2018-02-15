package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

public interface RedmineEventHandler {
    void handle(User user, Issue issue, long companyId);
}
