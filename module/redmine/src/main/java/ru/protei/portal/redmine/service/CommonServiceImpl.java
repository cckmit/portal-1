package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.redmine.handlers.RedmineNewIssueHandler;

import java.util.*;
import java.util.stream.Collectors;

public class CommonServiceImpl implements CommonService {
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
    private AttachmentDAO attachmentDAO;

    @Autowired
    private EventPublisherService eventPublisherService;

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);

    private Set<String> getExistingAttachmentsNames(long caseObjId) {
        return caseAttachmentDAO.getListByCaseId(caseObjId).stream()
                .map(CaseAttachment::getAttachmentId)
                .map(attachmentDAO::get)
                .map(Attachment::getFileName)
                .collect(Collectors.toSet());
    }

    @Override
    public void processAttachments(Issue issue, CaseObject obj, Person contactPerson) {
        long caseObjId = obj.getId();
        Set<String> mrEXISTOR = getExistingAttachmentsNames(obj.getId());
        final Collection<Attachment> addedAttachments = new ArrayList<>(issue.getAttachments().size());
        if (issue.getAttachments() != null && !issue.getAttachments().isEmpty()) {
            logger.debug("process attachments for case, id={}", caseObjId);
            List<CaseAttachment> caseAttachments = new ArrayList<>(issue.getAttachments().size());
            issue.getAttachments()
                    .stream()
                    .filter(x -> !mrEXISTOR.contains(x.getFileName()))
                    .forEach(x -> {
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
                                caseAttachments.add(new CaseAttachment(caseObjId, a.getId()));
                            }
                        } catch (Exception e) {
                            logger.debug("unable to process attachment {}", x.getFileName());
                            logger.debug("trace", e);
                        }
                    });
            caseAttachments.forEach(caseAttachmentDAO::saveOrUpdate);
        }

        eventPublisherService.publishEvent(new CaseAttachmentEvent(
                this,
                obj,
                addedAttachments,
                null,
                contactPerson
        ));
    }

    @Override
    public CaseComment processStoreComment(Issue issue, Person contactPerson, CaseObject obj, Long caseObjId, CaseComment comment) {
        comment.setCaseId(caseObjId);
        caseCommentDAO.persist(comment);

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
    public Person getAssignedPerson(Long companyId, User user, Issue issue) {

        Person person = null;

        if (HelperFunc.isEmpty(user.getMail())) {
            logger.debug("no contact data provided for request {}", issue.getId());
        }

        if (HelperFunc.isNotEmpty(user.getMail())) {
            // try find by e-mail
            person = personDAO.findContactByEmail(companyId, user.getMail());
        }


        if (person == null && HelperFunc.isNotEmpty(user.getFullName())) {
            // try find by name
            person = personDAO.findContactByName(companyId, user.getFullName());
        }


        if (person != null) {
            logger.debug("contact found: {} (id={}), request {}", person.getDisplayName(), person.getId(), issue.getId());
        } else {
            logger.debug("unable to find contact person : email={}, company={}, create new one", user.getMail(), companyId);

            person = new Person();
            person.setCreated(new Date());
            person.setCreator("redmine");
            person.setCompanyId(companyId);

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
