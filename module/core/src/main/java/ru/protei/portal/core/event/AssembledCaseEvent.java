package ru.protei.portal.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.*;
import java.util.function.Function;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AssembledCaseEvent extends ApplicationEvent {

    private Long caseObjectId;
    private CaseObject lastState;
    private CaseObject initState;
    protected CaseComment newComment;
    private CaseComment oldComment;
    private CaseComment removedComment;
    private DiffCollectionResult <CaseLink> mergeLinks = new DiffCollectionResult<>();
    private DiffCollectionResult <Attachment> attachments = new DiffCollectionResult<>();

    private Person initiator;
    private ServiceModule serviceModule;
    private List<CaseComment> caseComments;
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
    }

    public void attachCaseObjectEvent( CaseObjectEvent objectEvent ) {
        this.lastUpdated = currentTimeMillis();
        this.initState = objectEvent.getOldState();
        this.lastState = objectEvent.getNewState();
        this.initiator = objectEvent.getPerson();
        this.serviceModule = objectEvent.getServiceModule();

        isEagerEvent = isEagerEvent||objectEvent.isEagerEvent();
    }

    public void attachLinkEvent( CaseLinksEvent event ) {
        this.lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent||event.isEagerEvent();
        mergeLinks = synchronizeDiffs(mergeLinks, event.getMergeLinks(), CaseLink::getId );
    }

    public void attachCommentEvent( CaseCommentEvent commentEvent ) {
        this.lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent||commentEvent.isEagerEvent();
        oldComment = commentEvent.getOldCaseComment();//TODO
        newComment = commentEvent.getNewCaseComment();
        removedComment = commentEvent.getRemovedCaseComment();
    }

    public void attachAttachmentEvent( CaseAttachmentEvent event ) {
        this.lastUpdated = currentTimeMillis();
        isEagerEvent = isEagerEvent||event.isEagerEvent();
        attachments = synchronizeDiffs( attachments, event.getAttachments(), Attachment::getId );
    }

    public CaseComment getCaseComment() {
        return newComment;
    }

    public boolean isCreateEvent() {
        return this.initState == null;
    }

    private boolean isUpdateEvent() {
        return this.initState != null ;//&& lastState!=null;
    }

    public boolean isCommentAttached() {
        return this.newComment != null;
    }

    public boolean isCommentRemoved() {
        return removedComment != null;
    }

    public boolean isCaseStateChanged() {
        return isUpdateEvent() && lastState.getState() != initState.getState();
    }

    public boolean isTimeElapsedChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getTimeElapsed(), initState.getTimeElapsed());
    }

    public boolean isCaseImportanceChanged() {
        return isUpdateEvent() && !lastState.getImpLevel().equals(initState.getImpLevel());
    }

    public boolean isManagerChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getManagerId(), initState.getManagerId());
    }

    public boolean isProductChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getProductId(), initState.getProductId());
    }

    public boolean isInitiatorChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInitiatorId(), initState.getInitiatorId());
    }

    public boolean isInitiatorCompanyChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInitiatorCompanyId(), initState.getInitiatorCompanyId());
    }

    public boolean isInfoChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInfo(), initState.getInfo());
    }

    public boolean isNameChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getName(), initState.getName());
    }

    public boolean isPrivacyChanged() {
        return isUpdateEvent() && lastState.isPrivateCase() != initState.isPrivateCase();
    }

    public boolean isPlatformChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getPlatformId(), initState.getPlatformId());
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
        newComment = CollectionUtils.lastOrDefault(commentList, newComment );
        lastUpdated = currentTimeMillis();
    }

    @Deprecated
    public void includeCaseAttachments (List<Attachment> attachments1) {
        this.lastUpdated = currentTimeMillis();
        DiffCollectionResult<Attachment> diff = new DiffCollectionResult<>();
        diff.putAddedEntries( attachments1 );
        attachments = synchronizeDiffs( attachments, diff, Attachment::getId );
    }

    public CaseComment getOldComment() {
        return oldComment;
    }

    public CaseComment getRemovedComment() {
        return removedComment;
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

    public boolean isSendToCustomers() {
        return isCreateEvent()
                || (
                        (!isCommentAttached() || isAttachedCommentNotPrivate())
                                && (!isCommentRemoved() || isRemovedCommentNotPrivate()))
                || isPublicChangedWithOutComments();
    }

    private boolean isAttachedCommentNotPrivate() {
        return newComment != null && !newComment.isPrivateComment();
    }

    private boolean isRemovedCommentNotPrivate() {
        return removedComment != null && !removedComment.isPrivateComment();
    }

    private boolean isPublicChangedWithOutComments() {
        return  isCaseImportanceChanged()
                || isCaseStateChanged()
                || isInfoChanged()
                || isInitiatorChanged()
                || isInitiatorCompanyChanged()
                || isManagerChanged()
                || isNameChanged()
                || isPrivacyChanged()
                || isProductChanged();
    }


    public boolean isCaseObjectFilled() {
        return initState!=null;
    }

    public boolean isCaseCommentsFilled() {
        return caseComments!=null;
    }

    public void setLastCaseObject( CaseObject caseObject ) {
        lastState = caseObject;
    }

    public void setInitialCaseComments( List<CaseComment> caseComments ) {
        this.caseComments = caseComments;
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
        List<CaseComment> comments = new ArrayList<>(  caseComments );
        if (getRemovedComment() != null) {
            comments.add(getRemovedComment());
        }

        if (getCaseComment() != null) {
            boolean isNewCommentPresents = comments.stream()
                    .anyMatch(comment ->
                            Objects.equals(comment.getId(), getCaseComment().getId())
                    );

            if (!isNewCommentPresents) {
                comments.add(getCaseComment());
            }
        }
        return comments;
    }

    public Person getCreator() {
        return getCaseObject().getCreator();
    }

    public Person getManager() {
        return getCaseObject().getManager();
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
