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

    private List<Integer> prepareIssuesIds(String param, Date date, String projectName) throws RedmineException {
        Params params = new Params()
                .add(param, RedmineUtils.parseDateToAfter(date))
                .add("project_id", projectName);
        return manager.getIssueManager().getIssues(params)
                .getResults()
                .stream()
                .map(Issue::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void checkForNewIssues(RedmineEndpoint endpoint) {
        Date created = endpoint.getLastCreatedOnDate();
        String projectId = endpoint.getProjectId();
        manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        try {
            List<Issue> issues = getIssuesAfterDate(created, projectId);
            if (!issues.isEmpty()) {
                RedmineEventHandler handler = new RedmineNewIssueHandler();
                issues.stream().map(x -> new Tuple<>(getUser(x.getId()), x))
                .forEach(x -> handler.handle(x.a, x.b, endpoint.getCompanyId()));
                issues.get(issues.size()).getCreatedOn();
            }
            issues.sort((Comparator.comparing(Issue::getCreatedOn)));
            Date lastCreatedOn = issues.get(issues.size()).getCreatedOn();
            redmineEndpointDAO.updateCreatedOn(endpoint.getCompanyId(), projectId, lastCreatedOn);
        } catch (RedmineException re) {
            //do some stuff
            logger.debug("Failed when getting issues created after date: {} from project with id: {}", created, projectId);
            re.printStackTrace();
        }
    }

    @Override
    public void checkForIssuesUpdates(RedmineEndpoint endpoint) {
        Date updated = endpoint.getLastUpdatedOnDate();
        String projectId = endpoint.getProjectId();
        Long companyId = endpoint.getCompanyId();
        try {
            List<Issue> issues = getIssuesUpdatedAfterDate(updated, projectId);
            if (!issues.isEmpty()) {
                RedmineEventHandler handler = new RedmineUpdateIssueHandler();
                issues.stream().map(x -> new Tuple<>(getUser(x.getId()), x))
                        .forEach(x -> handler.handle(x.a, x.b, companyId));
                redmineEndpointDAO.updateUpdatedOn(endpoint.getCompanyId(), endpoint.getProjectId(),
                        issues.get(issues.size()).getUpdatedOn());
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
/*

        CaseObject object = event.getCaseObject();
        ExternalCaseAppData appData = externalCaseAppDAO.get(object.getId());
*/

//        logger.debug("redmine, case-comment event, case {}, comment #{}", object.getExtId(), event.getCaseComment().getId());
//        ServiceInstance instance = serviceInstanceRegistry.find(event.getCaseObject());
/*
        if (instance == null) {
            logger.debug("no handler instance found for case {}", object.getExtId());
            return;
        }*/

        BackchannelEventHandler handler = new BackchannelUpdateIssueHandler();

        try {
            handler.handle(event);
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

    private RedmineManager manager;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RedmineServiceImpl.class);
}
