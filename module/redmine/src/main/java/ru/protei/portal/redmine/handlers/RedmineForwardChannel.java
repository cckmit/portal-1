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
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.service.RedmineService;
import ru.protei.portal.redmine.utils.CachedPersonMapper;
import ru.protei.portal.redmine.utils.HttpInputSource;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isNotBlank;
import static ru.protei.portal.core.model.util.CrmConstants.Redmine.NO_CONTENT_TYPE;
import static ru.protei.portal.core.model.util.CrmConstants.Time.MINUTE;
import static ru.protei.portal.redmine.enums.RedmineChangeType.*;
import static ru.protei.portal.redmine.utils.CachedPersonMapper.isTechUser;

public class RedmineForwardChannel implements ForwardChannelEventHandler {

    @Override
    public void checkIssues() {
        logger.debug("Check for new issues stared");
        commonService.getEndpoints().ifOk( endpoints -> endpoints.forEach( this::checkForNewIssues ) );
        logger.debug("Check for new issues ended");

        logger.debug("Check for issues updates started");
        commonService.getEndpoints().ifOk( endpoints -> endpoints.forEach( this::checkForUpdatedIssues ) );
        logger.debug("Check for issues updates ended");
    }

    public static Date maxDate( Date a, Date b) {
        return a == null ? b : b == null ? a : a.after(b) ? a : b;
    }

    private String userInfo ( User user) {
        if (user == null)
            return "unknown";

        StringBuilder sb = new StringBuilder();
        sb.append("user[id=").append(user.getId()).append(",name=");
        if (user.getLastName() != null)
            sb.append(user.getLastName()).append(" ");
        else
            sb.append("? ");

        if (user.getFirstName() != null) {
            sb.append(user.getFirstName());
        }
        else
            sb.append("?");

        sb.append("]");
        return sb.toString();
    }

    private void checkForNewIssues(RedmineEndpoint endpoint) {
        Result<List<Issue>> issuesResult = redmineService.getNewIssues( endpoint );
        if (issuesResult.isError()) return;
        List<Issue> issues = issuesResult.getData();
        if (isEmpty(issues)) {
            logger.debug( "no new issues from {}", endpoint.getServerAddress() );
            return;
        }

        Date lastCreatedOn = endpoint.getLastCreatedOnDate();
        logger.debug( "Last new issue date from DB: {}", lastCreatedOn );
        logger.debug( "got {} issues from {}", issues.size(), endpoint.getServerAddress() );

        for (Issue issue : issues) {
            Result<User> userResult = redmineService.getUser( issue.getAuthorId(), endpoint );
            if (userResult.isError()) continue;
            User user = userResult.getData();

            logger.debug( "try handle new issue from {}, issue-id: {}", userInfo( user ), issue.getId() );

            createCaseObject( user, issue, endpoint );
            lastCreatedOn = maxDate( issue.getCreatedOn(), lastCreatedOn );
        }

        logger.debug( "max created on, taken from issues: {}", lastCreatedOn );

        // добавляем 2 секунды, иначе redmine будет возвращать опять нам последнюю запись
        // не очень это хорошо, нужно наверное все таки переделать получение новых issue
        // не только по дате, но еще и по ID
        lastCreatedOn = new Date( lastCreatedOn.getTime() + 2000 );

        logger.debug( "max created on, store in our db: {}", lastCreatedOn );

        endpoint.setLastCreatedOnDate( lastCreatedOn );
        commonService.updateCreatedOn( endpoint );
    }

    private void checkForUpdatedIssues(RedmineEndpoint endpoint) {
        Result<List<Issue>> issuesResult = redmineService.getUpdatedIssues( endpoint );
        if (issuesResult.isError()) return;
        List<Issue> issues = issuesResult.getData();

        if (isEmpty(issues)) {
            logger.debug("no changed issues from {}", endpoint.getServerAddress());
            return;
        }

        Date lastUpdatedOn = endpoint.getLastUpdatedOnDate();
        logger.debug("got {} updated issues from {}", issues.size(), endpoint.getServerAddress());

        for (Issue issue : issues) {
            if (Math.abs(issue.getUpdatedOn().getTime() - issue.getCreatedOn().getTime()) < 5 * MINUTE) {
                logger.debug("Skipping recently created issue with id {}", issue.getId());
                continue;
            }
            Result<User> userResult = redmineService.getUser( issue.getAuthorId(), endpoint );
            if (userResult.isError()) continue;
            User user = userResult.getData();

            if (user == null) {
                logger.debug("User with id {} not found, skipping it", issue.getAuthorId());
                continue;
            }

            logger.debug("try update issue from {}, issue-id: {}", userInfo(user), issue.getId());
            compareAndUpdate(user, issue, endpoint);
            lastUpdatedOn = maxDate(issue.getUpdatedOn(), lastUpdatedOn);
        }
        logger.debug("max update-date, taken from issues: {}", lastUpdatedOn);

        // use same trick as above (see check-new-issues)
        lastUpdatedOn = new Date(lastUpdatedOn.getTime() + 2000);

        logger.debug("max update-date, store in our db: {}", lastUpdatedOn);

        endpoint.setLastUpdatedOnDate(lastUpdatedOn);
        commonService.updateUpdatedOn(endpoint);
    }

    private void compareAndUpdate( User user, Issue issue, RedmineEndpoint endpoint ) {
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

    private CaseObject createCaseObject(User user, Issue issue, RedmineEndpoint endpoint) {
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
        Result<Long> stateCommentId = commonService.createAndStoreStateHistory(issue.getCreatedOn(), contactPerson.getId(), obj.getStateId(), caseObjId);
        if (stateCommentId.isError()) {
           logger.error("State comment for the issue {} not saved!", caseObjId);
            return null;
        }

        Result<Long> importanceCommentId = commonService.createAndStoreImportanceHistory(issue.getCreatedOn(), contactPerson.getId(), obj.getImpLevel(), caseObjId);
        if (importanceCommentId.isError()) {
            logger.error("Importance comment for the issue {} not saved!", caseObjId);
            return null;
        }

        final ExternalCaseAppData appData = new ExternalCaseAppData(obj);
        appData.setExtAppCaseId(issue.getId() + "_" + companyId);
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        logger.debug("create redmine-case id={}, ext={}, data={}", appData.getId(), appData.getExtAppCaseId(), appData.getExtAppData());
        commonService.mergeExtAppData(appData);

        publisherService.publishEvent(new CaseObjectCreateEvent(this, ServiceModule.REDMINE, contactPerson.getId(), obj));

        processComments(issue.getJournals(), obj, personMapper);
        processAttachments(issue.getAttachments(), obj, endpoint, personMapper);

        logger.debug("Object with id {} was created, guid={}", obj.getId(), obj.defGUID());
        return obj;
    }

    private void updateCaseObject(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        logger.trace( "issue(): {}", issue );
        CachedPersonMapper personMapper = commonService.getPersonMapper(endpoint);

        Date latestHistoryDate = commonService.getLatestHistoryDate(object.getId()).getData();
        Date latestCommentDate = commonService.getLatestCommentDate(object.getId()).getData();
        Date latestSynchronizedDate = getLatestSynchronizedDate(latestHistoryDate, latestCommentDate, issue.getCreatedOn());
        logger.info("Last sync date {}", latestSynchronizedDate);

        List<Journal> latestJournals = selectLatestJournals( issue.getJournals(), latestSynchronizedDate, endpoint.getDefaultUserId() );
        logger.debug("Got {} journals after {}", latestJournals.size(), latestSynchronizedDate);

        //Synchronize comments, status, priority, name, info
        for (Journal journal : latestJournals) {
            logger.trace( "updateCaseObject(): {}", journal );

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
        processAttachments(issue.getAttachments(), object, endpoint, personMapper);
    }

    private void processComments( Collection<Journal> journals, CaseObject object, CachedPersonMapper personMapper ) {
        logger.debug("Process comments for case with id {}", object.getId());
        stream(journals)
                .filter(journal -> StringUtils.isNotBlank(journal.getNotes()))
                .forEach(journal -> publishEvents(
                        commonService.updateComment(object.getId(), journal.getCreatedOn(), journal.getNotes(), personMapper.toProteiPerson(journal.getUser()) )
                ) );
    }

    private void processAttachments( Collection<Attachment> attachments, CaseObject caseObject, RedmineEndpoint endpoint, CachedPersonMapper personMapper ) {
        if (isEmpty( attachments )) {
            logger.debug("No attachments to process for case with id {}, attachment", caseObject.getId());
            return;
        }

        Set<Integer> existingAttachmentsHashCodes = commonService.getExistingAttachmentsHashCodes( caseObject.getId() ).orElseGet( ignore -> ok(new HashSet<>()) ).getData();
        logger.debug("Process attachments for case with id {}, exists {} attachment", caseObject.getId(), existingAttachmentsHashCodes.size());

        for (com.taskadapter.redmineapi.bean.Attachment attachment : emptyIfNull( attachments )) {
            if (personMapper.isTechUser( endpoint.getDefaultUserId(), attachment.getAuthor() )) continue;
            if (existingAttachmentsHashCodes.contains( toHashCode( attachment ) )) continue;

            final Person author = personMapper.toProteiPerson( attachment.getAuthor() );
            ru.protei.portal.core.model.ent.Attachment a = new ru.protei.portal.core.model.ent.Attachment();
            a.setCreated( attachment.getCreatedOn() );
            a.setCreatorId( author.getId() );
            a.setDataSize( attachment.getFileSize() );
            a.setFileName( attachment.getFileName() );
            a.setMimeType( attachment.getContentType() != null ? attachment.getContentType() : NO_CONTENT_TYPE );
            a.setLabelText( attachment.getDescription() );


            Result<Long> saveResult = commonService.saveAttachment( a, author,
                    new HttpInputSource( attachment.getContentURL(), endpoint.getApiKey() ),
                    attachment.getFileSize(), attachment.getContentType(), caseObject );

            publishEvents( saveResult );
        }
    }

    private void publishEvents( Result<Long> saveResult ) {
        if (saveResult.isError()) return;
        for (ApplicationEvent event : emptyIfNull( saveResult.getEvents() )) {
            logger.info( "publishEvents(): {}", event );
            publisherService.publishEvent( event );
        }
    }

    private int toHashCode(com.taskadapter.redmineapi.bean.Attachment attachment){
        return ((attachment.getCreatedOn() == null ? "" : attachment.getCreatedOn().getTime()) + (attachment.getFileName() == null ? "" : attachment.getFileName())).hashCode();
    }

    private Optional<JournalDetail> find( List<JournalDetail> details, RedmineChangeType type ) {
        return CollectionUtils.find( details, detail -> Objects.equals( type.getName(), detail.getName() ) );
    }


    private List<Journal> selectLatestJournals(Collection<Journal> journals, Date latestCreated, Integer defaultUserId) {
        return journals.stream()
                .filter(Objects::nonNull)
                .filter(journal -> !isTechUser(defaultUserId, journal.getUser()))
                .filter(journal -> journal.getCreatedOn() != null && journal.getCreatedOn().compareTo(latestCreated) > 0)
                .sorted(Comparator.comparing( Journal::getCreatedOn))
                .collect(Collectors.toList());
    }

    private Date getLatestSynchronizedDate(Date latestHistoryDate, Date latestCommentDate, Date createdOnDate) {
        if (latestHistoryDate == null && latestCommentDate == null) {
            return createdOnDate;
        }
        if (latestHistoryDate == null) {
            return latestCommentDate.after(createdOnDate) ? latestCommentDate : createdOnDate;
        }
        if (latestCommentDate == null) {
            return latestHistoryDate.after(createdOnDate) ? latestHistoryDate : createdOnDate;
        }
        return latestHistoryDate.after(latestCommentDate) ? latestHistoryDate : latestCommentDate;
    }

    private CaseObject buildCaseObject( Issue issue, Person contactPerson, final long companyId, final long priorityMapId, final long statusMapId ) {

        final CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreatedOn());
        obj.setModified(issue.getUpdatedOn());
        obj.setCreator(contactPerson);
        obj.setInitiator(contactPerson);
        obj.setType(En_CaseType.CRM_SUPPORT);
        obj.setExtAppType(En_ExtAppType.REDMINE.getCode());

        logger.debug("Trying to get portal priority level id matching with redmine {}", issue.getPriorityId());
        final RedminePriorityMapEntry redminePriorityMapEntry = commonService.getByRedminePriorityId(issue.getPriorityId(), priorityMapId).getData();
        if (redminePriorityMapEntry != null) {
            obj.setImpLevel(redminePriorityMapEntry.getLocalPriorityId());
        } else {
            logger.warn( "Priority level not found, setting default" );
            obj.setImpLevel(CrmConstants.ImportanceLevel.BASIC);
        }

        logger.debug("Trying to get portal status id matching with redmine {}", issue.getStatusId());
        final RedmineToCrmEntry redmineStatusMapEntry = commonService.getLocalStatus(statusMapId, issue.getStatusId()).getData();
        if (redmineStatusMapEntry != null) {
            obj.setStateId(redmineStatusMapEntry.getLocalStatusId());
        } else {
            logger.warn("Object status was not found, setting default");
            obj.setStateId(CrmConstants.State.CREATED);
        }

        obj.setName(issue.getSubject());
        obj.setInfo(issue.getDescription());
        obj.setInitiatorCompanyId(companyId);
        obj.setManagerCompanyId(CrmConstants.Company.HOME_COMPANY_ID);

        List<Platform> platforms = commonService.getPlatforms(obj.getInitiatorCompanyId());
        if (platforms != null && platforms.size() == 1) {
            obj.setPlatformId(platforms.get(0).getId());
        }

        return obj;
    }

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedmineService redmineService;

    @Autowired
    private EventPublisherService publisherService;

    private final static Logger logger = LoggerFactory.getLogger( RedmineForwardChannel.class);
}
