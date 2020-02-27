package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalDetail;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseObjectCreateEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.CachedPersonMapper;

import java.util.*;

import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isNotBlank;
import static ru.protei.portal.redmine.enums.RedmineChangeType.*;
import static ru.protei.portal.redmine.utils.CachedPersonMapper.*;

public class RedmineForwardIssueChannel {

    public void compareAndUpdate(User user, Issue issue, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.getByExternalAppCaseId(issue.getId() + "_" + endpoint.getCompanyId());
        if (object != null) {
            logger.debug("Found case object with id {}", object.getId());
            updateCaseObject(issue, object, endpoint);
            logger.debug("Object with id {} saved", object.getId());
        } else {
            logger.debug("Object with external app id {} is not found; starting it's creation", issue.getId());
            createCaseObject(user, issue, endpoint);
        }
    }

    public CaseObject createCaseObject(User user, Issue issue, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        logger.debug("Starting creating case object for issue id {}, user id {}, company id {}", issue.getId(), user.getId(), companyId);

        final CachedPersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint, null);
        logger.debug("Creating case object ...");

        final CaseObject testExists = caseObjectDAO.getByExternalAppCaseId(issue.getId().toString() + "_" + companyId );
        if (testExists != null) {
            logger.debug("issue {} is already created as case-object {}", issue.getId(), testExists.defGUID());
            return testExists;
        }

        Person contactPerson = personMapper.toProteiPerson(user);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id = {} from project with id = {}", issue.getId(), issue.getProjectId());
            return null;
        }

        final CaseObject obj = buildCaseObject( issue, contactPerson, endpoint.getCompanyId(), endpoint.getPriorityMapId(), endpoint.getStatusMapId() );
        final long caseObjId = caseObjectDAO.insertCase(obj);

        Long stateCommentId = commonService.createAndStoreStateComment(issue.getCreatedOn(), contactPerson.getId(), obj.getStateId(), caseObjId);
        if (stateCommentId == null) {
            logger.error("State comment for the issue {} not saved!", caseObjId);
        }

        Long importanceCommentId = commonService.createAndStoreImportanceComment(issue.getCreatedOn(), contactPerson.getId(), obj.getImpLevel(), caseObjId);
        if (importanceCommentId == null) {
            logger.error("Importance comment for the issue {} not saved!", caseObjId);
        }

        final ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(issue.getId() + "_" + companyId);
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        logger.debug("create redmine-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());
        externalCaseAppDAO.merge(appData);

        // TODO Почему до processComments and processAttachments ?
        publisherService.publishEvent(new CaseObjectCreateEvent(this, ServiceModule.REDMINE, contactPerson.getId(), obj));

        commonService.processComments(issue.getJournals(), personMapper, obj);
        commonService.processAttachments(issue, personMapper, obj, endpoint);

        if (obj != null) {
            logger.debug("Object with id {} was created, guid={}", obj.getId(), obj.defGUID());
        }
        return obj;
    }

    private void updateCaseObject(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        CachedPersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint, null);

        List<CaseComment> caseComments = caseCommentDAO.getCaseComments( new CaseCommentQuery( object.getId() ) );
        Date latestCreated = findLatestSynchronizedCommentDate(caseComments, issue.getCreatedOn());
        logger.info("Last comment was synced on {}", latestCreated);

        List<Journal> latestJournals = selectLatestsJournals( issue.getJournals(), latestCreated, endpoint.getDefaultUserId() );
        logger.debug("Got {} journals after {}", latestJournals.size(), endpoint.getLastUpdatedOnDate());

        //Synchronize comments, status, priority, name, info
        for (Journal journal : latestJournals) {
            if (isNotBlank( journal.getNotes() )) {
//                getUpdater(RedmineChangeType.COMMENT).apply( object, endpoint, journal, null, personMapper );//TODO dublicate Comments see below
                commonService.updateComment( object.getId(), journal.getCreatedOn(), journal.getNotes(), personMapper.toProteiPerson( journal.getUser() ) );
            }

            List<JournalDetail> details = journal.getDetails();

            find( details, STATUS_CHANGE ).ifPresent( detail ->
                    commonService.updateCaseStatus( object, endpoint.getStatusMapId(), journal.getCreatedOn(), detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) );

            find( details, PRIORITY_CHANGE ).ifPresent( detail ->
                    commonService.updateCasePriority( object, endpoint.getPriorityMapId(), journal, detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) );

            find( details, DESCRIPTION_CHANGE ).ifPresent( detail ->
                    commonService.updateCaseDescription( object, detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) );

            find( details, SUBJECT_CHANGE ).ifPresent( detail ->
                    commonService.updateCaseSubject( object, detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) );

            find( details, COMMENT ).ifPresent( detail ->
                    commonService.updateComment( object.getId(), journal.getCreatedOn(), journal.getNotes(), personMapper.toProteiPerson( journal.getUser() ) ) );

//            for (JournalDetail detail : journal.getDetails()) {
////                RedmineChangeType.findByName( detail.getName() )
////                        .ifPresent( type -> getUpdater( type ).apply( object, endpoint, journal, detail.getNewValue(), personMapper )
////                        );
//                RedmineChangeType.findByName( detail.getName() ).ifPresent( type -> {
//                            switch (type) {
//                                case STATUS_CHANGE:
//                                    commonService.updateCaseStatus(object, endpoint.getStatusMapId(), journal.getCreatedOn(), detail.getNewValue(), personMapper.toProteiPerson(journal.getUser()) );
//                                    break;
//                                case PRIORITY_CHANGE:
//                                    commonService.updateCasePriority(object, endpoint.getPriorityMapId(), journal, detail.getNewValue(), personMapper.toProteiPerson(journal.getUser()) );
//                                    break;
//                                case DESCRIPTION_CHANGE:
//                                    commonService.updateCaseDescription(object, detail.getNewValue(), personMapper.toProteiPerson(journal.getUser()) );
//                                    break;
//                                case SUBJECT_CHANGE:
//                                    commonService.updateCaseSubject(object, detail.getNewValue(), personMapper.toProteiPerson(journal.getUser()) );
//                                    break;
//                                case COMMENT:
//                                    commonService.updateComment(object.getId(), journal.getCreatedOn(), journal.getNotes(), personMapper.toProteiPerson(journal.getUser()) );
//                                    break;
//                                default:
//
//                            }
//                        }
//                );
//            }
        }

        //Synchronize attachment
        commonService.processAttachments(issue, personMapper, object, endpoint);
    }

    private Optional<JournalDetail> find( List<JournalDetail> details, RedmineChangeType type ) {
        return CollectionUtils.find( details, detail -> Objects.equals( type.getName(), detail.getName() ) );
    }


    private List<Journal> selectLatestsJournals( Collection<Journal> journals, Date latestCreated, Integer defaultUserId ) {
        return journals.stream()
                .filter( Objects::nonNull)
                .filter(journal -> !isTechUser(defaultUserId, journal.getUser()))
                .filter(journal -> journal.getCreatedOn() != null && journal.getCreatedOn().compareTo(latestCreated) > 0)
                .sorted( Comparator.comparing( Journal::getCreatedOn))
                .collect( Collectors.toList());
    }

    private Date findLatestSynchronizedCommentDate( List<CaseComment> caseComments, Date createdOnDate) {
        return caseComments.stream()
                .filter(comment -> "redmine".equals( comment.getAuthor().getCreator() ))
                .max( Comparator.comparing( CaseComment::getCreated))
                .map(CaseComment::getCreated)
                .orElse(createdOnDate);
    }

    private CaseObject buildCaseObject( Issue issue, Person contactPerson, final long companyId, final long priorityMapId, final long statusMapId ) {

        final CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
        obj.setCreator(contactPerson);
        obj.setInitiator(contactPerson);
        obj.setCaseType(En_CaseType.CRM_SUPPORT);
        obj.setExtAppType(En_ExtAppType.REDMINE.getCode());

        logger.debug("Trying to get portal priority level id matching with redmine {}", issue.getPriorityId());
        final RedminePriorityMapEntry redminePriorityMapEntry = priorityMapEntryDAO.getByRedminePriorityId(issue.getPriorityId(), priorityMapId);
        if (redminePriorityMapEntry != null) {
            obj.setImpLevel(redminePriorityMapEntry.getLocalPriorityId());
        } else {
            logger.warn( "Priority level not found, setting default" );
            obj.setImpLevel(En_ImportanceLevel.BASIC.getId());
        }

        logger.debug("Trying to get portal status id matching with redmine {}", issue.getStatusId());
        final RedmineToCrmEntry redmineStatusMapEntry = statusMapEntryDAO.getLocalStatus(statusMapId, issue.getStatusId());
        if (redmineStatusMapEntry != null) {
            obj.setStateId(redmineStatusMapEntry.getLocalStatusId());
        } else {
            logger.warn("Object status was not found, setting default");
            obj.setStateId(En_CaseState.CREATED.getId());
        }

        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(companyId);
        return obj;
    }

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineToCrmStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger( RedmineForwardIssueChannel.class);
}
