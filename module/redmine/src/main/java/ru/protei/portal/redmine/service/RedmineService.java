package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.config.RedmineProjectConfig;

import java.util.Date;
import java.util.List;

public interface RedmineService {
    void checkForNewIssues(RedmineEndpoint endpoint);

    void checkForIssuesUpdates(RedmineEndpoint endpoint);

    @EventListener
    void onAssembledCaseEvent(AssembledCaseEvent event);
}
