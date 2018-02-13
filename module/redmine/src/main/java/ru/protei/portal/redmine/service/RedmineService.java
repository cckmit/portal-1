package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.redmine.config.RedmineProjectConfig;

import java.util.Date;
import java.util.List;

public interface RedmineService {
    void checkForNewIssues(RedmineProjectConfig config);

    void checkForIssuesUpdates(RedmineProjectConfig config);
}
