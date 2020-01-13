package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.service.CommonService;

import java.util.*;
import java.util.stream.Collectors;

public class RedmineUpdateIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.getByExternalAppCaseId(issue.getId() + "_" + endpoint.getCompanyId());
        if (object != null) {
            logger.debug("Found case object with id {}", object.getId());
            compareAndUpdate(issue, object, endpoint);
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
        commonService.processAttachments(issue, object, endpoint);
    }

    public void handleUpdateCaseObjectByIssue(Issue issue, Long caseId, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.get(caseId);
        compareAndUpdate(issue, object, endpoint);
        logger.debug("Object with id {} saved", object.getId());
    }

    @Transactional
    protected void compareAndUpdate(Issue issue, CaseObject object, RedmineEndpoint endpoint) {

        //Synchronize comments
        handleComments(issue, object, endpoint);

        //Parameters synchronize by date from last update (endpoint)
        final List<Journal> latestJournals = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getCreatedOn() != null)
                .filter(x -> x.getCreatedOn().compareTo(endpoint.getLastUpdatedOnDate()) > 0)
                .collect(Collectors.toList());
        logger.debug("Got {} journals after {}", latestJournals.size(), endpoint.getLastUpdatedOnDate());

        //Synchronize status, priority, name, info
        latestJournals
                .stream()
                .filter(x -> StringUtils.isEmpty(x.getNotes()))
                .sorted(Comparator.comparing(Journal::getCreatedOn))
                .forEach(journal -> journal.getDetails()
                        .stream()
                        .filter(detail -> RedmineChangeType.findByName(detail.getName()).isPresent())
                        .forEach(detail -> {
                            caseUpdaterFactory
                                    .getUpdater(RedmineChangeType.findByName(detail.getName()).get())
                                    .apply(object, endpoint, journal, detail.getNewValue());
                        }));

        //Synchronize attachment
        commonService.processAttachments(issue, object, endpoint);
    }

    private void handleComments(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        logger.debug("Processing comments ...");

        //Comments synchronize by date from last created comment
        final long companyId = endpoint.getCompanyId();

        logger.debug("Trying to get latest synchronized comment");
        final CaseComment comment = caseCommentDAO.getCaseComments(new CaseCommentQuery(object.getId()))
                .stream()
                .filter(x -> x.getAuthor().getCreator().equals("redmine"))
                .sorted(Comparator.comparing(CaseComment::getCreated))
                .reduce((o1, o2) -> o2)
                .orElse(null);
        final Date latestCreated = (comment != null) ? comment.getCreated() : issue.getCreatedOn();
        logger.debug("Last comment was synced on {}, with id {}", latestCreated, comment.getId());

        logger.debug("Starting adding new comments");

        logger.debug("Finding comments (journals where notes are not empty)");
        final List<Journal> nonEmptyJournalsWithComments = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getCreatedOn() != null)
                .filter(x -> x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(x -> StringUtils.isNotEmpty(x.getNotes()))
                .collect(Collectors.toList());
        logger.debug("Found {} comments after {}", nonEmptyJournalsWithComments.size(), latestCreated);
        nonEmptyJournalsWithComments.forEach(journal -> logger.debug("Comment with journal-id {} has following text: {}", journal.getId(), journal.getNotes()));

        final List<CaseComment> comments = nonEmptyJournalsWithComments
                .stream()
                .map(journal -> commonService.parseJournalToCaseComment(journal, companyId))
                .filter(Objects::nonNull)
                .map(caseComment -> commonService.processStoreComment(caseComment.getAuthor().getId(), object.getId(), caseComment))
                .collect(Collectors.toList());
        logger.debug("Added {} new case comments to case with id {}", comments.size(), object.getId());
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
