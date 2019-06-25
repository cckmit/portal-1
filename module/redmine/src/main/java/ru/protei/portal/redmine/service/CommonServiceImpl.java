package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.redmine.handlers.RedmineNewIssueHandler;
import ru.protei.portal.redmine.utils.HttpInputSource;

import java.util.*;
import java.util.stream.Collectors;

public final class CommonServiceImpl implements CommonService {

    @Override
    public CaseComment parseJournal(Journal journal, long companyId) {
        final Person author = getAssignedPerson(companyId, journal.getUser());
        if (journal.getNotes().contains("PROTEI"))
            return null;
        final CaseComment comment = new CaseComment();
        comment.setCreated(journal.getCreatedOn());
        comment.setAuthor(author);
        comment.setText(journal.getNotes());
        return comment;
    }

    @Override
    public void processAttachments(Issue issue, CaseObject obj, Person contactPerson, RedmineEndpoint endpoint) {
        final long caseObjId = obj.getId();
        final Set<Integer> existingAttachmentsHashCodes = getExistingAttachmentsHashCodes(obj.getId());
        final Collection<Attachment> addedAttachments = new ArrayList<>(issue.getAttachments().size());
        if (CollectionUtils.isNotEmpty(issue.getAttachments())) {
            logger.debug("process attachments for case, id={}, existingAttachmentsHashCodes={}", caseObjId, existingAttachmentsHashCodes);
            List<CaseAttachment> caseAttachments = new ArrayList<>(issue.getAttachments().size());
            issue.getAttachments()
                    .stream()
                    .filter(x -> !existingAttachmentsHashCodes.contains(toHashCode(x)))
                    .forEach(x -> {
                        Attachment a = new Attachment();
                        a.setCreated(x.getCreatedOn());
                        a.setCreatorId(contactPerson.getId());
                        a.setDataSize(x.getFileSize());
                        a.setFileName(x.getFileName());
                        a.setMimeType(x.getContentType());
                        a.setLabelText(x.getDescription());
                        addedAttachments.add(a);
                        try {
                            logger.debug("invoke file controller to store attachment {} (size={}, hashCode={})", x.getFileName(), x.getFileSize(), toHashCode(x));
                            Long caId = fileController.saveAttachment(a,
                                    new HttpInputSource(x.getContentURL(), endpoint.getApiKey()), x.getFileSize(), x.getContentType(), caseObjId);
                            logger.debug("result from file controller = {} for {} (size={})", caId, x.getFileName(), x.getFileSize());
                            final boolean isAlreadyExists =
                                    caseAttachmentDAO.getByCondition("CASE_ID = ? and ATT_ID = ?", caseObjId, a.getId()) != null;
                            if (caId != null && !isAlreadyExists) {
                                caseAttachments.add(new CaseAttachment(caseObjId, a.getId()));
                            }
                        } catch (Exception e) {
                            logger.debug("unable to process attachment {}", x.getFileName());
                            logger.debug("trace", e);
                        }
                    });
            addedAttachments.forEach(attachmentDAO::saveOrUpdate);
            caseAttachments.forEach(caseAttachmentDAO::saveOrUpdate);
        }

        eventPublisherService.publishEvent(new CaseAttachmentEvent(
                ServiceModule.REDMINE,
                this,
                obj,
                addedAttachments,
                null,
                contactPerson
        ));
    }

/*
    @Override
    public void processUpdateCreationDateAttachments(Issue issue, CaseObject obj, RedmineEndpoint endpoint) {
        final long caseObjId = obj.getId();
        final Set<String> existingAttachmentsNames = getExistingAttachmentsNames(obj.getId());
        final Collection<Attachment> updatedAttachments = new ArrayList<>(existingAttachmentsNames.size());
        if (CollectionUtils.isNotEmpty(issue.getAttachments()) && CollectionUtils.isNotEmpty(existingAttachmentsNames)) {
            logger.debug("process update creation date of attachments for case, id={}, existingAttachmentsNames={}", caseObjId, existingAttachmentsNames);
            issue.getAttachments()
                    .stream()
                    .filter(x -> existingAttachmentsNames.contains(x.getFileName()))
                    .forEach(x -> {
                        Attachment attachment = attachmentDAO.getByCondition("file_name=? and DATA_SIZE=?", x.getFileName(), x.getFileSize());
                        if (attachment != null) {
                            attachment.setCreated(x.getCreatedOn());
                            updatedAttachments.add(attachment);
                        }
                    });
        }
        attachmentDAO.mergeBatch(updatedAttachments);
    }
*/

    @Override
    public void processUpdateCreationDateAttachments(Issue issue, CaseObject obj, RedmineEndpoint endpoint) {
        final long caseObjId = obj.getId();
        final Set<Attachment> existingAttachments = getExistingAttachments(caseObjId);
        if (CollectionUtils.isNotEmpty(issue.getAttachments()) && CollectionUtils.isNotEmpty(existingAttachments)) {
            logger.debug("process update creation date of attachments for case, id={}", caseObjId);
            existingAttachments.forEach(attachment -> {

                com.taskadapter.redmineapi.bean.Attachment redmineAttachment = issue.getAttachments().stream()
                        .filter(y -> y.getFileName().equals(attachment.getFileName()) && y.getFileSize().equals(attachment.getDataSize()))
                        .findFirst()
                        .orElse(null);

                if (redmineAttachment != null) {
                    attachment.setCreated(redmineAttachment.getCreatedOn());
                }
            });
        }
        attachmentDAO.mergeBatch(existingAttachments);
    }

    @Override
    public CaseComment processStoreComment(Issue issue, Person contactPerson, CaseObject obj, Long caseObjId, CaseComment comment) {
        comment.setCaseId(caseObjId);
        caseCommentDAO.saveOrUpdate(comment);

        eventPublisherService.publishEvent(new CaseCommentEvent(
                ServiceModule.REDMINE,
                caseService,
                obj,
                null,
                null,
                comment,
                null,
                contactPerson
        ));

        return comment;
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
        return caseAttachmentDAO.getListByCaseId(caseObjId).stream()
                .map(CaseAttachment::getAttachmentId)
                .map(attachmentDAO::get)
                .map(Attachment::toHashCodeForRedmineCheck)
                .collect(Collectors.toSet());
    }

    private Set<Attachment> getExistingAttachments(long caseObjId) {
        return caseAttachmentDAO.getListByCaseId(caseObjId).stream()
                .map(CaseAttachment::getAttachmentId)
                .map(attachmentDAO::get)
                .collect(Collectors.toSet());
    }

    private int toHashCode(com.taskadapter.redmineapi.bean.Attachment attachment){
        return ((attachment.getCreatedOn() == null ? "" : attachment.getCreatedOn().toString()) + (attachment.getFileName() == null ? "" : attachment.getFileName())).hashCode();
    }

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    private FileController fileController;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CaseService caseService;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private EventPublisherService eventPublisherService;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);

    private final static String STUB_NAME = "?";
}
