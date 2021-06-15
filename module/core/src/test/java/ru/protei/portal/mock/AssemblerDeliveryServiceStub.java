package ru.protei.portal.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.event.AssembledDeliveryEvent;
import ru.protei.portal.core.service.AssemblerDeliveryService;

public class AssemblerDeliveryServiceStub implements AssemblerDeliveryService {
    @Override
    public void proceed( AssembledDeliveryEvent sourceEvent ) {
        log.info( "proceed(): Stub of service, event ignored. Event received {}", sourceEvent );
    }

    private static final Logger log = LoggerFactory.getLogger( AssemblerDeliveryServiceStub.class );

}
