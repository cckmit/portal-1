package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import protei.utils.common.Tuple;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.handlers.*;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RedmineServiceImpl implements RedmineService {

    private List<Issue> getIssuesAfterDate(String date, String projectName) throws RedmineException {
        List<Integer> ids = prepareIssuesIds("created_on", date, projectName);
        return requestIssues(ids);
    }

    private List<Issue> getIssuesUpdatedAfterDate(String date, String projectName) throws RedmineException {
        List<Integer> ids = prepareIssuesIds("updated_on", date, projectName);
        return requestIssues(ids);
    }

    @Override
    public Issue getIssueById(int id) {
        try {
            return manager.getIssueManager().getIssueById(id, Include.journals, Include.attachments, Include.watchers);
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

    private List<Integer> prepareIssuesIds(String param, String date, String projectName) throws RedmineException {
        Params params = new Params()
                .add(param, date)
                .add("project_id", projectName);
        return manager.getIssueManager().getIssues(params)
                .getResults()
                .stream()
                .map(Issue::getId)
                .collect(Collectors.toList());
    }

    // If date is not updated, we increment it manually by 1 min
    // in order to avoid querying same issues multiple times
    private Date checkDate(Date date, Date lastDate) {
        if (date.equals(lastDate)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastDate);
            calendar.add(Calendar.MINUTE, 1);
            lastDate = calendar.getTime();
        }
        return lastDate;
    }

    @Override
    public void checkForNewIssues(RedmineEndpoint endpoint) {
        String created = RedmineUtils.parseDateToAfter(endpoint.getLastCreatedOnDate());
        String projectId = endpoint.getProjectId();
        manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        try {
            List<Issue> issues = getIssuesAfterDate(created, projectId);
            if (!issues.isEmpty()) {
                issues.stream().map(x -> new Tuple<>(getUser(x.getAuthorId()), x))
                        .forEach(x -> handler.handle(x.a, x.b, endpoint.getCompanyId()));
                issues.get(issues.size()).getCreatedOn();
            }
            issues.sort((Comparator.comparing(Issue::getCreatedOn)));
            Date lastCreatedOn = checkDate(endpoint.getLastCreatedOnDate(),
                    issues.get(issues.size() - 1).getCreatedOn());
            redmineEndpointDAO.updateCreatedOn(endpoint.getCompanyId(), projectId, lastCreatedOn);
        } catch (RedmineException re) {
            //do some stuff
            logger.debug("Failed when getting issues created after date: {} from project with id: {}", created, projectId);
            re.printStackTrace();
        }
    }

    @Override
    public void checkForIssuesUpdates(RedmineEndpoint endpoint) {
        String updated = RedmineUtils.parseDateToAfter(endpoint.getLastUpdatedOnDate());
        String projectId = endpoint.getProjectId();
        Long companyId = endpoint.getCompanyId();
        manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        try {
            List<Issue> issues = getIssuesUpdatedAfterDate(updated, projectId);
            if (!issues.isEmpty()) {
                issues.stream().map(x -> new Tuple<>(getUser(x.getId()), x))
                        .forEach(x -> updateHandler.handle(x.a, x.b, companyId));
                Date lastUpdatedOn = checkDate(endpoint.getLastUpdatedOnDate(),
                        issues.get(issues.size() - 1).getUpdatedOn());
                redmineEndpointDAO.updateUpdatedOn(endpoint.getCompanyId(), endpoint.getProjectId(), lastUpdatedOn);
            }
        } catch (RedmineException re) {
            //something
            logger.debug("Failed when getting issues updated after date {} from project {}", updated, projectId);
            re.printStackTrace();
        }
    }

    @Override
    public void updateIssue(Issue issue) throws RedmineException {
        manager.getIssueManager().update(issue);
    }

    @Override
    @EventListener
    public void onAssembledCaseEvent(AssembledCaseEvent event) {
        if (event.getServiceModule() == ServiceModule.REDMINE) {
            logger.debug("skip handle self-published event for {}", event.getCaseObject().getExtId());
            return;
        }
        try {
            backchannelUpdateIssueHandler.handle(event);
            logger.debug("case-object event handled for case {}", event.getCaseObject().getExtId());
        } catch (Exception e) {
            logger.debug("error while handling event for case {}", event.getCaseObject().getExtId(), e);
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
    private RedmineEndpointDAO redmineEndpointDAO;

    @Autowired
    private RedmineNewIssueHandler handler;

    @Autowired
    private RedmineUpdateIssueHandler updateHandler;

    @Autowired
    private BackchannelUpdateIssueHandler backchannelUpdateIssueHandler;

    private RedmineManager manager;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RedmineServiceImpl.class);
}
