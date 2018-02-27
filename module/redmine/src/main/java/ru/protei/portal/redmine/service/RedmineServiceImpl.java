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
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.handlers.*;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RedmineServiceImpl implements RedmineService {

    private List<Issue> getIssuesAfterDate(String date, String projectName, RedmineEndpoint endpoint) throws RedmineException {
        RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        List<Integer> ids = prepareIssuesIds("created_on", date, projectName, manager);
        return requestIssues(ids, endpoint);
    }

    private List<Issue> getIssuesUpdatedAfterDate(String date, String projectName, RedmineEndpoint endpoint) throws RedmineException {
        RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        List<Integer> ids = prepareIssuesIds("updated_on", date, projectName, manager);
        return requestIssues(ids, endpoint);
    }

    @Override
    public Issue getIssueById(int id, RedmineEndpoint endpoint) {
        try {
            RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
            return manager.getIssueManager().getIssueById(id, Include.journals, Include.attachments, Include.watchers);
        } catch (RedmineException e) {
            logger.debug("Get exception while trying to get issue with id {}", id);
            return null;
        }
    }

    private List<Issue> requestIssues(List<Integer> ids, RedmineEndpoint endpoint) {
        return ids.stream()
                .map(x -> getIssueById(x, endpoint))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Integer> prepareIssuesIds(String param, String date, String projectName, RedmineManager manager) throws RedmineException {
        Params params = new Params()
                .add(param, date)
                .add("project_id", projectName);
        return manager.getIssueManager().getIssues(params)
                .getResults()
                .stream()
                .map(Issue::getId)
                .collect(Collectors.toList());
    }

    // If date is not updated, we increment it manually by 1 sec
    // in order to avoid querying same issues multiple times
    private Date checkDate(Date date, Date lastDate) {
        if (date.toInstant().toEpochMilli() == lastDate.toInstant().toEpochMilli()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastDate);
            calendar.add(Calendar.SECOND, 1);
            lastDate = calendar.getTime();
        }
        return lastDate;
    }

    @Override
    public void checkForNewIssues(RedmineEndpoint endpoint) {
        String created = RedmineUtils.parseDateToAfter(endpoint.getLastCreatedOnDate());
        String projectId = endpoint.getProjectId();
        try {
            List<Issue> issues = getIssuesAfterDate(created, projectId, endpoint);
            if (!issues.isEmpty()) {
                issues.stream().map(x -> new Tuple<>(getUser(x.getAuthorId(), endpoint), x))
                        .forEach(x -> handler.handle(x.a, x.b, endpoint.getCompanyId()));
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
        try {
            List<Issue> issues = getIssuesUpdatedAfterDate(updated, projectId, endpoint);
            if (!issues.isEmpty()) {
                issues.stream().map(x -> new Tuple<>(getUser(x.getId(), endpoint), x))
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
    public void updateIssue(Issue issue, RedmineEndpoint endpoint) throws RedmineException {
        RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
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

    /*@Override
    public void uploadMyDude(int issueId, File myDude, String mimeType, RedmineEndpoint endpoint) throws IOException, RedmineException {
        RedmineManager manager = initManager(endpoint);
        manager.getAttachmentManager().addAttachmentToIssue(issueId, myDude, mimeType);
    }*/

    @Override
    public User findUser(Person person, RedmineEndpoint endpoint) throws RedmineException {
        String email = person.getContactInfo().getItems(En_ContactItemType.EMAIL).get(0).value();
        UserManager userManager = initManager(endpoint).getUserManager();
        return userManager.getUsers().stream()
                .filter(x -> x.getFirstName().equals(person.getFirstName()))
                .filter(x -> x.getLastName().equals(person.getLastName()))
                .filter(x -> x.getMail().equals(email))
                .findFirst().get();
    }

    private User getUser(int id, RedmineEndpoint endpoint) {
        try {
            return initManager(endpoint).getUserManager().getUserById(id);
        } catch (RedmineException e) {
            logger.debug("User with id {} not found", id);
            return null;
        }
    }

    private RedmineManager initManager(RedmineEndpoint endpoint) {
        return RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
    }

    @Autowired
    private RedmineEndpointDAO redmineEndpointDAO;

    @Autowired
    private RedmineNewIssueHandler handler;

    @Autowired
    private RedmineUpdateIssueHandler updateHandler;

    @Autowired
    private BackchannelUpdateIssueHandler backchannelUpdateIssueHandler;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RedmineServiceImpl.class);
}
