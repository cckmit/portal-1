package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Attachment;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.CachedPersonMapper;

import java.util.*;
import java.util.stream.Collectors;

public class RedmineUpdateIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.getByExternalAppCaseId(issue.getId() + "_" + endpoint.getCompanyId());
        if (object != null) {
            logger.debug("Found case object with id {}", object.getId());
            compareAndUpdate(issue, object, endpoint);
            logger.debug("Object with id {} saved", object.getId());
        } else {
            logger.debug("Object with external app id {} is not found; starting it's creation", issue.getId());
            newIssueHandler.handle(user, issue, endpoint);
        }
    }

    public void handleUpdateCreationDateAttachments(Issue issue, Long caseObjId) {
        commonService.processUpdateCreationDateAttachments(issue, caseObjId);
    }

    public void handleUpdateAttachmentsByIssue(Issue issue, Long caseId, RedmineEndpoint endpoint) {
        final CachedPersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint, null);
        final CaseObject object = caseObjectDAO.get(caseId);
        commonService.processAttachments(issue.getAttachments(), personMapper, object, endpoint);
    }

    public void handleUpdateCaseObjectByIssue(Issue issue, Long caseId, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.get(caseId);
        compareAndUpdate(issue, object, endpoint);
    }

    public void handleUpdatePriorityByIssue(Issue issue, Long caseId, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.get(caseId);
        issue.getJournals()
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Journal::getCreatedOn))
                .forEach(journal -> journal.getDetails()
                        .stream()
                        .filter(detail -> detail.getName().equals(RedmineChangeType.PRIORITY_CHANGE.getName()))
                        .forEach(detail -> {
                            caseUpdaterFactory
                                    .getUpdater(RedmineChangeType.findByName(detail.getName()).get())
                                    .apply(object, endpoint, journal, detail.getNewValue(), null);
                        }));
    }

    private void compareAndUpdate(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        CachedPersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint, null);

        logger.debug("Trying to get latest synchronized comment");
        Date latestCreated = caseCommentDAO.getCaseComments(new CaseCommentQuery(object.getId())).stream()
                .filter(x -> x.getAuthor().getCreator().equals("redmine"))
                .max(Comparator.comparing(CaseComment::getCreated))
                .map(CaseComment::getCreated)
                .orElse(issue.getCreatedOn());
        logger.debug("Last comment was synced on {}", latestCreated);

        List<Journal> latestJournals = issue.getJournals().stream()
                .filter(Objects::nonNull)
                .filter(journal -> !journal.getUser().getId().equals(endpoint.getDefaultUserId()))
                .filter(journal -> journal.getCreatedOn() != null && journal.getCreatedOn().compareTo(latestCreated) > 0)
                .sorted(Comparator.comparing(Journal::getCreatedOn))
                .collect(Collectors.toList());
        logger.debug("Got {} journals after {}", latestJournals.size(), endpoint.getLastUpdatedOnDate());

        //Synchronize comments
        latestJournals.forEach(journal -> {
            if (journal.getNotes() != null) {
                caseUpdaterFactory.getCommentsUpdater().apply(object, endpoint, journal, null, personMapper);
            }
            journal.getDetails().forEach(detail ->
                    RedmineChangeType.findByName(detail.getName())
                            .ifPresent(type -> caseUpdaterFactory.getUpdater(type).apply(object, endpoint, journal, detail.getNewValue(), personMapper)
            ));
        });

        //Synchronize attachment
        logger.debug("Trying to get latest synchronized attachment");
        PersonQuery personQuery = new PersonQuery();
        personQuery.setCompanyIds(new HashSet<>(Collections.singletonList(endpoint.getCompanyId())));
        List<Long> redminePersons = personDAO.getPersons(personQuery).stream().map(Person::getId).collect(Collectors.toList());

        Date latestCreatedAttach = attachmentDAO.getListByCaseId(object.getId()).stream()
                .filter(attachment -> redminePersons.contains(attachment.getCreatorId()))
                .max(Comparator.comparing(ru.protei.portal.core.model.ent.Attachment::getCreated))
                .map(ru.protei.portal.core.model.ent.Attachment::getCreated)
                .orElse(issue.getCreatedOn());
        logger.debug("Last attachment was synced on {}", latestCreated);

        List<Attachment> latestAttachments = issue.getAttachments().stream()
                .filter(Objects::nonNull)
                .filter(attachment -> !attachment.getAuthor().getId().equals(endpoint.getDefaultUserId()))
                .filter(attachment -> attachment.getCreatedOn() != null && attachment.getCreatedOn().compareTo(latestCreatedAttach) > 0)
                .sorted(Comparator.comparing(Attachment::getCreatedOn))
                .collect(Collectors.toList());
        commonService.processAttachments(latestAttachments, personMapper, object, endpoint);
    }

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CaseUpdaterFactory caseUpdaterFactory;

    @Autowired
    private RedmineNewIssueHandler newIssueHandler;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);
}
