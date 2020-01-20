package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.redmine.utils.HttpInputSource;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class CommonServiceImpl implements CommonService {

    @Override
    public CaseComment parseJournalToCaseComment(Journal journal, long companyId) {
        final Person author = getAssignedPerson(companyId, journal.getUser());

        final CaseComment comment = new CaseComment();
        comment.setCreated(journal.getCreatedOn());
        comment.setAuthor(author);
        comment.setText(journal.getNotes());
        return comment;
    }

    @Override
    public void processAttachments(Issue issue, CaseObject obj, RedmineEndpoint endpoint) {
        final long caseObjId = obj.getId();
        final Set<Integer> existingAttachmentsHashCodes = getExistingAttachmentsHashCodes(obj.getId());
        if (CollectionUtils.isNotEmpty(issue.getAttachments())) {
            logger.debug("Process attachments for case with id {}, exists {} attachment", caseObjId, existingAttachmentsHashCodes.size());
            issue.getAttachments()
                    .stream()
                    .filter(x -> !existingAttachmentsHashCodes.contains(toHashCode(x)))
                    .forEach(x -> {
                        final Person author = getAssignedPerson(endpoint.getCompanyId(), x.getAuthor());
                        Attachment a = new Attachment();
                        a.setCreated(x.getCreatedOn());
                        a.setCreatorId(author.getId());
                        a.setDataSize(x.getFileSize());
                        a.setFileName(x.getFileName());
                        a.setMimeType(x.getContentType());
                        a.setLabelText(x.getDescription());
                        try {
                            logger.debug("Invoke file controller to store attachment {} (size={})", x.getFileName(), x.getFileSize());
                            fileController.saveAttachment(a,
                                    new HttpInputSource(x.getContentURL(), endpoint.getApiKey()), x.getFileSize(), x.getContentType(), caseObjId);

                            publisherService.publishEvent(new CaseAttachmentEvent(this, ServiceModule.REDMINE, author.getId(), obj.getId(),
                                    Collections.singletonList(a), null));

                        } catch (Exception e) {
                            logger.debug("Unable to process attachment {}", x.getFileName());
                            logger.debug("Trace", e);
                        }
                    });
        }
    }

    @Override
    public void processUpdateCreationDateAttachments(Issue issue, Long caseObjId) {
        final List<Attachment> existingAttachments = attachmentDAO.getListByCaseId(caseObjId);
        if (CollectionUtils.isNotEmpty(issue.getAttachments()) && CollectionUtils.isNotEmpty(existingAttachments)) {
            logger.debug("Process update creation date of attachments for case object with id {}", caseObjId);
            existingAttachments.forEach(attachment ->
                issue.getAttachments().stream()
                        .filter(y -> y.getFileName().equals(attachment.getFileName()) && y.getFileSize().equals(attachment.getDataSize()))
                        .findFirst()
                        .ifPresent(redmineAttachment -> attachment.setCreated(redmineAttachment.getCreatedOn())));
        }
        attachmentDAO.mergeBatch(existingAttachments);
    }

    @Override
    public CaseComment processStoreComment(Long authorId, Long caseObjectId, CaseComment comment) {
        comment.setCaseId(caseObjectId);
        caseCommentDAO.saveOrUpdate(comment);

        publisherService.publishEvent(new CaseCommentEvent(caseService, ServiceModule.REDMINE, authorId,
                caseObjectId, false, null, comment, null));

        return comment;
    }

    @Override
    public void createAndStoreStateComment(Date created, Long authorId, Long stateId, Long caseObjectId) {
        CaseComment statusComment = new CaseComment();
        statusComment.setCreated(created);
        statusComment.setAuthorId(authorId);
        statusComment.setCaseStateId(stateId);
        statusComment.setCaseId(caseObjectId);
        caseCommentDAO.persist(statusComment);
    }

    @Override
    public Person getAssignedPerson(Long companyId, User user) {

        Person person = null;

        if (HelperFunc.isNotEmpty(user.getMail())) {
            // try find by e-mail
            person = personDAO.findContactByEmail(companyId, user.getMail());
        }

        if (person == null && HelperFunc.isNotEmpty(user.getFullName())) {
            // try find by name
            person = personDAO.findContactByName(companyId, user.getFullName());
        }

        if (person != null) {
            logger.debug("contact found: {} (id={})", person.getDisplayName(), person.getId());
        } else {
            logger.debug("unable to find contact person : email={}, company={}, create new one", user.getMail(), companyId);

            person = new Person();
            person.setCreated(new Date());
            person.setCreator("redmine");
            person.setCompanyId(companyId);
            if (HelperFunc.isEmpty(user.getFirstName()) && HelperFunc.isEmpty(user.getLastName())) {
                person.setFirstName(STUB_NAME);
                person.setLastName(STUB_NAME);
                person.setSecondName(STUB_NAME);
                person.setDisplayName(STUB_NAME);
                person.setDisplayShortName(STUB_NAME);
            } else {
                String[] np = user.getFullName().split("\\s+");
                person.setLastName(np[0]);
                person.setFirstName(np.length > 1 ? np[1] : STUB_NAME);
                person.setSecondName(np.length > 2 ? np[2] : "");
                person.setDisplayName(user.getFullName());
                person.setDisplayShortName(getDisplayShortName(person));
            }

            PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade();

            if (user.getMail() != null)
                contactInfoFacade.setEmail(user.getMail());

            person.setContactInfo(contactInfoFacade.editInfo());

            person.setGender(En_Gender.UNDEFINED);
            person.setDeleted(false);
            person.setFired(false);
            personDAO.persist(person);
        }

        return person;
    }

    private String getDisplayShortName(Person person) {
        return person.getLastName() + " "
               + getShortName(person.getFirstName())
               + getShortName(person.getSecondName());
    }
    private String getShortName(String name) {
        if (HelperFunc.isEmpty(name) || name.equals(STUB_NAME))
            return "";
        if (name.length() >= 1 ) {
            return name.substring(0, 1) + ". ";
        } else {
            return name + " ";
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
    private PersonDAO personDAO;

    @Autowired
    private CaseService caseService;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    private final static String STUB_NAME = "?";
}
