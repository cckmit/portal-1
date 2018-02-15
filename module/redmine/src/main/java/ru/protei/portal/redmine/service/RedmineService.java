package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.config.RedmineProjectConfig;

import java.util.Date;
import java.util.List;

public interface RedmineService {
    void checkForNewIssues(RedmineEndpoint endpoint);

    void checkForIssuesUpdates(RedmineEndpoint endpoint);
}
