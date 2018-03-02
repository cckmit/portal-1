package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.dao.RedmineStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.core.service.EventAssemblerService;
import ru.protei.portal.redmine.service.CommonService;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RedmineUpdateIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId());
        if (object != null) {
            logger.debug("Found case object with id {}", object.getId());
            compareAndUpdate(issue, object, endpoint);
            caseObjectDAO.saveOrUpdate(object);
            logger.debug("Object with id {} saved", object.getId());
        } else
            logger.debug("Object with external app id {} is not found", issue.getId());
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

        final List<CaseComment> comments = issue.getJournals()
                .stream()
                .filter(x -> x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(x -> !x.getNotes().isEmpty())
                .map(x -> commonService.parseJournal(x, companyId))
                .filter(Objects::nonNull)
                .map(x -> commonService.processStoreComment(issue, x.getAuthor(), object, object.getId(), x))
                .collect(Collectors.toList());

        logger.debug("Added {} new case comments to issue with id: {}", comments.size(), object.getId());

        updateObject(issue, object, endpoint);
        commonService.processAttachments(issue, object, object.getInitiator());
    }

    //Well, just simpliest way to update object: ignoring everything and just setting fields...
    private void updateObject(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        final long prioritiesId = endpoint.getPriorityMapId();
        final long statusesId = endpoint.getStatusMapId();
        logger.debug("Trying to get portal priority level id matching with redmine: {}", issue.getPriorityId());
        final int priorityLvl = priorityMapEntryDAO.getByRedminePriorityId(issue.getPriorityId(), prioritiesId).getLocalPriorityId();
        logger.debug("Found priority level id: {}", priorityLvl);

        logger.debug("Trying to get portal status id matching with redmine: {}", issue.getStatusId());
        final long stateId = statusMapEntryDAO.getByRedmineStatusId(issue.getStatusId(), statusesId).getLocalStatusId();
        logger.debug("Found status id: {}", stateId);

        object.setInfo(issue.getDescription());
        object.setImpLevel(priorityLvl);
        object.setStateId(stateId);
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
    private RedmineStatusMapEntryDAO statusMapEntryDAO;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);
}
