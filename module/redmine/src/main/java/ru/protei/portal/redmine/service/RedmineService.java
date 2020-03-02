package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import org.springframework.context.event.EventListener;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.util.Collection;
import java.util.List;

public interface RedmineService {
    Result<Issue> getIssueById( int id, RedmineEndpoint endpoint);

    void checkForNewIssues(RedmineEndpoint endpoint);

    void checkForUpdatedIssues(RedmineEndpoint endpoint);

    Result<Issue> updateIssue( Issue issue, RedmineEndpoint endpoint);

    @EventListener
    void onAssembledCaseEvent(AssembledCaseEvent event);

    Result<List<com.taskadapter.redmineapi.bean.Attachment>> uploadAttachment( Collection<Attachment> attachment, RedmineEndpoint endpoint);
}
