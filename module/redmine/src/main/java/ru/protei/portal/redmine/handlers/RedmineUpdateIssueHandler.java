package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.EventAssemblerService;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class RedmineUpdateIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.getByExternalAppCaseId(issue.getId().toString());
        if (object != null) {
            logger.debug("Found case object with id {}", object.getId());
            compareAndUpdate(issue, object, endpoint);
            caseObjectDAO.saveOrUpdate(object);
            logger.debug("Object with id {} saved", object.getId());
        } else {
            logger.debug("Object with external app id {} is not found; strating it's creation", issue.getId());
            newIssueHandler.handle(user, issue, endpoint);
        }
    }

    private void compareAndUpdate(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        //Finding latest synchronized comment in our system
        logger.debug("Trying to get latest synchronized comment");
        final CaseComment comment = caseCommentDAO.getCaseComments(object.getId())
                .stream()
                .filter(x -> x.getAuthor().getCreator().equals("redmine"))
                .sorted(Comparator.comparing(CaseComment::getCreated))
                .reduce((o1, o2) -> o2)
                .orElse(null);

        final Date latestCreated = (comment != null) ? comment.getCreated() : issue.getCreatedOn();

        logger.debug("last comment was synced on: {}, with id {}", latestCreated);
        logger.debug("starting adding new comments");

        final List<Journal> nonEmptyJournals = issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getNotes() != null && x.getCreatedOn() != null)
                .filter(x -> x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(x -> !x.getNotes().isEmpty())
                .collect(Collectors.toList());

        final List<CaseComment> comments = nonEmptyJournals
                .stream()
                .map(x -> commonService.parseJournal(x, companyId))
                .filter(Objects::nonNull)
                .map(x -> commonService.processStoreComment(issue, x.getAuthor(), object, object.getId(), x))
                .collect(Collectors.toList());

        logger.debug("Added {} new case comments to issue with id: {}", comments.size(), object.getId());

        updateObject(issue, object, endpoint);
        commonService.processAttachments(issue, object, object.getInitiator(), endpoint);
    }

    //Well, just simpliest way to update object: ignoring everything and just setting fields...
    private void updateObject(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        final long priorityMapId = endpoint.getPriorityMapId();
        final long statusMapId = endpoint.getStatusMapId();

        logger.debug("Trying to get portal priority level id matching with redmine: {}",
                issue.getCustomFieldById(RedmineUtils.REDMINE_CUSTOM_FIELD_ID).getValue());
        final String redminePriorityName = issue.getCustomFieldById(RedmineUtils.REDMINE_CUSTOM_FIELD_ID).getValue();
        final RedminePriorityMapEntry priorityMapEntry =
                priorityMapEntryDAO.getByRedminePriorityName(redminePriorityName, priorityMapId);
        if (priorityMapEntry != null) {
            logger.debug("Found priority level id: {}", priorityMapEntry.getLocalPriorityId());
            object.setImpLevel(priorityMapEntry.getLocalPriorityId());
        } else
            logger.debug("Status was not found");

        logger.debug("Trying to get portal status id matching with redmine: {}", issue.getStatusId());
        final RedmineToCrmEntry redmineStatusEntry =
                statusMapEntryDAO.getLocalStatus(statusMapId, issue.getStatusId());
        if (redmineStatusEntry != null) {
            logger.debug("Found status id: {}", redmineStatusEntry.getLocalStatusId());
            object.setStateId(redmineStatusEntry.getLocalStatusId());
        } else
            logger.debug("Status was not found");

        object.setInfo(issue.getDescription());
        object.setName(issue.getSubject());
    }

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    EventAssemblerService assemblerService;

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private RedmineNewIssueHandler newIssueHandler;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);
}
