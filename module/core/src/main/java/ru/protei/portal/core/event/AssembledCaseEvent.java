package ru.protei.portal.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AssembledCaseEvent extends ApplicationEvent {

    private Long caseObjectId;
    private CaseObject lastState;
    private CaseObject initState;
    private ApplicationEvent fromEvent;
    private CaseObjectMeta lastMetaState;
    private CaseObjectMeta initMetaState;
    private DiffCollectionResult <CaseLink> mergeLinks = new DiffCollectionResult<>();
    private DiffCollectionResult <Attachment> attachments = new DiffCollectionResult<>();
    private DiffCollectionResult <CaseComment> comments = new DiffCollectionResult<>();

    private Person initiator;
    private ServiceModule serviceModule;
    private List<CaseComment> existingComments;
    private boolean isEagerEvent;
    // Measured in ms
    private final long timeCreated;
    protected long lastUpdated;

    public AssembledCaseEvent(AbstractCaseEvent ace) {
        this(ace.getSource(), ace.getServiceModule(), ace.getCaseObjectId(), ace.getPerson(), ace.isEagerEvent());
    }

    private AssembledCaseEvent( Object source, ServiceModule serviceModule, Long caseObjectId, Person initiator, boolean isEagerEvent ) {
        super( source );
        this.caseObjectId = caseObjectId;
        this.initiator = initiator;
        this.serviceModule = serviceModule;
        this.timeCreated = currentTimeMillis();
        lastUpdated = timeCreated;
        this.isEagerEvent = isEagerEvent;
        this.fromEvent = this;
    }

    public void attachCaseObjectEvent( CaseObjectEvent objectEvent ) {
        this.lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent||objectEvent.isEagerEvent();
        this.initState = objectEvent.getOldState();
        this.lastState = objectEvent.getNewState();
        this.initiator = objectEvent.getPerson();
        this.serviceModule = objectEvent.getServiceModule();
        this.mergeLinks.putAddedEntries(objectEvent.getIssueCreateRequest().getLinks());
        this.fromEvent = objectEvent;
    }

    public void attachCaseObjectMetaEvent( CaseObjectMetaEvent event ) {
        lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent || event.isEagerEvent();
        initMetaState = event.getOldState();
        lastMetaState = event.getNewState();
        initiator = event.getPerson();
        serviceModule = event.getServiceModule();
        this.fromEvent = event;
    }

    public void attachLinkEvent( CaseLinksEvent event ) {
        this.lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent||event.isEagerEvent();
        mergeLinks = synchronizeDiffs(mergeLinks, event.getMergeLinks(), CaseLink::getId );
        this.fromEvent = event;
    }

    public void attachCommentEvent( CaseCommentEvent commentEvent ) {
        this.lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent || commentEvent.isEagerEvent();
        if (commentEvent.getOldCaseComment() != null) {
            comments.putChangedEntry( commentEvent.getOldCaseComment(), commentEvent.getNewCaseComment() );
        } else if (commentEvent.getNewCaseComment() != null) {
            comments.putAddedEntry( commentEvent.getNewCaseComment() );
        }
        if (commentEvent.getRemovedCaseComment() != null) {
            comments.putRemovedEntry( commentEvent.getRemovedCaseComment() );
        }
        this.fromEvent = commentEvent;
    }

    public void attachAttachmentEvent( CaseAttachmentEvent event ) {
        this.lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent||event.isEagerEvent();
        attachments = synchronizeDiffs( attachments, event.getAttachments(), Attachment::getId );
        this.fromEvent = event;
    }

    public boolean isCreateEvent() {
        if ( !(this.fromEvent instanceof CaseObjectEvent) ) {
            return false;
        }

        if ( !( (CaseObjectEvent) this.fromEvent ).isCreateEvent() ) {
            return false;
        }

        return true;
    }

    private boolean isUpdateEvent() {
        return !isCreateEvent();
    }

    private boolean isUpdateEventMeta() {
        return initMetaState != null && lastMetaState != null;
    }

    public boolean isCommentAttached() {
        return comments.hasAdded();
    }

    public boolean isCommentRemoved() {
        return comments.hasRemovedEntries();
    }

    public boolean isCaseStateChanged() {
        return isUpdateEventMeta() && lastMetaState.getState() != initMetaState.getState();
    }

    public boolean isTimeElapsedChanged() {
        return isUpdateEventMeta() && !HelperFunc.equals(lastMetaState.getTimeElapsed(), initMetaState.getTimeElapsed());
    }

    public boolean isCaseImportanceChanged() {
        return isUpdateEventMeta() && !lastMetaState.getImpLevel().equals(initMetaState.getImpLevel());
    }

    public boolean isManagerChanged() {
        return isUpdateEventMeta() && !HelperFunc.equals(lastMetaState.getManagerId(), initMetaState.getManagerId());
    }

    public boolean isProductChanged() {
        return isUpdateEventMeta() && !HelperFunc.equals(lastMetaState.getProductId(), initMetaState.getProductId());
    }

    public boolean isInitiatorChanged() {
        return isUpdateEventMeta() && !HelperFunc.equals(lastMetaState.getInitiatorId(), initMetaState.getInitiatorId());
    }

    public boolean isInitiatorCompanyChanged() {
        return isUpdateEventMeta() && !HelperFunc.equals(lastMetaState.getInitiatorCompanyId(), initMetaState.getInitiatorCompanyId());
    }

    public boolean isInfoChanged() {
        return isUpdateEvent() && initState != null && !HelperFunc.equals(lastState.getInfo(), initState.getInfo());
    }

    public boolean isNameChanged() {
        return isUpdateEvent() && initState != null && !HelperFunc.equals(lastState.getName(), initState.getName());
    }

    public boolean isPlatformChanged() {
        return isUpdateEventMeta() && !HelperFunc.equals(lastMetaState.getPlatformId(), initMetaState.getPlatformId());
    }

    public boolean isPublicLinksChanged() {
        return isUpdateEvent() && publicLinksChanged();
    }

    private boolean publicLinksChanged() {
        if (!CollectionUtils.isEmpty(mergeLinks.getAddedEntries()) && mergeLinks.getAddedEntries().stream().anyMatch(caseLink -> !caseLink.isPrivate())) {
            return true;
        }

        if (!CollectionUtils.isEmpty(mergeLinks.getRemovedEntries()) && mergeLinks.getRemovedEntries().stream().anyMatch(caseLink -> !caseLink.isPrivate())) {
            return true;
        }

        return false;
    }

    public boolean isEagerEvent() {
        return isEagerEvent;
    }

    public boolean isLinksFilled() {
        synchronized (mergeLinks){
            return mergeLinks.hasSameEntries();
        }
    }

    public boolean isAttachmentsFilled() {
        synchronized (attachments){
            return attachments.hasSameEntries();
        }
    }

    @Deprecated
    public void includeCaseComments (List<CaseComment> commentList) {
        comments.putAddedEntries( commentList );
        lastUpdated = currentTimeMillis();
    }

    @Deprecated
    public void includeCaseAttachments (List<Attachment> attachments1) {
        this.lastUpdated = currentTimeMillis();
        DiffCollectionResult<Attachment> diff = new DiffCollectionResult<>();
        diff.putAddedEntries( attachments1 );
        attachments = synchronizeDiffs( attachments, diff, Attachment::getId );
    }

    public List<CaseComment> getAddedCaseComments() {
        return comments.getAddedEntries();
    }

    public List<CaseComment> getChangedComments() {
        if(!comments.hasChanged()) return Collections.EMPTY_LIST;
        return comments.getChangedEntries().stream().map( dr->dr.getInitialState() ).collect( Collectors.toList());
    }

    public List<CaseComment> getRemovedComments() {
        return comments.getRemovedEntries();
    }

    public Collection<Attachment> getAddedAttachments() {
        return attachments.getAddedEntries();
    }

    public Collection<Attachment> getExistingAttachments() {
        attachments = synchronizeExists( attachments, Attachment::getId );
        return attachments.getSameEntries();
    }

    public Collection<Attachment> getRemovedAttachments() {
        return attachments.getRemovedEntries();
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Date getEventDate() {
        return new Date(getTimestamp());
    }

    public CaseObject getCaseObject() {
        return lastState != null ? lastState : initState;
    }

    public CaseObject getLastState() {
        return lastState;
    }

    public CaseObject getInitState() {
        return initState;
    }

    public CaseObjectMeta getCaseMeta() {
        return lastMetaState != null ? lastMetaState : initMetaState;
    }

    public CaseObjectMeta getLastCaseMeta() {
        return lastMetaState;
    }

    public CaseObjectMeta getInitCaseMeta() {
        return initMetaState;
    }

    public Person getInitiator() {
        return initiator;
    }

    public DiffCollectionResult<CaseLink> getMergeLinks() {
        mergeLinks = synchronizeExists( mergeLinks, CaseLink::getId );
        return mergeLinks;
    }

    public boolean isCoreModuleEvent () {
        return serviceModule == null || serviceModule == ServiceModule.GENERAL;
    }

    public boolean isAttachedCommentNotPrivate() {
        for (CaseComment addedComment : emptyIfNull( comments.getAddedEntries())) {
            if(addedComment.isPrivateComment()) return false;
        }
        return true;
    }

    public boolean isRemovedCommentNotPrivate() {
        for (CaseComment removedComment : emptyIfNull( comments.getRemovedEntries())) {
            if(removedComment.isPrivateComment()) return false;
        }
        return true;
    }

    public boolean isPublicCommentsChanged() {
        List<CaseComment> allEntries = new ArrayList<>();
        allEntries.addAll(emptyIfNull(comments.getAddedEntries()));
        allEntries.addAll(emptyIfNull(comments.getRemovedEntries()));
        allEntries.addAll(emptyIfNull(comments.getChangedEntries()).stream().map(DiffResult::getNewState).collect(Collectors.toList()));

        for (CaseComment comment : allEntries) {
            if (!comment.isPrivateComment()) {
                return true;
            }
        }

        return false;
    }


    public boolean isCaseObjectFilled() {
        return initState!=null;
    }

    public boolean isCaseMetaFilled() {
        return initMetaState != null;
    }

    public boolean isCaseCommentsFilled() {
        return existingComments !=null;
    }

    public void setLastCaseObject( CaseObject caseObject ) {
        lastState = caseObject;
    }

    public void setLastCaseMeta( CaseObjectMeta caseMeta ) {
        lastMetaState = caseMeta;
    }

    public void setExistingCaseComments( List<CaseComment> caseComments ) {
        this.existingComments = caseComments;
    }

    public void setExistingLinks( List<CaseLink> existingLinks ) {
        mergeLinks.putSameEntries( existingLinks );
    }

    public void setExistingAttachments( List<Attachment> existingAttachments ) {
        attachments.putSameEntries( existingAttachments );
    }

    public Long getCaseNumber() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return null;
        return caseObject.getCaseNumber();
    }

    public List<CaseComment> getAllComments() {
        Set<CaseComment> allComments = setOf( existingComments );
        if (comments.hasRemovedEntries()) {
            allComments.addAll(comments.getRemovedEntries());
        }

        if (comments.hasAdded()) {
            allComments.addAll( comments.getAddedEntries() );
        }

        return listOf( allComments ).stream()
                .sorted( Comparator.comparing( CaseComment::getId ) ).collect( Collectors.toList());
    }

    public Person getCreator() {
        return getCaseObject().getCreator();
    }

    public Person getManager() {
        return getCaseMeta().getManager();
    }

    public Long getCaseObjectId() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return caseObjectId;
        return caseObject.getId();
    }

    //Только добавленные и удаленные
    private <T> DiffCollectionResult<T> synchronizeDiffs( DiffCollectionResult<T> source, DiffCollectionResult<T> other, Function<T, Object> getId ) {
        if(!other.hasDifferences()) return source;
        synchronized (source){
            log.info( "synchronizeDiffs(): before +{} -{} {} ", toList( source.getAddedEntries(), getId ), toList( source.getRemovedEntries(), getId ), toList( source.getSameEntries(), getId ) );
            log.info( "synchronizeDiffs(): -> +{} -{} {}", toList( other.getAddedEntries(), getId ), toList( other.getRemovedEntries(), getId ) , toList( other.getSameEntries(), getId ) );

            source.putAddedEntries( other.getAddedEntries() );
            source.putRemovedEntries( other.getRemovedEntries() );

            Set<T> addedAttachments = setOf(source.getAddedEntries());
            Set<T> removedAttachments = setOf( source.getRemovedEntries() );

            Iterator<T> it = removedAttachments.iterator();
            while (it.hasNext()) {
                T removedAttachment = it.next();
                if (addedAttachments.contains( removedAttachment )) { //if you add and remove an attachment in a row
                    addedAttachments.remove( removedAttachment );
                    it.remove();
                    continue;
                }
            }

            log.info( "synchronizeDiffs(): after +{} -{} {} ", toList( source.getAddedEntries(), getId ), toList( source.getRemovedEntries(), getId ), toList( source.getSameEntries(), getId ) );

            DiffCollectionResult<T> result = new DiffCollectionResult<>();
            result.putAddedEntries( addedAttachments );
            result.putRemovedEntries( removedAttachments );
            result.putSameEntries( source.getSameEntries() );
            return result;
        }
    }

    // Убрать из существующих элементов добавленные и удаленные
    private <T> DiffCollectionResult<T> synchronizeExists( DiffCollectionResult<T> diff,Function<T, Object> getId) {
        log.info( "synchronizeExists(): before +{} -{} {} ", toList( diff.getAddedEntries(), getId ), toList( diff.getRemovedEntries(), getId ), toList( diff.getSameEntries(), getId ) );
        if (diff.hasSameEntries()){
            synchronized (diff) {
                diff.getSameEntries().removeAll( emptyIfNull( diff.getAddedEntries() ) );
                diff.getSameEntries().removeAll( emptyIfNull( diff.getRemovedEntries() ) );
            }
        }
        log.info( "synchronizeExists(): after +{} -{} {} ", toList( diff.getAddedEntries(), getId ), toList( diff.getRemovedEntries(), getId ), toList( diff.getSameEntries(), getId ) );
        return diff;
    }

    private static final Logger log = LoggerFactory.getLogger( AssembledCaseEvent.class );

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }
}
