package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.redmine.factories.MergeHandlerFactory;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class RedmineUpdateIssueHandler implements RedmineEventHandler{
    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private FileController fileController;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CaseService caseService;

    @Autowired
    private EventPublisherService eventPublisherService;

    @Autowired
    private CommonService commonService;

    @Autowired
    MergeHandlerFactory mergeHandlerFactory;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);

    @Override
    public void handle(User user, Issue issue, long companyId) {
        CaseObject object = caseObjectDAO.getByCondition("EXT_APP_ID=?", issue.getId());
        object = compareAndUpdate(issue, object);
        caseObjectDAO.saveOrUpdate(object);
    }

    private CaseObject compareAndUpdate(Issue issue, CaseObject object) {
        logger.debug("Trying to get latest created comment");
        CaseComment comment = caseCommentDAO.getCaseComments(object.getId())
                .stream()
                .sorted(Comparator.comparing(CaseComment::getCreated))
                .reduce((o1, o2) -> o2)
                .orElse(null);

        final Date latestCreated = (comment != null) ? comment.getCreated() : issue.getCreatedOn();

        logger.debug("last comment was created on: {}, with id {}", latestCreated);
        logger.debug("starting adding new comments");

        commonService.processAttachments(issue, object, object.getInitiator());

        List<CaseComment> comments = issue.getJournals()
                .stream()
                .filter(x -> x.getCreatedOn().compareTo(latestCreated) > 0)
                .filter(x -> !x.getNotes().isEmpty())
                .map(RedmineUtils::parseJournal)
                .map(x -> commonService.processStoreComment(issue, x.getAuthor(), object, object.getId(), x))
                .collect(Collectors.toList());

        logger.debug("Added {} new case comments to issue with id: {}", comments.size(), object.getId());

        logger.debug("");

        return  mergeHandlerFactory.mergeWithCaseObject(issue, object);
    }
}
