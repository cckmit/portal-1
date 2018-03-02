package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

public interface RedmineEventHandler {
    void handle(User user, Issue issue, RedmineEndpoint endpoint);
}
