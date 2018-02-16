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

    @Override
    public void handle(User user, Issue issue, long companyId) {
        CaseObject object = caseObjectDAO.getByCondition("EXT_ID=?", issue.getId());
        compareAndUpdate(object, issue);
    }

    private CaseObject compareAndUpdate(CaseObject object, Issue issue) {
        CaseComment comment = caseCommentDAO.getCaseComments(object.getId())
                .stream()
                .sorted(Comparator.comparing(CaseComment::getCreated))
                .reduce((o1, o2) -> o2)
                .orElse(null);

        final Date latestCreated = (comment != null) ? comment.getCreated() : issue.getCreatedOn();

        List<Journal> journals = issue.getJournals()
                .stream()
                .filter(x -> x.getCreatedOn().compareTo(latestCreated) > 0)
                .map(RedmineUtils::parseJournal)
                .map()
                .collect(Collectors.toList());

        issue.getAttachments();
        issue.getSubject();
        issue.getStatusName();
        issue.getAssigneeId();
        issue.getCategory();
        issue.getChangesets();
        issue.getUpdatedOn();
        issue.getPriorityId();
        issue.getSpentHours();
        issue.getDescription();
        issue.getChangesets();
        issue.getProjectName();
    }

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);

    private CaseComment processStoreComment(Issue issue, Person contactPerson, CaseObject obj, Long caseObjId, CaseComment comment) {
        caseCommentDAO.persist(comment);
        final Collection<Attachment> addedAttachments = new ArrayList<>(issue.getAttachments().size());
        if (issue.getAttachments() != null && !issue.getAttachments().isEmpty()) {
            logger.debug("process attachments for new case, id={}", caseObjId);

            List<CaseAttachment> caseAttachments = new ArrayList<>(issue.getAttachments().size());

            issue.getAttachments().forEach(x -> {
                Attachment a = new Attachment();
                a.setCreated(new Date());
                a.setCreatorId(contactPerson.getId());
                a.setDataSize(x.getFileSize());
                a.setFileName(x.getFileName());
                a.setMimeType(x.getContentType());
                a.setLabelText(x.getDescription());
                addedAttachments.add(a);
                try {
                    logger.debug("invoke file controller to store attachment {} (size={})", x.getFileName(), x.getFileSize());
                    Long caId = fileController.saveAttachment(a, new UrlResource(x.getContentURL()), caseObjId);
                    logger.debug("result from file controller = {} for {} (size={})", caId, x.getFileName(), x.getFileSize());

                    if (caId != null) {
                        caseAttachments.add(new CaseAttachment(caseObjId, a.getId(), comment.getId(), caId));
                    }
                } catch (Exception e) {
                    logger.debug("unable to process attachment {}", x.getFileName());
                    logger.debug("trace", e);
                }
            });

            comment.setCaseAttachments(caseAttachments);
        }

        eventPublisherService.publishEvent(new CaseCommentEvent(
                ServiceModule.REDMINE,
                caseService,
                obj,
                null,
                null,
                comment,
                addedAttachments,
                contactPerson
        ));

        return comment;
    }

}
