package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseNameAndDescriptionEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.service.events.EventPublisherService;
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
                    .filter(attachment -> !personMapper.isTechUser(endpoint.getDefaultUserId(), attachment.getAuthor()))
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
                        updateComment(object.getId(), journal.getCreatedOn(), journal.getNotes(), personMapper.toProteiPerson(journal.getUser()) )
                        );
    }

    @Override
    public CaseComment createAndStoreComment(Date creationDate, String text, Person author, Long caseId) {
        final CaseComment comment = new CaseComment();
        comment.setCreated(creationDate);
        comment.setAuthor(author);
        comment.setText(text);
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

    @Override
    public void updateCaseStatus( CaseObject object, Long statusMapId, Date creationOn, String value, Person author ){
        Integer newStatus = parseToInteger(value);
        logger.debug("Trying to get portal status id matching with redmine {}", newStatus);
        final RedmineToCrmEntry redmineStatusEntry = statusMapEntryDAO.getLocalStatus(statusMapId, newStatus);

        if (redmineStatusEntry != null) {

            final CaseObjectMeta oldMeta = new CaseObjectMeta(object);

            object.setStateId(redmineStatusEntry.getLocalStatusId());
            caseObjectDAO.merge(object);
            logger.debug("Updated case state for case with id {}, old={}, new={}", object.getId(), En_CaseState.getById(oldMeta.getStateId()), En_CaseState.getById(object.getStateId()));

            Long stateCommentId = createAndStoreStateComment(creationOn, author.getId(), redmineStatusEntry.getLocalStatusId().longValue(), object.getId());
            if (stateCommentId == null) {
                logger.error("State comment for the issue {} not saved!", object.getId());
            }

            publisherService.publishEvent(new CaseObjectMetaEvent(
                    this,
                    ServiceModule.REDMINE,
                    author.getId(),
                    En_ExtAppType.forCode(object.getExtAppType()),
                    oldMeta,
                    new CaseObjectMeta(object)));
        } else {
            logger.warn("Status was not found");
        }

    }

    @Override
    public void updateCasePriority( CaseObject object, Long priorityMapId, Journal journal, String value, Person author ) {
        Integer newPriority = parseToInteger(value);
        logger.debug("Trying to get portal priority level id matching with redmine {}", value);
        final RedminePriorityMapEntry priorityMapEntry = priorityMapEntryDAO.getByRedminePriorityId(newPriority, priorityMapId);

        if (priorityMapEntry != null) {

            final CaseObjectMeta oldMeta = new CaseObjectMeta(object);

            object.setImpLevel(priorityMapEntry.getLocalPriorityId());
            caseObjectDAO.merge(object);
            logger.debug("Updated case priority for case with id {}, old={}, new={}", object.getId(), En_ImportanceLevel.find(oldMeta.getImpLevel()), En_ImportanceLevel.find(object.getImpLevel()));

            Long ImportanceCommentId = createAndStoreImportanceComment(journal.getCreatedOn(), author.getId(), priorityMapEntry.getLocalPriorityId(), object.getId());
            if (ImportanceCommentId == null) {
                logger.error("Importance comment for the issue {} not saved!", object.getId());
            }

            publisherService.publishEvent(new CaseObjectMetaEvent(
                    this,
                    ServiceModule.REDMINE,
                    author.getId(),
                    En_ExtAppType.forCode(object.getExtAppType()),
                    oldMeta,
                    new CaseObjectMeta(object)));
        } else {
            logger.warn("Priority was not found");
        }
    }

    @Override
    public void updateCaseDescription( CaseObject object, String value, Person author ) {

        final DiffResult<String> infoDiff = new DiffResult<>(object.getInfo(), value);

        object.setInfo(value);
        caseObjectDAO.merge(object);
        logger.debug("Updated case info for case with id {}, old={}, new={}", object.getId(), infoDiff.getInitialState(), infoDiff.getNewState());

        publisherService.publishEvent(new CaseNameAndDescriptionEvent(
                this,
                object.getId(),
                new DiffResult<>(null, object.getName()),
                infoDiff,
                author.getId(),
                ServiceModule.REDMINE,
                En_ExtAppType.forCode(object.getExtAppType())));
    }

    @Override
    public void updateCaseSubject( CaseObject object, String value, Person author ) {
        final DiffResult<String> nameDiff = new DiffResult<>(object.getName(), value);

        object.setName(value);
        caseObjectDAO.merge(object);
        logger.debug("Updated case name for case with id {}, old={}, new={}", object.getId(), nameDiff.getInitialState(), nameDiff.getNewState());

        publisherService.publishEvent(new CaseNameAndDescriptionEvent(
                this,
                object.getId(),
                nameDiff,
                new DiffResult<>(null, object.getInfo()),
                author.getId(),
                ServiceModule.REDMINE,
                En_ExtAppType.forCode(object.getExtAppType())));
    }

    @Override
    public void updateComment( Long objectId, Date creationDate, String text, Person author ){
        CaseComment caseComment = createAndStoreComment( creationDate,  text, author, objectId);
        logger.debug("Added new case comment to case with id {}, comment has following text: {}", objectId, caseComment.getText());

        publisherService.publishEvent(new CaseCommentEvent(
                this,
                ServiceModule.REDMINE,
                author.getId(),
                objectId,
                false,
                null,
                caseComment,
                null));
    }

    private Integer parseToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Can't parse value {} to Integer", value);
            return null;
        }
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
    private EventPublisherService publisherService;

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    public CommonService commonService;

    private final static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
}
