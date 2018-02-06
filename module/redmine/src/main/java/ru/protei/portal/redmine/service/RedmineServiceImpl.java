package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.redmine.api.RedmineRequestFilters.CREATED_ON;

public class RedmineServiceImpl implements RedmineService {
    @Override
    public Issue getIssueById(int id) throws RedmineException {
        return manager.getIssueManager().getIssueById(id);
    }

    @Override
    public List<Issue> getIssuesAfterDate(Date date) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToAfter(date));
        return manager.getIssueManager().getIssues(params).getResults();
    }

    @Override
    public List<Issue> getIssuesBeforeDate(Date date) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToBefore(date));
        return manager.getIssueManager().getIssues(params).getResults();
    }

    @Override
    public List<Issue> getIssuesInDateRange(Date start, Date end) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToRange(start, end));
        return manager.getIssueManager().getIssues(params).getResults();
    }

    @Override
    public void createIssue() {

    }


    private final String apiKey = "";
    private final String uri = "";
    private final RedmineManager manager = RedmineManagerFactory.createWithApiKey(uri, apiKey);
}
