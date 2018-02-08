package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.redmine.config.RedmineProjectConfig;
import ru.protei.portal.redmine.factories.RedmineHandlerFactory;
import ru.protei.portal.redmine.handlers.RedmineEventHandler;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.redmine.api.RedmineRequestFilters.CREATED_ON;

public class RedmineServiceImpl implements RedmineService {
    private Issue getIssueById(int id) throws RedmineException {
        return manager.getIssueManager().getIssueById(id);
    }

    private List<Issue> getIssuesAfterDate(Date date, String projectId) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToAfter(date))
                .add("project_id", projectId);
        return manager.getIssueManager().getIssues(params).getResults();
    }

    private List<Issue> getIssuesBeforeDate(Date date) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToBefore(date));
        return manager.getIssueManager().getIssues(params).getResults();
    }

    private List<Issue> getIssuesInDateRange(Date start, Date end) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToRange(start, end));
        return manager.getIssueManager().getIssues(params).getResults();
    }

    @Override
    public void checkForNewIssues(RedmineProjectConfig config) {
        Date created = config.getLastCreatedIssueDate();
        String projectId = String.valueOf(config.getProjectId());
        try {
            List<Issue> issues = getIssuesAfterDate(created, projectId);
            if (!issues.isEmpty()) {
                RedmineEventHandler handler = redmineHandlerFactory.createHandler();
                issues.get(issues.size()).getCreatedOn();
                issues.forEach(handler::handle);
            }
        } catch (RedmineException re) {
            //do some smartass stuff
        }
    }

    @Override
    public void checkForIssuesUpdates() {

    }

    @Autowired
    RedmineHandlerFactory redmineHandlerFactory;

    private final String apiKey = "";
    private final String uri = "";
    private final RedmineManager manager = RedmineManagerFactory.createWithApiKey(uri, apiKey);
}
