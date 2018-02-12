package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.redmine.api.RedmineStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public void handle(Issue issue) {
        createCaseObject(issue);
        createCaseComment(issue);
    }

    private void createCaseObject(Issue issue) {
        Person contactPerson = getAssignedPerson(issue);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id {} from project with id", issue.getId(), issue.getProjectId());
            return;
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
        caseObjectDAO.persist(obj);
    }

    private void createCaseComment(Issue issue) {
        Collection<Journal> journals = issue.getJournals();
        journals.stream().map(this::parseJournal).forEach(x -> {
            caseCommentDAO.persist(x);
            logger.debug("add comment to new case, case-id={}, comment={}", issue.getId(), x.getId());
        });
    }

    private void persistAttachments(Issue issue) {
        List<Attachment> attachments = issue.getAttachments().stream().map(this::parseAttachment).collect(Collectors.toList());
        caseAttachmentDAO.persist(attachments);
    }

    private Attachment parseAttachment(com.taskadapter.redmineapi.bean.Attachment attachment) {
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
    }

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

    private CaseComment processStoreComment(HpsmEvent request, Person contactPerson, CaseObject obj, Long caseObjId, CaseComment comment) {
        Collection<Attachment> addedAttachments = null;
        if (request.hasAttachments()) {
            logger.debug("process attachments for new case, id={}", caseObjId);

            List<CaseAttachment> caseAttachments = new ArrayList<>(request.getAttachments().size());
            addedAttachments = new ArrayList<>(request.getAttachments().size());

            for (HpsmAttachment in : request.getAttachments()) {
                Attachment a = new Attachment();
                a.setCreated(new Date());
                a.setCreatorId(contactPerson.getId());
                a.setDataSize((long) in.getSize());
                a.setFileName(in.getFileName());
                a.setMimeType(in.getContentType());
                a.setLabelText(in.getDescription());

                addedAttachments.add(a);

                try {
                    logger.debug("invoke file controller to store attachment {} (size={})", in.getFileName(), in.getSize());
                    Long caId = fileController.saveAttachment(a, in.getStreamSource(), caseObjId);
                    logger.debug("result from file controller = {} for {} (size={})", caId, in.getFileName(), in.getSize());

                    if (caId != null) {
                        caseAttachments.add(new CaseAttachment(caseObjId, a.getId(), comment.getId(), caId));
                    }
                } catch (Exception e) {
                    logger.debug("unable to process attachment {}", in.getFileName());
                    logger.debug("trace", e);
                }
            }

            comment.setCaseAttachments(caseAttachments);
        }

        eventPublisherService.publishEvent(new CaseCommentEvent(
                ServiceModule.HPSM,
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

    private Person getAssignedPerson(Issue issue) {

        Person person = personDAO.get(Long.valueOf(issue.getAssigneeId()));

        if (person != null) {
            logger.debug("contact found: {} (id={})", person.getDisplayName(), person.getId());
        } else {
            logger.debug("unable to find contact person : id={}, create new one", issue.getAssigneeId());
            person = new Person();
            person.setCreated(new Date());
            person.setCreator("hpsm");
            if (HelperFunc.isEmpty(issue.getAssigneeName())) {
                person.setFirstName("?");
                person.setLastName("?");
            } else {
                String[] np = issue.getAssigneeName().split("\\s+");
                person.setLastName(np[0]);
                person.setFirstName(np.length > 1 ? np[1] : "?");
                person.setSecondName(np.length > 2 ? np[2] : "");
            }

            person.setDisplayName(issue.getAssigneeName());
            person.setGender(En_Gender.UNDEFINED);
            person.setDeleted(false);
            person.setFired(false);

            personDAO.persist(person);
        }

        return person;
    }
}
