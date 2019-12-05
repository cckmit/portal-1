package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
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
    public void onCaseObjectEvent(CaseObjectCreateEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseObjectEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent.getInitiatorId(), assembledPrevEvent );
        assembledPrevEvent.attachCaseObjectCreateEvent( event );
    }

    @Override
    @EventListener
    public void onCaseNameAndDescriptionEvent(CaseNameAndDescriptionEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent(event);
        log.info( "onCaseNameAndDescriptionEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent.getInitiatorId(), assembledPrevEvent );
        assembledPrevEvent.attachCaseNameAndDescriptionEvent(event);
    }

    @Override
    @EventListener
    public void onCaseObjectMetaEvent(CaseObjectMetaEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent(event);
        log.info("onCaseObjectMetaEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent.getInitiatorId(), assembledPrevEvent);
        assembledPrevEvent.attachCaseObjectMetaEvent(event);
    }

    @Override
    @EventListener
    public void onCaseCommentEvent(CaseCommentEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseCommentEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent.getInitiatorId(), assembledPrevEvent );
        assembledPrevEvent.attachCommentEvent( event );
    }

    @Override
    @EventListener
    public void onCaseAttachmentEvent(CaseAttachmentEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseAttachmentEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent.getInitiatorId(), assembledPrevEvent );
        assembledPrevEvent.attachAttachmentEvent( event );
    }

    @Override
    @EventListener
    public void onCaseLinkEvent(CaseLinksEvent event) {
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "onCaseLinkEvent(): CaseObjectId={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent.getInitiatorId(), assembledPrevEvent );
        assembledPrevEvent.attachLinkEvent( event );
    }

    @Override
    public AssembledCaseEvent getEvent(Long personId, Long caseId) {
        Tuple<Long, Long> key = makeKey(personId, caseId);
        return assembledEventsMap.getOrDefault(key, null);
    }

    @Override
    public int getEventsCount() {
        return assembledEventsMap.size();
    }

    @Scheduled(fixedRate = 1 * SEC)
    public void checkEventsMap() {
        //Measured in ms
        Collection<Tuple<Long, Long>> events = assembledEventsMap.values().stream()
                .filter(x -> isExpired(x))
                .map(x -> makeKey(x.getInitiatorId(), x.getCaseObjectId()))
                .distinct()
                .collect(Collectors.toList());

        if (!events.isEmpty()) {
            log.debug("publish set of events, initiators : {}", events.size());
            events.forEach(this::publishAndClear);
        }
    }

    private void publishAndClear(Tuple<Long, Long> key) {
        AssembledCaseEvent personsEvent = assembledEventsMap.remove(key);
        log.info("publishAndClear event, case:{}, person:{}", personsEvent.getCaseObjectId(), personsEvent.getInitiatorId());
        assemblerService.proceed(personsEvent);
    }

    private AssembledCaseEvent getAssembledCaseEvent( AbstractCaseEvent event ) {
        Tuple<Long, Long> key = makeEventKey(event);
        log.info( "getAssembledCaseEvent(): Key {} for {}",  key, event);
        return assembledEventsMap.computeIfAbsent(key, k->new AssembledCaseEvent( event ));//concurrency
    }

    private Tuple<Long, Long> makeEventKey(AbstractCaseEvent event) {
        return makeKey(event.getPersonId(), event.getCaseObjectId());
    }

    private Tuple<Long, Long> makeKey(Long personId, Long caseId) {
        return new Tuple<>(personId, caseId);
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

    private final Map<Tuple<Long, Long>, AssembledCaseEvent> assembledEventsMap = new ConcurrentHashMap<>();
    private static Logger log = LoggerFactory.getLogger(EventAssemblerServiceImpl.class);
}
