package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
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
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.redmine.handlers.RedmineNewIssueHandler;
import ru.protei.portal.redmine.utils.HttpInputSource;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        final Set<String> existingAttachmentsNames = getExistingAttachmentsNames(obj.getId());
        final Collection<Attachment> addedAttachments = new ArrayList<>(issue.getAttachments().size());
        if (issue.getAttachments() != null && !issue.getAttachments().isEmpty()) {
            logger.debug("process attachments for case, id={}", caseObjId);
            List<CaseAttachment> caseAttachments = new ArrayList<>(issue.getAttachments().size());
            issue.getAttachments()
                    .stream()
                    .filter(x -> !existingAttachmentsNames.contains(x.getFileName()))
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
                            Long caId = fileController.saveAttachment(a,
                                    new HttpInputSource(x.getContentURL(), endpoint.getApiKey()), caseObjId);
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
                caseService
                , this,
                obj,
                addedAttachments,
                null,
                contactPerson
        ));
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

    private Set<String> getExistingAttachmentsNames(long caseObjId) {
        return caseAttachmentDAO.getListByCaseId(caseObjId).stream()
                .map(CaseAttachment::getAttachmentId)
                .map(attachmentDAO::get)
                .map(Attachment::getFileName)
                .collect(Collectors.toSet());
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

    private static final Map<Character, String> cyrillicToLatin = new HashMap<Character, String>() {{
        //Cyrillic uppercase
        put('\u0410', "A");
        put('\u0411', "B");
        put('\u0412', "V");
        put('\u0413', "G");
        put('\u0414', "D");
        put('\u0415', "E");
        put('\u0416', "ZH");
        put('\u0417', "Z");
        put('\u0418', "I");
        put('\u0419', "Y");
        put('\u041A', "K");
        put('\u041B', "L");
        put('\u041C', "M");
        put('\u041D', "N");
        put('\u041E', "O");
        put('\u041F', "P");
        put('\u0420', "R");
        put('\u0421', "S");
        put('\u0422', "T");
        put('\u0423', "U");
        put('\u0424', "F");
        put('\u0425', "H");
        put('\u0426', "C");
        put('\u0427', "CH");
        put('\u0428', "SH");
        put('\u0429', "SCH");
        put('\u042A', "");
        put('\u042B', "I");
        put('\u042C', "");
        put('\u042D', "E");
        put('\u042E', "YU");
        put('\u042F', "YA");

        //Cyrillic lowercase
        put('\u0430', "a");
        put('\u0431', "b");
        put('\u0432', "v");
        put('\u0433', "g");
        put('\u0434', "d");
        put('\u0435', "e");
        put('\u0436', "zh");
        put('\u0437', "z");
        put('\u0438', "i");
        put('\u0439', "y");
        put('\u043A', "k");
        put('\u043B', "l");
        put('\u043C', "m");
        put('\u043D', "n");
        put('\u043E', "o");
        put('\u043F', "p");
        put('\u0440', "r");
        put('\u0441', "s");
        put('\u0442', "t");
        put('\u0443', "u");
        put('\u0444', "f");
        put('\u0445', "h");
        put('\u0446', "c");
        put('\u0447', "ch");
        put('\u0448', "sh");
        put('\u0449', "sch");
        put('\u044A', "");
        put('\u044B', "i");
        put('\u044C', "");
        put('\u044D', "e");
        put('\u044E', "yu");
        put('\u044F', "ya");
    }};

    private static final Set<Character> cyrillic = new HashSet<Character>() {{
        addAll(cyrillicToLatin.keySet());
    }};

    private final static Logger logger = LoggerFactory.getLogger(RedmineNewIssueHandler.class);
}
