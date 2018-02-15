package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import protei.utils.common.Tuple;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.factories.RedmineHandlerFactory;
import ru.protei.portal.redmine.handlers.RedmineEventHandler;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RedmineServiceImpl implements RedmineService {

    private List<Issue> getIssuesAfterDate(Date date, String projectName) throws RedmineException {
        List<Integer> ids = prepareIssuesIds("created_on", date, projectName);
        return requestIssues(ids);
    }

    private List<Issue> getIssuesUpdatedAfterDate(Date date, String projectName) throws RedmineException {
        List<Integer> ids = prepareIssuesIds("updated_on", date, projectName);
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

    private List<Integer> prepareIssuesIds(String param, Date date, String projectName) throws RedmineException {
        Params params = new Params()
                .add(param, RedmineUtils.parseDateToAfter(date))
                .add("project_name", projectName);
        return issueManager.getIssues(params)
                .getResults()
                .stream()
                .map(Issue::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void checkForNewIssues(RedmineEndpoint endpoint) {
        Date created = endpoint.getLastCreatedOnDate();
        String projectName = endpoint.getProjectName();
        issueManager = RedmineManagerFactory
                .createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey())
                .getIssueManager();
        try {
            List<Issue> issues = getIssuesAfterDate(created, projectName);
            if (!issues.isEmpty()) {
                RedmineEventHandler handler = redmineHandlerFactory.createHandler();
                issues.stream().map(x -> new Tuple<>(getUser(x.getId()), x))
                .forEach(x -> handler.handle(x.a, x.b));
                issues.get(issues.size()).getCreatedOn();
            }
            issues.sort((Comparator.comparing(Issue::getCreatedOn)));
            Date lastCreatedOn = issues.get(issues.size()).getCreatedOn();
            redmineEndpointDAO.updateCreatedOn(endpoint.getCompanyId(), projectName, lastCreatedOn);
        } catch (RedmineException re) {
            //do some stuff
            logger.debug("Failed when getting issues created after date {} from project {}", created, projectName);
            re.printStackTrace();
        }
    }

    @Override
    public void checkForIssuesUpdates(RedmineEndpoint endpoint) {
        Date updated = endpoint.getLastUpdatedOnDate();
        String projectName = endpoint.getProjectName();
        Long companyId = endpoint.getCompanyId();
        try {
            List<Issue> issues = getIssuesUpdatedAfterDate(updated, projectName);
            if (!issues.isEmpty()) {
                RedmineEventHandler handler = redmineHandlerFactory.createHandler();
                issues.stream().map(x -> new Tuple<>(getUser(x.getId()), x))
                        .forEach(x -> handler.handle(x.a, x.b, companyId));
                issues.get(issues.size()).getCreatedOn();
            }
        } catch (RedmineException re) {
            //something
            logger.debug("Failed when getting issues updated after date {} from project {}", updated, projectName);
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
    private RedmineHandlerFactory redmineHandlerFactory;

    @Autowired
    private RedmineEndpointDAO redmineEndpointDAO;

    private IssueManager issueManager;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RedmineServiceImpl.class);
}
