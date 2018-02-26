package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

public interface RedmineService {
    Issue getIssueById(int id);

    void checkForNewIssues(RedmineEndpoint endpoint);

    void checkForIssuesUpdates(RedmineEndpoint endpoint);

    void updateIssue(Issue issue) throws RedmineException;

    @EventListener
    void onAssembledCaseEvent(AssembledCaseEvent event);
}
