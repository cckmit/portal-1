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
import ru.protei.portal.core.model.dao.RedmineToCrmStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.core.model.ent.RedmineToCrmEntry;
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
        //Finding latest synchronized comment in our system
        logger.debug("Trying to get latest synchronized comment");
        final CaseComment comment = caseCommentDAO.getCaseComments(new CaseCommentQuery(object.getId()))
                .stream()
                .filter(x -> x.getAuthor().getCreator().equals("redmine"))
                .sorted(Comparator.comparing(CaseComment::getCreated))
                .reduce((o1, o2) -> o2)
                .orElse(null);

        final Date latestCreated = (comment != null) ? comment.getCreated() : issue.getCreatedOn();

        logger.debug("last comment was synced on: {}, with id {}", latestCreated);
        logger.debug("starting adding new comments");

        final List<Journal> latestJournals = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getCreatedOn() != null
                        && x.getCreatedOn().compareTo(endpoint.getLastUpdatedOnDate()) > 0)
                .collect(Collectors.toList());

        logger.debug("got {} journals after {}", latestJournals.size(), latestCreated);

        latestJournals.forEach(x -> logger.debug("Journal with id {} has following notes: {}", x.getId(), x.getNotes()));

        logger.debug("finding comments (journals where notes are not empty)");

        final List<Journal> nonEmptyJournals = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getNotes() != null && x.getCreatedOn() != null)
                .filter(x -> x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(x -> !x.getNotes().isEmpty())
                .collect(Collectors.toList());

        logger.debug("found {} comments", nonEmptyJournals.size());
        nonEmptyJournals.forEach(x -> logger.debug("Comment with id {} has following text: {}", x.getId(), x.getNotes()));

        List<CaseComment> comments = nonEmptyJournals
                .stream()
                .map(x -> commonService.parseJournalToCaseComment(x, companyId))
                .collect(Collectors.toList());

        List<CaseComment> stateChangeComments = nonEmptyJournals
                .stream()
                .map(journal -> createCommentWithChangedStatus(journal, endpoint.getStatusMapId(), companyId))
                .collect(Collectors.toList());

        comments.addAll(stateChangeComments);

        comments = comments
                .stream()
                .filter(Objects::nonNull)
                .map(x -> commonService.processStoreComment(issue, x.getAuthor(), object, object.getId(), x))
                .collect(Collectors.toList());

        logger.debug("Added {} new case comments to issue with id: {}", comments.size(), object.getId());

        parseJournals(latestJournals).stream().map(caseUpdaterFactory::getUpdater).forEach(x -> x.apply(object, issue, endpoint));

        commonService.processAttachments(issue, object, object.getInitiator(), endpoint);
    }

    private CaseComment createCommentWithChangedStatus(Journal journal, Long mapId, Long companyId) {
        JournalDetail detail = journal.getDetails()
                .stream()
                .filter(journalDetail -> "status_id".equals(journalDetail.getName()))
                .findFirst()
                .orElse(null);

        if (detail == null) {
            return null;
        }

        final RedmineToCrmEntry statusEntry = statusMapEntryDAO.getLocalStatus(mapId, Integer.parseInt(detail.getNewValue()));

        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthor(commonService.getAssignedPerson(companyId, journal.getUser()));
        stateChangeMessage.setCreated(journal.getCreatedOn());
        stateChangeMessage.setCaseStateId((long) statusEntry.getLocalStatusId());

        return stateChangeMessage;
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

    @Autowired
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);
}
