package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

/**
 * Ссылки на обращения
 */
public class CaseLinksEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long caseObjectId;
    private Long personId;
    private ServiceModule serviceModule;
    private DiffCollectionResult<CaseLink> mergeLinks;

    public CaseLinksEvent( Object source, ServiceModule serviceModule, Long personId, Long caseObjectId,
                           DiffCollectionResult<CaseLink> mergeLinks
                            ) {
        super(source);
        this.serviceModule = serviceModule;
        this.personId = personId;
        this.caseObjectId = caseObjectId;
        this.mergeLinks = mergeLinks;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public Long getCaseObjectId(){
        return caseObjectId;
    }

    @Override
    public boolean isEagerEvent() {
        return false;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    public DiffCollectionResult<CaseLink> getMergeLinks() {
        return mergeLinks;
    }

    @Override
    public String toString() {
        return "CaseLinksEvent{" +
                "caseObjectId=" + caseObjectId +
                ", personId=" + personId +
                asString( mergeLinks ) +
                '}';
    }

    private String asString( DiffCollectionResult<CaseLink> mergeLinks ) {
        if(mergeLinks==null) return ", links=[no changes]";
        return  ", existLinks=" + toList(mergeLinks.getSameEntries(), CaseLink::getId ) +
                ", addedLinks=" + toList(mergeLinks.getAddedEntries(), CaseLink::getId ) +
                ", removedLinks=" + toList(mergeLinks.getRemovedEntries(), CaseLink::getId );
    }
}
