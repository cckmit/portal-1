package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.service.AssemblerProjectService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.util.CrmConstants.Time.SEC;

public class EventProjectAssemblerServiceImpl implements EventProjectAssemblerService {
    @Override
    @EventListener
    public void onProjectCreateEvent(ProjectCreateEvent event) {
        publishCreateEvent(new AssembledProjectEvent(event));
    }

    @Override
    @EventListener
    public void onProjectSaveEvent(ProjectUpdateEvent event) {
        AssembledProjectEvent assembledProjectEvent = getAssembledProjectEvent(event);
        assembledProjectEvent.attachSaveEvent(event);
        publishAndClear(makeEventKey(event));
    }

    @Override
    @EventListener
    public void onProjectCommentEvent(ProjectCommentEvent event) {
        AssembledProjectEvent assembledProjectEvent = getAssembledProjectEvent(event);
        assembledProjectEvent.attachCommentEvent(event);
    }

    @Override
    @EventListener
    public void onProjectLinkEvent(ProjectLinkEvent event) {
        AssembledProjectEvent assembledProjectEvent = getAssembledProjectEvent(event);
        assembledProjectEvent.attachLinkEvent(event);
    }

    @Scheduled(fixedRate = 1 * SEC)
    public void checkEventsMap() {
        //Measured in ms
        Collection<Tuple<Long, Long>> eventKeys = assembledEventsMap.values().stream()
                .filter(this::isExpired)
                .map(event -> makeKey(event.getInitiatorId(), event.getProjectId()))
                .distinct()
                .collect(Collectors.toList());

        if (!eventKeys.isEmpty()) {
            log.debug("publish set of events, initiators : {}", eventKeys.size());
            eventKeys.forEach(this::publishAndClear);
        }
    }

    private void publishCreateEvent(AssembledProjectEvent event) {
        assemblerService.proceed(event);
        log.info("publishCreate event, projectId:{}", event.getProjectId());
    }

    private void publishAndClear(Tuple<Long, Long> key) {
        AssembledProjectEvent personsEvent = assembledEventsMap.remove(key);
        log.info("publishAndClear event, case:{}, person:{}", personsEvent.getProjectId(), personsEvent.getInitiatorId());
        assemblerService.proceed(personsEvent);
    }

    private AssembledProjectEvent getAssembledProjectEvent(AbstractProjectEvent event) {
        Tuple<Long, Long> key = makeEventKey(event);
        log.info( "getAssembledProjectEvent(): Key {} for {}",  key, event);
        return assembledEventsMap.computeIfAbsent(key, k -> new AssembledProjectEvent(event));//concurrency
    }

    private Tuple<Long, Long> makeEventKey(AbstractProjectEvent event) {
        return makeKey(event.getPersonId(), event.getProjectId());
    }

    private Tuple<Long, Long> makeKey(Long personId, Long caseId) {
        return new Tuple<>(personId, caseId);
    }

    private boolean isExpired(AssembledProjectEvent event) {
        long lastUpdated = event.getLastUpdated();
        long l = currentTimeMillis() - lastUpdated;
        long waitingPeriodMillis = config.data().eventAssemblyConfig().getWaitingPeriodMillis();
        return l  >= waitingPeriodMillis;
    }

    @Autowired
    private PortalConfig config;
    @Autowired
    AssemblerProjectService assemblerService;

    private final Map<Tuple<Long, Long>, AssembledProjectEvent> assembledEventsMap = new ConcurrentHashMap<>();
    private static Logger log = LoggerFactory.getLogger(EventProjectAssemblerService.class);
}
