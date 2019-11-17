package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.AssemblerService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.util.CrmConstants.Time.SEC;

public class EventAssemblerServiceImpl implements EventAssemblerService {

    @Override
    @EventListener
    public void onCaseObjectEvent( CaseObjectEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseObjectEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(),  assembledPrevEvent, assembledPrevEvent.getInitiator() );
        assembledPrevEvent.attachCaseObjectEvent( event );
    }

    @Override
    @EventListener
    public void onCaseCommentEvent( CaseCommentEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseCommentEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(),  assembledPrevEvent, assembledPrevEvent.getInitiator() );
        assembledPrevEvent.attachCommentEvent( event );
    }

    @Override
    @EventListener
    public void onCaseAttachmentEvent( CaseAttachmentEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseAttachmentEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent, assembledPrevEvent.getInitiator() );
        assembledPrevEvent.attachAttachmentEvent( event );
    }

    @Override
    @EventListener
    public void onCaseLinkEvent( CaseLinksEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseLinkEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent, assembledPrevEvent.getInitiator() );
        assembledPrevEvent.attachLinkEvent( event );
    }

    @Override
    public AssembledCaseEvent getEvent(Person person, Long caseId) {
        Tuple<Person, Long> key = new Tuple<>(person, caseId);
        return assembledEventsMap.getOrDefault(key, null);
    }

    @Override
    public int getEventsCount() {
        return assembledEventsMap.size();
    }

    @Scheduled(fixedRate = 1 * SEC)
    public void checkEventsMap() {
        //Measured in ms
        Collection<Tuple<Person, Long>> events = assembledEventsMap.values().stream()
                .filter(x -> isExpired(x))
                .map(x -> new Tuple<>(x.getInitiator(), x.getCaseObjectId()))
                .distinct()
                .collect(Collectors.toList());

        if (!events.isEmpty()) {
            log.debug("publish set of events, initiators : {}", events.size());
            events.forEach(this::publishAndClear);
        }
    }

    private void publishAndClear(Tuple<Person, Long> key) {
        AssembledCaseEvent personsEvent = assembledEventsMap.remove(key);
        log.info("publishAndClear event, case:{}, person:{}", personsEvent.getCaseObjectId(),
                personsEvent.getInitiator().getDisplayName());
        assemblerService.proceed(personsEvent);
    }

    private AssembledCaseEvent getAssembledCaseEvent( AbstractCaseEvent event ) {
        Tuple<Person, Long> key = makeEventKey(event);
        log.info( "getAssembledCaseEvent(): Key {} for {}",  key, event);
        return assembledEventsMap.computeIfAbsent(key, k->new AssembledCaseEvent( event ));//concurrency
    }

    private Tuple<Person, Long> makeEventKey(AbstractCaseEvent event) {
        return new Tuple<>(event.getPerson(), event.getCaseObjectId());
    }

    public boolean isExpired(AssembledCaseEvent event) {
        if (event.isEagerEvent()) {
            return (currentTimeMillis() - event.getLastUpdated()) >= 2 * SEC;
        }
        long lastUpdated = event.getLastUpdated();
        long l = currentTimeMillis() - lastUpdated;
        long waitingPeriodMillis = config.data().eventAssemblyConfig().getWaitingPeriodMillis();
        return l  >= waitingPeriodMillis;
    }

    @Autowired
    private PortalConfig config;
    @Autowired
    AssemblerService assemblerService;

    private final Map<Tuple<Person, Long>, AssembledCaseEvent> assembledEventsMap = new ConcurrentHashMap<>();
    private static Logger log = LoggerFactory.getLogger(EventAssemblerServiceImpl.class);
}
