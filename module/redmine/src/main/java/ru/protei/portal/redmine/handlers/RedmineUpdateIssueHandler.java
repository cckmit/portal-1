package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalDetail;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.service.CommonService;

import java.util.*;
import java.util.stream.Collectors;

public final class RedmineUpdateIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.getByExternalAppCaseId(issue.getId() + "_"
                + endpoint.getCompanyId());
        if (object != null) {
            logger.debug("Found case object with id {}", object.getId());
            compareAndUpdate(issue, object, endpoint);
            caseObjectDAO.saveOrUpdate(object);
            logger.debug("Object with id {} saved", object.getId());
        } else {
            logger.debug("Object with external app id {} is not found; starting it's creation", issue.getId());
            newIssueHandler.handle(user, issue, endpoint);
        }
    }

    public void handleUpdateCreationDateAttachments(Issue issue, Long caseObjId) {
        commonService.processUpdateCreationDateAttachments(issue, caseObjId);
    }

    public void handleUpdateAttachmentsByIssue(Issue issue, Long caseId, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.get(caseId);
        commonService.processAttachments(issue, object, object.getInitiator(), endpoint);
    }

    public void handleUpdateCaseObjectByIssue(Issue issue, Long caseId, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.get(caseId);
        compareAndUpdate(issue, object, endpoint);
        caseObjectDAO.saveOrUpdate(object);
        logger.debug("Object with id {} saved", object.getId());
    }

    /**
     * Finding changes made after last update querying.
     * FIltering out comments, getting lists of details, parsing them to change types, distinct and returning it
     * @param journals see redmine api
     * @return list of changes since last check
     */
    private List<RedmineChangeType> parseJournals(List<Journal> journals) {
        logger.debug("Trying to parse redmine journals ...");

        final List<JournalDetail> details = journals.stream()
                .filter(x -> x.getNotes() == null || x.getNotes().isEmpty())
                .flatMap(x -> x.getDetails().stream())
                .collect(Collectors.toList());

        final List<Optional<RedmineChangeType>> changes =  details.stream()
                .map(JournalDetail::getName)
                .map(RedmineChangeType::findByName)
                .collect(Collectors.toList());

        return changes.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.toList());
    }

    private void compareAndUpdate(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();

        //Comments synchronize by date from last created comment
        logger.debug("Trying to get latest synchronized comment");
        final CaseComment comment = caseCommentDAO.getCaseComments(new CaseCommentQuery(object.getId()))
                .stream()
                .filter(x -> x.getAuthor().getCreator().equals("redmine"))
                .sorted(Comparator.comparing(CaseComment::getCreated))
                .reduce((o1, o2) -> o2)
                .orElse(null);
        final Date latestCreated = (comment != null) ? comment.getCreated() : issue.getCreatedOn();
        logger.debug("last comment was synced on {}, with id {}", latestCreated, comment.getId());
        logger.debug("starting adding new comments");

        logger.debug("finding comments (journals where notes are not empty)");
        final List<Journal> nonEmptyJournalsWithComments = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getCreatedOn() != null &&
                        x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(x -> StringUtils.isNotEmpty(x.getNotes()))
                .collect(Collectors.toList());
        logger.debug("found {} comments", nonEmptyJournalsWithComments.size());
        nonEmptyJournalsWithComments.forEach(journal -> logger.debug("Comment with id {} has following text: {}", journal.getId(), journal.getNotes()));

        final List<CaseComment> comments = nonEmptyJournalsWithComments
                .stream()
                .map(journal -> commonService.parseJournalToCaseComment(journal, companyId))
                .filter(Objects::nonNull)
                .map(caseComment -> commonService.processStoreComment(issue, caseComment.getAuthor(), object, object.getId(), caseComment))
                .collect(Collectors.toList());
        logger.debug("Added {} new case comments to issue with id: {}", comments.size(), object.getId());

        logger.debug("starting adding new status comments");
        logger.debug("finding status changes (journal details where status changes exist)");
        final List<Journal> nonEmptyJournalsWithStatusChange = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getCreatedOn() != null &&
                        x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(journal -> CollectionUtils.isNotEmpty(journal.getDetails()))
                .filter(journal -> journal.getDetails().stream().anyMatch(detail -> detail.getName().equals(RedmineChangeType.STATUS_CHANGE.getName())))
                .collect(Collectors.toList());
        logger.debug("found {} status changes", nonEmptyJournalsWithStatusChange.size());

        final List<CaseComment> statusComments = nonEmptyJournalsWithStatusChange
                .stream()
                .map(journal -> commonService.parseJournalToStatusComment(journal, companyId, endpoint.getStatusMapId()))
                .filter(Objects::nonNull)
                .map(statusComment -> commonService.processStoreComment(issue, statusComment.getAuthor(), object, object.getId(), statusComment))
                .collect(Collectors.toList());
        statusComments.forEach(statusComment -> logger.debug("Status comment with id {} has following status: {}", statusComment.getId(), statusComment.getCaseStateId()));
        logger.debug("Added {} new status comments to issue with id: {}", statusComments.size(), object.getId());

        //Parameters synchronize by date from last update (endpoint)
        final List<Journal> latestJournals = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getCreatedOn() != null &&
                        x.getCreatedOn().compareTo(endpoint.getLastUpdatedOnDate()) > 0)
                .collect(Collectors.toList());
        logger.debug("got {} journals after {}", latestJournals.size(), endpoint.getLastUpdatedOnDate());

        logger.debug("starting updating case object");
        parseJournals(latestJournals).stream().map(caseUpdaterFactory::getUpdater).forEach(x -> x.apply(object, issue, endpoint));

        commonService.processAttachments(issue, object, object.getInitiator(), endpoint);
    }

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CaseUpdaterFactory caseUpdaterFactory;

    @Autowired
    private RedmineNewIssueHandler newIssueHandler;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);
}
