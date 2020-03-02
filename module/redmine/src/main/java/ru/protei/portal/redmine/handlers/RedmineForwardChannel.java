package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Attachment;
import com.taskadapter.redmineapi.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseObjectCreateEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.CachedPersonMapper;
import ru.protei.portal.redmine.utils.HttpInputSource;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isNotBlank;
import static ru.protei.portal.redmine.enums.RedmineChangeType.*;
import static ru.protei.portal.redmine.utils.CachedPersonMapper.isTechUser;

public class RedmineForwardChannel implements ForwardChannelEventHandler {

    @Override
    public void compareAndUpdate( User user, Issue issue, RedmineEndpoint endpoint ) {
        final Result<CaseObject> object = commonService.getByExternalAppCaseId(issue.getId() + "_" + endpoint.getCompanyId());
        if (object.isOk()) {
            logger.debug("Found case object with id {}", object.getData().getId());
            updateCaseObject(issue, object.getData(), endpoint);
            logger.debug("Object with id {} saved", object.getData().getId());
        } else {
            logger.debug("Object with external app id {} is not found; starting it's creation", issue.getId());
            createCaseObject(user, issue, endpoint);
        }
    }

    @Override
    public CaseObject createCaseObject(User user, Issue issue, RedmineEndpoint endpoint) {
        final long companyId = endpoint.getCompanyId();
        logger.info("Starting creating case object for issue id {}, user id {}, company id {}", issue.getId(), user.getId(), companyId);

        final Result<CaseObject> testExists = commonService.getByExternalAppCaseId(issue.getId().toString() + "_" + companyId );
        if (testExists.isOk()) {
            logger.debug("issue {} is already created as case-object {}", issue.getId(), testExists.getData().defGUID());
            return testExists.getData();
        }

        final CachedPersonMapper personMapper = commonService.getPersonMapper( endpoint );
        Person contactPerson = personMapper.toProteiPerson(user);
        if (contactPerson == null) {
            logger.debug("no assigned person for issue with id = {} from project with id = {}", issue.getId(), issue.getProjectId());
            return null;
        }

        final CaseObject obj = buildCaseObject( issue, contactPerson, endpoint.getCompanyId(), endpoint.getPriorityMapId(), endpoint.getStatusMapId() );
        final Result<Long> saveResult = commonService.saveCase(obj);
        if(saveResult.isError()){
            logger.debug("Can't create case object {}", saveResult);
            return null;
        }

        Long caseObjId = saveResult.getData();
        Result<Long> stateCommentId = commonService.createAndStoreStateComment(issue.getCreatedOn(), contactPerson.getId(), obj.getStateId(), caseObjId);
        if (stateCommentId.isError()) {
            logger.error("State comment for the issue {} not saved!", caseObjId);
        }

        Result<Long> importanceCommentId = commonService.createAndStoreImportanceComment(issue.getCreatedOn(), contactPerson.getId(), obj.getImpLevel(), caseObjId);
        if (importanceCommentId.isError()) {
            logger.error("Importance comment for the issue {} not saved!", caseObjId);
        }

        final ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(issue.getId() + "_" + companyId);
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        logger.debug("create redmine-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());
        commonService.mergeExtAppData(appData);

        publisherService.publishEvent(new CaseObjectCreateEvent(this, ServiceModule.REDMINE, contactPerson.getId(), obj));

        processComments(issue.getJournals(), obj, endpoint);
        processAttachments(issue.getAttachments(), obj.getId(), endpoint);

        if (obj != null) {
            logger.debug("Object with id {} was created, guid={}", obj.getId(), obj.defGUID());
        }
        return obj;
    }

    private void updateCaseObject(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        CachedPersonMapper personMapper = commonService.getPersonMapper(endpoint);

        List<CaseComment> caseComments = commonService.getCaseComments( new CaseCommentQuery( object.getId() ) ).getData();
        Date latestCreated = findLatestSynchronizedCommentDate(caseComments, issue.getCreatedOn());
        logger.info("Last comment was synced on {}", latestCreated);

        List<Journal> latestJournals = selectLatestsJournals( issue.getJournals(), latestCreated, endpoint.getDefaultUserId() );
        logger.debug("Got {} journals after {}", latestJournals.size(), endpoint.getLastUpdatedOnDate());

        //Synchronize comments, status, priority, name, info
        for (Journal journal : latestJournals) {

            List<JournalDetail> details = journal.getDetails();

            if (isNotBlank( journal.getNotes() )) {
                publishEvents(
                        commonService.updateComment( object.getId(), journal.getCreatedOn(), journal.getNotes(), personMapper.toProteiPerson( journal.getUser() ) ) );
            }

            find( details, STATUS_CHANGE ).ifPresent( detail -> publishEvents(
                    commonService.updateCaseStatus( object, endpoint.getStatusMapId(), journal.getCreatedOn(), detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) ) );

            find( details, PRIORITY_CHANGE ).ifPresent( detail -> publishEvents(
                    commonService.updateCasePriority( object, endpoint.getPriorityMapId(), journal, detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) ) );

            find( details, DESCRIPTION_CHANGE ).ifPresent( detail -> publishEvents(
                    commonService.updateCaseDescription( object, detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) ) );

            find( details, SUBJECT_CHANGE ).ifPresent( detail -> publishEvents(
                    commonService.updateCaseSubject( object, detail.getNewValue(), personMapper.toProteiPerson( journal.getUser() ) ) ) );

        }

        //Synchronize attachment
        processAttachments(issue.getAttachments(), object.getId(), endpoint);
    }

    private void processComments( Collection<Journal> journals, CaseObject object, RedmineEndpoint endpoint ) {
        logger.debug("Process comments for case with id {}", object.getId());
        CachedPersonMapper personMapper = commonService.getPersonMapper( endpoint );
        stream(journals)
                .filter(journal -> StringUtils.isNotBlank(journal.getNotes()))
                .forEach(journal ->
                        commonService.updateComment(object.getId(), journal.getCreatedOn(), journal.getNotes(), personMapper.toProteiPerson(journal.getUser()) )
                );
    }

    private void processAttachments( Collection<Attachment> attachments, Long caseObjId, RedmineEndpoint endpoint ) {
        if (isEmpty( attachments )) {
            logger.debug("No attachments to process for case with id {}, attachment", caseObjId);
            return;
        }

        Set<Integer> existingAttachmentsHashCodes = commonService.getExistingAttachmentsHashCodes( caseObjId ).orElseGet( ignore -> ok(new HashSet<Integer>()) ).getData();
        CachedPersonMapper personMapper = commonService.getPersonMapper( endpoint );
        logger.debug("Process attachments for case with id {}, exists {} attachment", caseObjId, existingAttachmentsHashCodes.size());

        for (com.taskadapter.redmineapi.bean.Attachment attachment : emptyIfNull( attachments )) {
            if (personMapper.isTechUser( endpoint.getDefaultUserId(), attachment.getAuthor() )) continue;
            if (existingAttachmentsHashCodes.contains( toHashCode( attachment ) )) continue;

            final Person author = personMapper.toProteiPerson( attachment.getAuthor() );
            ru.protei.portal.core.model.ent.Attachment a = new ru.protei.portal.core.model.ent.Attachment();
            a.setCreated( attachment.getCreatedOn() );
            a.setCreatorId( author.getId() );
            a.setDataSize( attachment.getFileSize() );
            a.setFileName( attachment.getFileName() );
            a.setMimeType( attachment.getContentType() );
            a.setLabelText( attachment.getDescription() );


            Result<Long> saveResult = commonService.saveAttachment( a, author,
                    new HttpInputSource( attachment.getContentURL(), endpoint.getApiKey() ),
                    attachment.getFileSize(), attachment.getContentType(), caseObjId );

            publishEvents( saveResult );
        }
    }

    private void publishEvents( Result<Long> saveResult ) {
        if (saveResult.isError()) return;
        for (ApplicationEvent event : emptyIfNull( saveResult.getEvents() )) {
            publisherService.publishEvent( event );
        }
    }

    private int toHashCode(com.taskadapter.redmineapi.bean.Attachment attachment){
        return ((attachment.getCreatedOn() == null ? "" : attachment.getCreatedOn().getTime()) + (attachment.getFileName() == null ? "" : attachment.getFileName())).hashCode();
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
        return stream(caseComments)
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
        final RedminePriorityMapEntry redminePriorityMapEntry = commonService.getByRedminePriorityId(issue.getPriorityId(), priorityMapId).getData();
        if (redminePriorityMapEntry != null) {
            obj.setImpLevel(redminePriorityMapEntry.getLocalPriorityId());
        } else {
            logger.warn( "Priority level not found, setting default" );
            obj.setImpLevel(En_ImportanceLevel.BASIC.getId());
        }

        logger.debug("Trying to get portal status id matching with redmine {}", issue.getStatusId());
        final RedmineToCrmEntry redmineStatusMapEntry = commonService.getLocalStatus(statusMapId, issue.getStatusId()).getData();
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
    private CommonService commonService;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger( RedmineForwardChannel.class);
}
