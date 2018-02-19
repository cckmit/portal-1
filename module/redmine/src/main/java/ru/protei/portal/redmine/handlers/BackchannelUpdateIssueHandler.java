package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.redmine.api.RedmineStatus;

public class BackchannelUpdateIssueHandler implements BackchannelEventHandler{
    @Override
    public void handle(AssembledCaseEvent event) {
        Issue issue = new Issue();
        CaseObject obj = event.getCaseObject();
        RedmineStatus status = RedmineStatus.getByCaseState(obj.getState());
        issue.setStatusName(status.getRedmineCode());
        issue.obj.getCaseType();
        obj.getAttachments();
        obj.getImpLevel();
        obj.getName();
    }
}
