package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.handlers.RedmineBackChannelHandler;
import ru.protei.portal.redmine.handlers.RedmineNewIssueHandler;
import ru.protei.portal.redmine.handlers.RedmineUpdateIssueHandler;
import ru.protei.portal.redmine.utils.LoggerUtils;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.redmine.utils.RedmineUtils.userInfo;

//Stateless
public final class RedmineServiceImpl implements RedmineService {

    @Override
    @EventListener
    public void onAssembledCaseEvent(AssembledCaseEvent event) {
        if (!event.isCoreModuleEvent()) {
            logger.debug("skip handle plugin-published event for {}", event.getCaseObject().getExtId());
            return;
        }
        try {
            redmineBackChannelHandler.handle(event);
            logger.debug("case-object event handled for case {}", event.getCaseObject().getExtId());
        } catch (Exception e) {
            logger.error("error while handling event for case " + event.getCaseObject().getExtId(), e);
        }
    }

    @Override
    public Issue getIssueById(int id, RedmineEndpoint endpoint) {
        try {
            final RedmineManager manager =
                    RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
            return manager.getIssueManager().getIssueById(id, Include.journals, Include.attachments, Include.watchers);
        } catch (RedmineException e) {
            logger.error("Get exception while trying to get issue with id {}", id);
            LoggerUtils.logRedmineException(logger, e);
            return null;
        }
    }

    @Override
    public void checkForNewIssues(RedmineEndpoint endpoint) {
        final String created = RedmineUtils.parseDateToAfter(endpoint.getLastCreatedOnDate());
        final String projectId = endpoint.getProjectId();

        logger.debug("new issues poll from redmine endpoint {}, company {}, project {}, check created from {}",
                endpoint.getServerAddress(), endpoint.getCompanyId(), projectId, created);

        try {
            Date lastCreatedOn = endpoint.getLastCreatedOnDate();
            logger.debug("Last new issue date from DB: {}", lastCreatedOn);
            final List<Issue> issues = getIssuesCreatedAfterDate(created, projectId, endpoint);
            if (!issues.isEmpty()) {

                logger.debug("got {} issues from {}", issues.size(), endpoint.getServerAddress());

                for (Issue issue : issues) {
                    User user = getUser(issue.getAuthorId(), endpoint);

                    logger.debug("try handle new issue from {}, issue-id: {}", userInfo(user), issue.getId());

                    handler.handle(user, issue, endpoint);
                    lastCreatedOn = RedmineUtils.maxDate(issue.getCreatedOn(), lastCreatedOn);
                }

                logger.debug("max created on, taken from issues: {}", lastCreatedOn);

                // добавляем 2 секунды, иначе redmine будет возвращать опять нам последнюю запись
                // не очень это хорошо, нужно наверное все таки переделать получение новых issue
                // не только по дате, но еще и по ID
                lastCreatedOn = new Date(lastCreatedOn.getTime() + 2000);

                logger.debug("max created on, store in our db: {}", lastCreatedOn);

                endpoint.setLastCreatedOnDate(lastCreatedOn);
                redmineEndpointDAO.updateCreatedOn(endpoint);
            } else {
                logger.debug("no new issues from {}", endpoint.getServerAddress());
            }

        } catch (RedmineException re) {
            //do some stuff
            logger.error("Failed when getting issues created after date: {} from project with id: {}", created, projectId);
            LoggerUtils.logRedmineException(logger, re);
        }
    }

    @Override
    public void checkForUpdatedIssues(RedmineEndpoint endpoint) {
        final String updated = RedmineUtils.parseDateToAfter(endpoint.getLastUpdatedOnDate());

        final String projectId = endpoint.getProjectId();

        logger.debug("updated issues poll from redmine endpoint {}, company {}, project {}, check updated from {}",
                endpoint.getServerAddress(), endpoint.getCompanyId(), projectId, updated);

        try {
            Date lastUpdatedOn = endpoint.getLastUpdatedOnDate();
            final List<Issue> issues = getIssuesUpdatedAfterDate(updated, projectId, endpoint);
            issues.addAll(getClosedIssuesAfterDate(updated, projectId, endpoint));
            if (!issues.isEmpty()) {
                logger.debug("got {} updated issues from {}", issues.size(), endpoint.getServerAddress());

                for (Issue issue : issues) {
                    if (Math.abs(issue.getUpdatedOn().getTime() - issue.getCreatedOn().getTime()) < DELAY) {
                        logger.debug("Skipping recently created issue with id {}", issue.getId());
                        continue;
                    }
                    User user = getUser(issue.getAuthorId(), endpoint);

                    if (user == null) {
                        logger.debug("User with id {} not found, skipping it", issue.getAuthorId());
                        continue;
                    }

                    logger.debug("try update issue from {}, issue-id: {}", userInfo(user), issue.getId());
                    updateHandler.handle(user, issue, endpoint);
                    lastUpdatedOn = RedmineUtils.maxDate(issue.getUpdatedOn(), lastUpdatedOn);
                }
                logger.debug("max update-date, taken from issues: {}", lastUpdatedOn);

                // use same trick as above (see check-new-issues)
                lastUpdatedOn = new Date(lastUpdatedOn.getTime() + 2000);

                logger.debug("max update-date, store in our db: {}", lastUpdatedOn);

                endpoint.setLastUpdatedOnDate(lastUpdatedOn);
                redmineEndpointDAO.updateUpdatedOn(endpoint);
            } else {
                logger.debug("no changed issues from {}", endpoint.getServerAddress());
            }
        } catch (RedmineException re) {
            //something
            logger.error("Failed when getting issues updated after date {} from project {}", updated, projectId);
            LoggerUtils.logRedmineException(logger, re);
        }
    }

    @Override
    public void updateIssue(Issue issue, RedmineEndpoint endpoint) throws RedmineException {
        final RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        manager.getIssueManager().update(issue);
    }

    @Override
    public List<com.taskadapter.redmineapi.bean.Attachment> uploadAttachment(Collection<Attachment> attachments, RedmineEndpoint endpoint) {
        final AttachmentManager attachmentManager = initManager(endpoint).getAttachmentManager();
        List<com.taskadapter.redmineapi.bean.Attachment> list = new ArrayList<>();
        for(Attachment attachment : attachments) {
            FileStorage.File file = fileStorage.getFile(attachment.getExtLink());
            try {
                list.add(attachmentManager.uploadAttachment(attachment.getFileName(), file.getContentType(), file.getData()));
            } catch (IOException | RedmineException ex) {
                logger.debug("Get exception while trying to process attach with ExtLink {}", attachment.getExtLink());
                ex.printStackTrace();
                return null;
            }
        }
        return list;
    }

    private List<Issue> getClosedIssuesAfterDate(String date, String projectName, RedmineEndpoint endpoint) throws RedmineException {
        final RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        final Params params = new Params()
                .add("status_id", "closed")
                .add("updated_on", date)
                .add("project_id", projectName);

        final List<Integer> idsOfClosed = manager.getIssueManager().getIssues(params).getResults()
                .parallelStream()
                .map(Issue::getId)
                .collect(Collectors.toList());

        idsOfClosed.forEach(x -> logger.debug("Issue with id {} was closed recently, handling it", x));

        return requestIssues(idsOfClosed, endpoint);
    }

    private List<Issue> getIssuesCreatedAfterDate(String date, String projectName, RedmineEndpoint endpoint) throws RedmineException {
        final RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        final List<Integer> ids = prepareIssuesIds("created_on", date, projectName, manager);
        return requestIssues(ids, endpoint);
    }

    private List<Issue> getIssuesUpdatedAfterDate(String date, String projectName, RedmineEndpoint endpoint) throws RedmineException {
        final RedmineManager manager = RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        final List<Integer> ids = prepareIssuesIds("updated_on", date, projectName, manager);
        return requestIssues(ids, endpoint);
    }

    private User getUser(int id, RedmineEndpoint endpoint) {
        try {
            return initManager(endpoint).getUserManager().getUserById(id);
        } catch (RedmineException e) {
            logger.error("User with id {} not found", id);
            LoggerUtils.logRedmineException(logger, e);
            return null;
        }
    }

    private RedmineManager initManager(RedmineEndpoint endpoint) {
        return RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
    }

    private List<Issue> requestIssues(List<Integer> ids, RedmineEndpoint endpoint) {
        return ids.stream()
                .map(x -> getIssueById(x, endpoint))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Integer> prepareIssuesIds(String param, String date, String projectName, RedmineManager manager) throws RedmineException {
        final Params params = new Params()
                .add(param, date)
                .add("limit", "100")
                .add("project_id", projectName);
        return manager.getIssueManager().getIssues(params)
                .getResults()
                .stream()
                .map(Issue::getId)
                .collect(Collectors.toList());
    }

    @Autowired
    private RedmineEndpointDAO redmineEndpointDAO;

    @Autowired
    private RedmineNewIssueHandler handler;

    @Autowired
    private RedmineUpdateIssueHandler updateHandler;

    @Autowired
    private RedmineBackChannelHandler redmineBackChannelHandler;

    @Autowired
    FileStorage fileStorage;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RedmineServiceImpl.class);

    //5 mins in ms
    private static final long DELAY = 300000L;
}
