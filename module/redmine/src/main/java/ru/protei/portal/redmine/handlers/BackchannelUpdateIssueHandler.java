package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.redmine.api.RedmineStatus;
import ru.protei.portal.redmine.service.RedmineService;

public class BackchannelUpdateIssueHandler implements BackchannelEventHandler {

    @Override
    public void handle(AssembledCaseEvent event) {
        String issueId = externalCaseAppDAO.get(event.getCaseObject().getId()).getExtAppCaseId();
        Issue issue = service.getIssueById(Integer.parseInt(issueId));
        assert issue != null;
        CaseObject obj = event.getCaseObject();
        RedmineStatus status = RedmineStatus.getByCaseState(obj.getState());
        issue.setStatusName(status.getRedmineCode());
        issue.setStatusId(status.getCaseState().getId());
        try {
            service.updateIssue(issue);
        } catch (RedmineException e) {
            logger.debug("Failed to update issue with id {}", issue.getId());
            e.printStackTrace();
        }
    }

    @Autowired
    RedmineService service;

    @Autowired
    ExternalCaseAppDAO externalCaseAppDAO;

    private static final Logger logger = LoggerFactory.getLogger(BackchannelUpdateIssueHandler.class);
}
