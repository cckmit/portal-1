package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.redmine.service.RedmineService;

public final class RedmineBackChannelHandler implements BackchannelEventHandler {

    @Override
    public void handle(AssembledCaseEvent event) {
        final long caseId = event.getCaseObject().getId();
        final CaseObject actualCaseObject = caseObjectDAO.get(caseId);
        final int issueId = Integer.parseInt(externalCaseAppDAO.get(caseId).getExtAppCaseId());
        final String projectId = externalCaseAppDAO.get(caseId).getExtAppData();
        final long companyId = actualCaseObject.getInitiatorCompanyId();

        final RedmineEndpoint endpoint = endpointDAO.getByCompanyIdAndProjectId(companyId,
                projectId);
        if (endpoint == null) {
            logger.debug("Endpoint was not found for companyId {} and projectId {}", companyId, projectId);
            return;
        }
        final Issue issue = service.getIssueById(issueId, endpoint);
        if (issue == null) {
            logger.debug("Issue with id {} was not found", issueId);
            return;
        }

        updateComments(issue, event.getCaseComment(), endpoint);
        updateIssueProps(issue, actualCaseObject, endpoint);

        try {
            service.updateIssue(issue, endpoint);
        } catch (RedmineException e) {
            logger.debug("Failed to update issue with id {}", issue.getId());
            e.printStackTrace();
        }
    }

    private void updateIssueProps(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        final long priorityMapId = endpoint.getPriorityMapId();

        logger.debug("Trying to get redmine priority level id matching with portal: {}", object.getImpLevel());
        final RedminePriorityMapEntry redminePriority = priorityMapEntryDAO.getByPortalPriorityId(object.getImpLevel(), priorityMapId);
        if (redminePriority != null) {
            logger.debug("Found redmine priority level id: {}", redminePriority.getRedminePriorityId());
            issue.setPriorityId(redminePriority.getRedminePriorityId());
        } else
            logger.debug("Redmine priority level not found");

        final long statusMapId = endpoint.getStatusMapId();
        logger.debug("Trying to get redmine status id matching with portal: {}", object.getStateId());
        final RedmineStatusMapEntry redmineStatus = statusMapEntryDAO.getByRedmineStatusId(object.getStateId(), statusMapId);
        if (redmineStatus != null && object.getState() != En_CaseState.VERIFIED) {
            logger.debug("Found redmine status id: {}", redmineStatus.getRedmineStatusId());
            issue.setStatusId(redmineStatus.getRedmineStatusId());
        } else
            logger.debug("Redmine status not found");

        issue.setProjectId(Integer.valueOf(externalCaseAppDAO.get(object.getId()).getExtAppData()));
        issue.setDescription(object.getInfo());
        issue.setSubject(object.getName());
    }

    private void updateComments(Issue issue, CaseComment comment, RedmineEndpoint endpoint) {
        if (comment != null && !comment.getText().isEmpty()) {
            issue.setNotes("PROTEI: " + comment.getAuthor().getDisplayName() + ": " + comment.getText());
        }
    }

    @Autowired
    private RedmineEndpointDAO endpointDAO;

    @Autowired
    private RedmineService service;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    private static final Logger logger = LoggerFactory.getLogger(RedmineBackChannelHandler.class);
}
