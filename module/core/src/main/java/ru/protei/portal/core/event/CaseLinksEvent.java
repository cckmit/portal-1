package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

/**
 * Ссылки на обращения
 */
public class CaseLinksEvent extends ApplicationEvent implements AbstractCaseEvent {

    private Long caseObjectId;
    private Person person;
    private ServiceModule serviceModule;
    private DiffCollectionResult<CaseLink> mergeLinks;

    public CaseLinksEvent( Object source, ServiceModule serviceModule, Person person, Long caseObjectId,
                           DiffCollectionResult<CaseLink> mergeLinks
                            ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
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

    public Person getPerson() {
        return person;
    }

    public DiffCollectionResult<CaseLink> getMergeLinks() {
        return mergeLinks;
    }

    @Override
    public String toString() {
        return "CaseLinksEvent{" +
                "caseObjectId=" + caseObjectId +
                ", person=" + (person==null?null:person.getId()) +
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
