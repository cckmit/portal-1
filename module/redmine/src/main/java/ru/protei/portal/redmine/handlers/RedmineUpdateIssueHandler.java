package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.redmine.api.RedmineIssuePriority;
import ru.protei.portal.redmine.api.RedmineIssueType;
import ru.protei.portal.redmine.api.RedmineStatus;
import ru.protei.portal.redmine.service.CommonService;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RedmineUpdateIssueHandler implements RedmineEventHandler {
    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private CommonService commonService;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);

    @Override
    public void handle(User user, Issue issue, long companyId) {
        CaseObject object = caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId());
        compareAndUpdate(issue, object, companyId);
        caseObjectDAO.saveOrUpdate(object);
    }

    private void compareAndUpdate(Issue issue, CaseObject object, long companyId) {
        logger.debug("Trying to get latest created comment");
        CaseComment comment = caseCommentDAO.getCaseComments(object.getId())
                .stream()
                .sorted(Comparator.comparing(CaseComment::getCreated))
                .reduce((o1, o2) -> o2)
                .orElse(null);

        final Date latestCreated = (comment != null) ? comment.getCreated() : issue.getCreatedOn();

        logger.debug("last comment was created on: {}, with id {}", latestCreated);
        logger.debug("starting adding new comments");

        List<CaseComment> comments = issue.getJournals()
                .stream()
                .filter(x -> x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(x -> !x.getNotes().isEmpty())
                .map(x -> commonService.parseJournal(x, companyId))
                .filter(Objects::nonNull)
                .map(x -> commonService.processStoreComment(issue, x.getAuthor(), object, object.getId(), x))
                .collect(Collectors.toList());

        logger.debug("Added {} new case comments to issue with id: {}", comments.size(), object.getId());

        updateObject(issue, object);
        commonService.processAttachments(issue, object, object.getInitiator());
    }

    private void updateObject(Issue issue, CaseObject object) {
        object.setInfo(issue.getDescription());
//        object.setCaseType(RedmineIssueType.find(issue.getTracker().getId()));
        object.setImpLevel(RedmineIssuePriority.find(issue.getPriorityId()).getCaseImpLevel().getId());
        object.setState(RedmineStatus.find(issue.getStatusId()).getCaseState());
        object.setName(issue.getSubject());
    }
}
