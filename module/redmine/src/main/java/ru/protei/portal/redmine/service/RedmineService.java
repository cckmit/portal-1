package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.util.Collection;
import java.util.List;

public interface RedmineService {
    Issue getIssueById(int id, RedmineEndpoint endpoint);

    void checkForNewIssues(RedmineEndpoint endpoint);

    void checkForUpdatedIssues(RedmineEndpoint endpoint);

    void updateIssue(Issue issue, RedmineEndpoint endpoint) throws RedmineException;

    void updateCreationDateAttachments(RedmineEndpoint endpoint);

    void updateAttachmentsByCaseId(Long caseId);

    void updateCaseObjectById(Long caseId);

    void createCaseObjectByIssue(int issueId, long companyId, String projectId);

    @EventListener
    void onAssembledCaseEvent(AssembledCaseEvent event);

    List<com.taskadapter.redmineapi.bean.Attachment> uploadAttachment(Collection<Attachment> attachment, RedmineEndpoint endpoint);

    User findUser(Person person, RedmineEndpoint endpoint) throws RedmineException;
}
