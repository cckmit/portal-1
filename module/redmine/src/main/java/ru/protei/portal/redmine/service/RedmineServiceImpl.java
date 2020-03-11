package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public final class RedmineServiceImpl implements RedmineService {

    @Override
    public Result<Issue> getIssueById( int id, RedmineEndpoint endpoint) {
        final RedmineManager manager =
                RedmineManagerFactory.createWithApiKey( endpoint.getServerAddress(), endpoint.getApiKey() );
        Issue issue = null;
        try {
            issue = manager.getIssueManager().getIssueById( id, Include.journals, Include.attachments, Include.watchers );
        } catch (RedmineException e) {
            logger.error( "Get exception while trying to get issue with id {}", id );
            logRedmineException( logger, e );
            return error( En_ResultStatus.INTERNAL_ERROR, "Can't get issue by id " + id + ". RedmineException occurred "+ e.getMessage() );
        }

        if (issue == null) return error( En_ResultStatus.NOT_FOUND, "Issue with id " + id + " was not found" );
        return ok( issue );
    }

    @Override
    public Result<List<Issue>> getNewIssues( RedmineEndpoint endpoint ) {
        final String created = RedmineUtils.parseDateToAfter(endpoint.getLastCreatedOnDate());
        final String projectId = endpoint.getProjectId();

        logger.debug("new issues poll from redmine endpoint {}, company {}, project {}, check created from {}",
                endpoint.getServerAddress(), endpoint.getCompanyId(), projectId, created);

        try{
            return ok(getIssuesCreatedAfterDate(created, projectId, endpoint));
        } catch (RedmineException re) {
            logger.error("Failed when getting issues created after date: {} from project with id: {}", created, projectId);
            logRedmineException(logger, re);
            return error( En_ResultStatus.INTERNAL_ERROR );
        }
    }

    @Override
    public  Result<List<Issue>> getUpdatedIssues(RedmineEndpoint endpoint) {
        final String updated = RedmineUtils.parseDateToAfter(endpoint.getLastUpdatedOnDate());

        final String projectId = endpoint.getProjectId();

        logger.debug("updated issues poll from redmine endpoint {}, company {}, project {}, check updated from {}",
                endpoint.getServerAddress(), endpoint.getCompanyId(), projectId, updated);

        try {
            List<Issue> issues = getIssuesUpdatedAfterDate( updated, projectId, endpoint );
            issues.addAll( getClosedIssuesAfterDate( updated, projectId, endpoint ) );
            return ok(issues);
        } catch (RedmineException re) {
            logger.error( "Failed when getting issues updated after date {} from project {}", updated, projectId );
            logRedmineException( logger, re );
            return error( En_ResultStatus.INTERNAL_ERROR );
        }
    }

    @Override
    public Result<Issue> updateIssue( Issue issue, RedmineEndpoint endpoint ) {
        final RedmineManager manager = RedmineManagerFactory.createWithApiKey( endpoint.getServerAddress(), endpoint.getApiKey() );
        try {
            manager.getIssueManager().update( issue );
            return ok( issue );
        } catch (RedmineException e) {
            logRedmineException( logger, e );
            return error( En_ResultStatus.INTERNAL_ERROR, String.format( "Failed to update issue with id {}", issue.getId() ) );
        }
    }

    @Override
    public Result<List<com.taskadapter.redmineapi.bean.Attachment>> uploadAttachment( Collection<Attachment> attachments, RedmineEndpoint endpoint) {
        final AttachmentManager attachmentManager = initManager(endpoint).getAttachmentManager();
        List<com.taskadapter.redmineapi.bean.Attachment> list = new ArrayList<>();
        for(Attachment attachment : attachments) {
            FileStorage.File file = fileStorage.getFile(attachment.getExtLink());
            try {
                list.add(attachmentManager.uploadAttachment(attachment.getFileName(), file.getContentType(), file.getData()));
            } catch (IOException | RedmineException ex) {
                logger.debug("Get exception while trying to process attach with ExtLink {}", attachment.getExtLink());
                ex.printStackTrace();
                return error( En_ResultStatus.INTERNAL_ERROR, "Failed to upload attachment link=" + attachment.getExtLink() );
            }
        }
        return ok(list);
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

    @Override
    public Result<User> getUser( int id, RedmineEndpoint endpoint ) {
        try {
            return ok(initManager(endpoint).getUserManager().getUserById(id));
        } catch (RedmineException e) {
            logger.error("User with id {} not found", id);
            logRedmineException(logger, e);
            return error( En_ResultStatus.INTERNAL_ERROR );
        }
    }

    private RedmineManager initManager(RedmineEndpoint endpoint) {
        return RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
    }

    private List<Issue> requestIssues(List<Integer> ids, RedmineEndpoint endpoint) {
        return ids.stream()
                .map(x -> getIssueById(x, endpoint))
                .filter(Result::isOk)
                .map( Result::getData )
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

    public static void logRedmineException( Logger logger, RedmineException e) {
        if (e instanceof RedmineProcessingException) {
            logger.error(String.join(", ", ((RedmineProcessingException) e).getErrors()), e);
        } else {
            logger.error(e.getMessage(), e);
        }
    }

    @Autowired
    FileStorage fileStorage;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RedmineServiceImpl.class);


}
