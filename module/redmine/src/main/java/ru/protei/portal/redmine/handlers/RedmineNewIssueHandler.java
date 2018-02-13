package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RedmineNewIssueHandler implements RedmineEventHandler {
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
    PersonDAO personDAO;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);

    @Override
    public void handle(User user, Issue issue) {
        CaseObject object = createCaseObject(user, issue);
        handleComments(issue, parseUser(user), object);
    }

    private CaseObject createCaseObject(User user, Issue issue) {
        Person contactPerson = getAssignedPerson(0L, user, issue);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id {} from project with id", issue.getId(), issue.getProjectId());
            return null;
        }
        CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
        obj.setCaseType(En_CaseType.CRM_SUPPORT);
        //obj.setProduct(product);
        obj.setInitiator(contactPerson);
        //obj.setInitiatorCompany();
        obj.setImpLevel(issue.getPriorityId());
        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setStateId(En_CaseState.CREATED.getId());
        //obj.setProduct(product);
        caseObjectDAO.saveOrUpdate(obj);
        return obj;
    }

    private void handleComments(Issue issue, Person person, CaseObject obj) {
        Collection<Journal> journals = issue.getJournals();
        journals.stream()
                .map(this::parseJournal)
                .map(x -> processStoreComment(issue, person, obj, obj.getId(), x))
                .forEach(caseCommentDAO::saveOrUpdate);
    }

    /*private Attachment parseAttachment(com.taskadapter.redmineapi.bean.Attachment attachment) {
        Attachment proteiAttachment = new Attachment();
        proteiAttachment.setCreated(attachment.getCreatedOn());
        proteiAttachment.setCreatorId(Long.valueOf(attachment.getAuthor().getId()));
        proteiAttachment.setDataSize(attachment.getFileSize());
        proteiAttachment.setExtLink(attachment.getContentURL());
        proteiAttachment.setFileName(attachment.getFileName());
        proteiAttachment.setId(Long.valueOf(attachment.getId()));
        proteiAttachment.setLabelText(attachment.getDescription());
        proteiAttachment.setMimeType(attachment.getContentType());
        return proteiAttachment;
    }*/

    private CaseComment parseJournal(Journal journal) {
        CaseComment comment = new CaseComment();
        comment.setCreated(journal.getCreatedOn());
        Person author = parseUser(journal.getUser());
        comment.setAuthor(author);
        comment.setId(Long.valueOf(journal.getId()));
        comment.setText(journal.getNotes());
        return comment;
    }

    private Person parseUser(User user) {
        Person person = new Person();
        person.setFirstName(user.getFirstName());
        person.setId(Long.valueOf(user.getId()));
        person.setLastName(user.getLastName());
        person.setCreated(user.getCreatedOn());
        person.setDisplayName(user.getFirstName());
        user.getStatus();
        return person;
    }

    private CaseComment processStoreComment(Issue issue, Person contactPerson, CaseObject obj, Long caseObjId, CaseComment comment) {
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
        //@question
        //Should we publish event here? Why are we doing this in HPSM module?
        /*eventPublisherService.publishEvent(new CaseCommentEvent(
                ServiceModule.HPSM,
                caseService,
                obj,
                null,
                null,
                comment,
                addedAttachments,
                contactPerson
        ));*/
        return comment;
    }

    private Person getAssignedPerson(Long companyId, User user, Issue issue) {

        Person person = null;

        if (HelperFunc.isEmpty(user.getMail())) {
            logger.debug("no contact data provided for request {}", issue.getId());
            return null;
        }

        if (HelperFunc.isNotEmpty(user.getMail())) {
            // try find by e-mail
            person = personDAO.findContactByEmail(companyId, user.getMail());
        }


        if (person == null && HelperFunc.isNotEmpty(issue.getAssigneeName())) {
            // try find by name
            person = personDAO.findContactByName(companyId, issue.getAssigneeName());
        }


        if (person != null) {
            logger.debug("contact found: {} (id={}), request {}", person.getDisplayName(), person.getId(), issue.getId());
        } else {
            logger.debug("unable to find contact person : email={}, company={}, create new one", user.getMail(), companyId);

            person = new Person();
            person.setCreated(new Date());
            person.setCreator("redmine");
            //person.setCompanyId();

            if (HelperFunc.isEmpty(user.getFirstName()) && HelperFunc.isEmpty(user.getLastName())) {
                person.setFirstName("?");
                person.setLastName("?");
            } else {
                String[] np = user.getFullName().split("\\s+");
                person.setLastName(np[0]);
                person.setFirstName(np.length > 1 ? np[1] : "?");
                person.setSecondName(np.length > 2 ? np[2] : "");
            }

            person.setDisplayName(user.getFullName());

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
}
