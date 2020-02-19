package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.utils.CachedPersonMapper;
import ru.protei.portal.redmine.utils.HttpInputSource;

import java.util.*;
import java.util.stream.Collectors;

public final class CommonServiceImpl implements CommonService {

    @Override
    public void processAttachments(Issue issue, CachedPersonMapper personMapper, CaseObject obj, RedmineEndpoint endpoint) {
        final long caseObjId = obj.getId();
        final Set<Integer> existingAttachmentsHashCodes = getExistingAttachmentsHashCodes(obj.getId());
        if (CollectionUtils.isNotEmpty(issue.getAttachments())) {
            logger.debug("Process attachments for case with id {}, exists {} attachment", caseObjId, existingAttachmentsHashCodes.size());
            issue.getAttachments().stream()
                    .filter(attachment -> !personMapper.isTechUser(endpoint, attachment.getAuthor()))
                    .filter(attachment -> !existingAttachmentsHashCodes.contains(toHashCode(attachment)))
                    .forEach(attachment -> {
                        final Person author = personMapper.toProteiPerson(attachment.getAuthor());
                        Attachment a = new Attachment();
                        a.setCreated(attachment.getCreatedOn());
                        a.setCreatorId(author.getId());
                        a.setDataSize(attachment.getFileSize());
                        a.setFileName(attachment.getFileName());
                        a.setMimeType(attachment.getContentType());
                        a.setLabelText(attachment.getDescription());
                        try {
                            logger.debug("Invoke file controller to store attachment {} (size={})", attachment.getFileName(), attachment.getFileSize());
                            fileController.saveAttachment(a,
                                    new HttpInputSource(attachment.getContentURL(), endpoint.getApiKey()), attachment.getFileSize(), attachment.getContentType(), caseObjId);

                            publisherService.publishEvent(new CaseAttachmentEvent(this, ServiceModule.REDMINE, author.getId(), obj.getId(),
                                    Collections.singletonList(a), null));

                        } catch (Exception e) {
                            logger.debug("Unable to process attachment {}", attachment.getFileName());
                            logger.debug("Trace", e);
                        }
                    });
        }
    }

    @Override
    public void processComments(Collection<Journal> journals, CachedPersonMapper personMapper, CaseObject object) {
        logger.debug("Process comments for case with id {}", object.getId());

        journals.stream()
                .filter(journal -> StringUtils.isNotBlank(journal.getNotes()))
                .forEach(journal ->
                        caseUpdaterFactory.getCommentsUpdater().apply(object, null, journal, null, personMapper));
    }

    @Override
    public CaseComment createAndStoreComment(Journal journal, Person author, Long caseId) {
        final CaseComment comment = new CaseComment();
        comment.setCreated(journal.getCreatedOn());
        comment.setAuthor(author);
        comment.setText(journal.getNotes());
        comment.setCaseId(caseId);
        caseCommentDAO.persist(comment);
        return comment;
    }

    @Override
    public Long createAndStoreStateComment(Date created, Long authorId, Long stateId, Long caseId) {
        final CaseComment statusComment = new CaseComment();
        statusComment.setCreated(created);
        statusComment.setAuthorId(authorId);
        statusComment.setCaseStateId(stateId);
        statusComment.setCaseId(caseId);
        return caseCommentDAO.persist(statusComment);
    }

    @Override
    public Long createAndStoreImportanceComment(Date created, Long authorId, Integer importance, Long caseId) {
        final CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setCreated(created);
        stateChangeMessage.setAuthorId(authorId);
        stateChangeMessage.setCaseImpLevel(importance);
        stateChangeMessage.setCaseId(caseId);
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Set<Integer> getExistingAttachmentsHashCodes(long caseObjId) {
        return attachmentDAO.getListByCaseId(caseObjId).stream()
                .map(Attachment::toHashCodeForRedmineCheck)
                .collect(Collectors.toSet());
    }

    private int toHashCode(com.taskadapter.redmineapi.bean.Attachment attachment){
        return ((attachment.getCreatedOn() == null ? "" : attachment.getCreatedOn().getTime()) + (attachment.getFileName() == null ? "" : attachment.getFileName())).hashCode();
    }

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private FileController fileController;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    CaseUpdaterFactory caseUpdaterFactory;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
}
