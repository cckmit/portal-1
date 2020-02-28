package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

public interface ForwardChannelEventHandler {
    void compareAndUpdate( User user, Issue issue, RedmineEndpoint endpoint );

    CaseObject createCaseObject( User user, Issue issue, RedmineEndpoint endpoint );
}
