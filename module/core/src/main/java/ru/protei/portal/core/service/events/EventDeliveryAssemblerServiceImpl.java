package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.service.AssemblerDeliveryService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.util.CrmConstants.Time.SEC;

public class EventDeliveryAssemblerServiceImpl implements EventDeliveryAssemblerService {
    @Override
    @EventListener
    public void onDeliveryCreateEvent(DeliveryCreateEvent event) {
        log.info("onDeliveryCreateEvent(): deliveryId:{}", event.getDeliveryId());
        assemblerService.proceed(new AssembledDeliveryEvent(event));
    }

    @Override
    @EventListener
    public void onDeliveryUpdateEvent(DeliveryUpdateEvent event) {
        log.info("onDeliveryUpdateEvent(): deliveryId:{}", event.getDeliveryId());
        AssembledDeliveryEvent assembledDeliveryEvent = getAssembledDeliveryEvent(event);
        assembledDeliveryEvent.attachUpdateEvent(event);
    }

    @Override
    @EventListener
    public void onDeliveryNameAndDescriptionEvent(DeliveryNameAndDescriptionEvent event) {
        AssembledDeliveryEvent assembledDeliveryEvent = getAssembledDeliveryEvent(event);
        log.info( "onDeliveryNameAndDescriptionEvent(): DeliveryId={} {} {}", assembledDeliveryEvent.getDeliveryId(), assembledDeliveryEvent.getInitiatorId(), assembledDeliveryEvent );
        assembledDeliveryEvent.attachCaseNameAndDescriptionEvent(event);
    }

    @Override
    @EventListener
    public void onDeliveryCommentEvent(DeliveryCommentEvent event) {
        log.info("onDeliveryUpdateEvent(): deliveryId:{}", event.getDeliveryId());
        AssembledDeliveryEvent assembledDeliveryEvent = getAssembledDeliveryEvent(event);
        assembledDeliveryEvent.attachCommentEvent(event);
    }

    @Override
    @EventListener
    public void onDeliveryAttachmentEvent(DeliveryAttachmentEvent event) {
        log.info("onDeliveryUpdateEvent(): deliveryId:{}", event.getDeliveryId());
        AssembledDeliveryEvent assembledDeliveryEvent = getAssembledDeliveryEvent(event);
        assembledDeliveryEvent.attachAttachmentEvent(event);
    }

    @Scheduled(fixedRate = 1 * SEC)
    public void checkEventsMap() {
        Collection<Tuple<Long, Long>> eventKeys = assembledEventsMap.values().stream()
                .filter(this::isExpired)
                .map(event -> makeKey(event.getInitiatorId(), event.getDeliveryId()))
                .distinct()
                .collect(Collectors.toList());

        if (!eventKeys.isEmpty()) {
            log.debug("publish set of events, initiators : {}", eventKeys.size());
            eventKeys.forEach(this::publishAndClear);
        }
    }

    private void publishAndClear(Tuple<Long, Long> key) {
        AssembledDeliveryEvent deliveryEvent = assembledEventsMap.remove(key);
        log.info("publishAndClear event, delivery:{}, person:{}", deliveryEvent.getDeliveryId(), deliveryEvent.getInitiatorId());
        assemblerService.proceed(deliveryEvent);
    }

    private AssembledDeliveryEvent getAssembledDeliveryEvent(AbstractDeliveryEvent event) {
        Tuple<Long, Long> key = makeEventKey(event);
        log.info( "getAssembledDeliveryEvent(): Key {} for {}",  key, event);
        return assembledEventsMap.computeIfAbsent(key, k -> new AssembledDeliveryEvent(event));
    }

    private Tuple<Long, Long> makeEventKey(AbstractDeliveryEvent event) {
        return makeKey(event.getPersonId(), event.getDeliveryId());
    }

    private Tuple<Long, Long> makeKey(Long personId, Long caseId) {
        return new Tuple<>(personId, caseId);
    }

    private boolean isExpired(AssembledDeliveryEvent event) {
        long lastUpdated = event.getLastUpdated();
        long l = currentTimeMillis() - lastUpdated;
        long waitingPeriodMillis = config.data().eventAssemblyConfig().getWaitingPeriodMillis();
        return l  >= waitingPeriodMillis;
    }

    @Autowired
    private PortalConfig config;
    @Autowired
    AssemblerDeliveryService assemblerService;

    private final Map<Tuple<Long, Long>, AssembledDeliveryEvent> assembledEventsMap = new ConcurrentHashMap<>();
    private static Logger log = LoggerFactory.getLogger(EventDeliveryAssemblerService.class);
}
