package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.service.AssemblerEmployeeRegistrationService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.util.CrmConstants.Time.SEC;

public class EventEmployeeRegistrationAssemblerServiceImpl implements EventEmployeeRegistrationAssemblerService {

    @Override
    @EventListener
    public void onEmployeeRegistrationCommentEvent(EmployeeRegistrationCommentEvent event) {
        log.info("onEmployeeRegistrationCommentEvent(): employeeRegistrationId:{}", event.getEmployeeRegistrationId());
        AssembledEmployeeRegistrationEvent assembledEmployeeRegistrationEvent = getAssembledEmployeeRegistrationEvent(event);
        assembledEmployeeRegistrationEvent.attachCommentEvent(event);
    }

    @Override
    @EventListener
    public void onEmployeeRegistrationAttachmentEvent(EmployeeRegistrationAttachmentEvent event) {
        AssembledEmployeeRegistrationEvent assembledEmployeeRegistrationEvent = getAssembledEmployeeRegistrationEvent(event);
        assembledEmployeeRegistrationEvent.attachAttachmentEvent(event);
    }

    @Scheduled(fixedRate = 1 * SEC)
    public void checkEventsMap() {
        Collection<Tuple<Long, Long>> eventKeys = assembledEventsMap.values().stream()
                .filter(this::isExpired)
                .map(event -> makeKey(event.getInitiatorId(), event.getEmployeeRegistrationId()))
                .distinct()
                .collect(Collectors.toList());

        if (!eventKeys.isEmpty()) {
            log.debug("publish set of events, initiators : {}", eventKeys.size());
            eventKeys.forEach(this::publishAndClear);
        }
    }

    private void publishAndClear(Tuple<Long, Long> key) {
        AssembledEmployeeRegistrationEvent employeeRegistrationEvent = assembledEventsMap.remove(key);
        log.info("publishAndClear event, employeeRegistration:{}, person:{}",
                employeeRegistrationEvent.getEmployeeRegistrationId(),
                employeeRegistrationEvent.getInitiatorId());
        assemblerService.proceed(employeeRegistrationEvent);
    }

    private AssembledEmployeeRegistrationEvent getAssembledEmployeeRegistrationEvent(AbstractEmployeeRegistrationEvent event) {
        Tuple<Long, Long> key = makeEventKey(event);
        log.info( "getAssembledEmployeeRegistrationEvent(): Key {} for {}",  key, event);
        return assembledEventsMap.computeIfAbsent(key, k -> new AssembledEmployeeRegistrationEvent(event));
    }

    private Tuple<Long, Long> makeEventKey(AbstractEmployeeRegistrationEvent event) {
        return makeKey(event.getPersonId(), event.getEmployeeRegistrationId());
    }

    private Tuple<Long, Long> makeKey(Long personId, Long caseId) {
        return new Tuple<>(personId, caseId);
    }

    private boolean isExpired(AssembledEmployeeRegistrationEvent event) {
        long lastUpdated = event.getLastUpdated();
        long l = currentTimeMillis() - lastUpdated;
        long waitingPeriodMillis = config.data().eventAssemblyConfig().getWaitingPeriodMillis();
        return l  >= waitingPeriodMillis;
    }

    @Autowired
    private PortalConfig config;
    @Autowired
    AssemblerEmployeeRegistrationService assemblerService;

    private final Map<Tuple<Long, Long>, AssembledEmployeeRegistrationEvent> assembledEventsMap = new ConcurrentHashMap<>();
    private static Logger log = LoggerFactory.getLogger(EventDeliveryAssemblerService.class);
}
