package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import protei.utils.common.Tuple;
import ru.protei.portal.redmine.config.RedmineProjectConfig;
import ru.protei.portal.redmine.factories.RedmineHandlerFactory;
import ru.protei.portal.redmine.handlers.RedmineEventHandler;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.redmine.api.RedmineRequestFilters.CREATED_ON;

public class RedmineServiceImpl implements RedmineService {

    private List<Issue> getIssuesAfterDate(Date date, String projectId) throws RedmineException {
        List<Integer> ids = prepareIssuesIds("created_on", date, projectId);
        return requestIssues(ids);
    }

    private List<Issue> getIssuesUpdatedAfterDate(Date date, String projectId) throws RedmineException {
        List<Integer> ids = prepareIssuesIds("updated_on", date, projectId);
        return requestIssues(ids);
    }

    private Issue getIssueById(int id) {
        try {
            return issueManager.getIssueById(id, Include.journals, Include.attachments, Include.watchers);
        } catch (RedmineException e) {
            logger.debug("Get exception while trying to get issue with id {}", id);
            return null;
        }
    }

    private List<Issue> requestIssues(List<Integer> ids) {
        return ids.stream()
                .map(this::getIssueById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Integer> prepareIssuesIds(String param, Date date, String projectId) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToAfter(date))
                .add("project_id", projectId);
        return issueManager.getIssues(params)
                .getResults()
                .stream()
                .map(Issue::getId)
                .collect(Collectors.toList());
    }

    /*private List<Issue> getIssuesBeforeDate(Date date) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToBefore(date));
        return manager.getIssueManager().getIssues(params).getResults();
    }

    private List<Issue> getIssuesInDateRange(Date start, Date end) throws RedmineException {
        Params params = new Params()
                .add(CREATED_ON.name(), RedmineUtils.parseDateToRange(start, end));
        return manager.getIssueManager().getIssues(params).getResults();
    }*/

    @Override
    public void checkForNewIssues(RedmineProjectConfig config) {
        Date created = config.getLastCreatedIssueDate();
        String projectId = String.valueOf(config.getProjectId());
        try {
            List<Issue> issues = getIssuesAfterDate(created, projectId);
            if (!issues.isEmpty()) {
                RedmineEventHandler handler = redmineHandlerFactory.createHandler();
                issues.stream().map(x -> new Tuple<>(getUser(x.getId()), x))
                .forEach(x -> handler.handle(x.a, x.b));
                issues.get(issues.size()).getCreatedOn();
            }
        } catch (RedmineException re) {
            //do some stuff
            logger.debug("Failed when getting issues created after date {} from project with id {}", created, projectId);
            re.printStackTrace();
        }
    }

    @Override
    public void checkForIssuesUpdates(RedmineProjectConfig config) {
        Date updated = config.getLastUpdatedIssueDate();
        String projectId = String.valueOf(config.getProjectId());
        try {
            List<Issue> issues = getIssuesUpdatedAfterDate(updated, projectId);
            if (!issues.isEmpty()) {
                RedmineEventHandler handler = redmineHandlerFactory.createHandler();
                issues.stream().map(x -> new Tuple<>(getUser(x.getId()), x))
                        .forEach(x -> handler.handle(x.a, x.b));
                issues.get(issues.size()).getCreatedOn();
            }
        } catch (RedmineException re) {
            //something
            logger.debug("Failed when getting issues updated after date {} from project with id {}", updated, projectId);
            re.printStackTrace();
        }
    }

    private User getUser(int id) {
        try {
            return manager.getUserManager().getUserById(id);
        } catch (RedmineException e) {
            logger.debug("User with id {} not found", id);
            return null;
        }
    }

    @Autowired
    RedmineHandlerFactory redmineHandlerFactory;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RedmineServiceImpl.class);

    //Should be read from config
    private final String apiKey = "";
    private final String uri = "";


    private final RedmineManager manager = RedmineManagerFactory.createWithApiKey(uri, apiKey);
    private final IssueManager issueManager = manager.getIssueManager();
}
