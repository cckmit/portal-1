package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.api.RedmineIssuePriority;
import ru.protei.portal.redmine.api.RedmineStatus;
import ru.protei.portal.redmine.service.RedmineService;

public final class BackchannelUpdateIssueHandler implements BackchannelEventHandler {

    @Override
    public void handle(AssembledCaseEvent event) {
        final long caseId = event.getCaseObject().getId();
        final int issueId = Integer.parseInt(externalCaseAppDAO.get(caseId).getExtAppCaseId());
        final String projectId = externalCaseAppDAO.get(caseId).getExtAppData();
        final long companyId = event.getCaseObject().getInitiatorCompanyId();

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

        // Attachment adding through rest-api requires uploading file to redmine server,
        // therefore, now it is impossible.
        // http://www.redmine.org/projects/redmine/wiki/Rest_api#Attaching-files
        //updateAttachments(issue, event.getAddedAttachments(), endpoint);
        updateComments(issue, event.getCaseComment(), endpoint);
        updateIssueProps(issue, event.getCaseObject());

        try {
            service.updateIssue(issue, endpoint);
        } catch (RedmineException e) {
            logger.debug("Failed to update issue with id {}", issue.getId());
            e.printStackTrace();
        }
    }

    private void updateIssueProps(Issue issue, CaseObject object) {
        final RedmineStatus status = RedmineStatus.getByCaseState(object.getState());
        if (status != null)
            issue.setStatusId(status.getRedmineCode());
        final RedmineIssuePriority priority = RedmineIssuePriority.find(En_ImportanceLevel.find(object.getImpLevel()));
        if (priority != null)
            issue.setPriorityId(priority.getRedminePriorityLevel());
        issue.setProjectId(Integer.valueOf(externalCaseAppDAO.get(object.getId()).getExtAppData()));
        issue.setDescription(object.getInfo());
        issue.setSubject(object.getName());
    }

    /*private void updateAttachments(Issue issue, Collection<Attachment> attachments, RedmineEndpoint endpoint) {
        attachments.forEach(x -> toRedmineAttachment(issue.getId(), x, endpoint));
    }*/

    /*private void toRedmineAttachment(int issueId, Attachment attachment, RedmineEndpoint endpoint) {
        try {
            File myDude = new File(new URI(attachment.getExtLink()));
            service.uploadMyDude(issueId, myDude, attachment.getMimeType(), endpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (RedmineException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void updateComments(Issue issue, CaseComment comment, RedmineEndpoint endpoint) {
        if (comment != null && !comment.getText().isEmpty()) {
            issue.setNotes("PROTEI: " + comment.getAuthor().getDisplayName() + ": " + comment.getText());
        }
    }

    /*private User personToUser(Person person, RedmineEndpoint endpoint) {
        String email = person.getContactInfo().getItems(En_ContactItemType.EMAIL).toString();
        try {
            return service.findUser(person, endpoint);
        } catch (RedmineException e) {
            logger.debug("Unable to find user; going to create a new one");
        }
        User user = UserFactory.create();
        user.setLogin("guest");
        user.setFirstName(person.getFirstName());
        user.setLastName(person.getLastName());
        user.setFullName(person.getDisplayName());
        user.setMail(email);
        return user;
    }*/

    @Autowired
    private RedmineEndpointDAO endpointDAO;

    @Autowired
    private RedmineService service;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    private static final Logger logger = LoggerFactory.getLogger(BackchannelUpdateIssueHandler.class);
}
