package ru.protei.portal.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.event.AssembledEmployeeRegistrationEvent;
import ru.protei.portal.core.service.AssemblerEmployeeRegistrationService;

public class AssemblerEmployeeRegistrationServiceStub implements AssemblerEmployeeRegistrationService {
    @Override
    public void proceed( AssembledEmployeeRegistrationEvent sourceEvent ) {
        log.info( "proceed(): Stub of service, event ignored. Event received {}", sourceEvent );
    }

    private static final Logger log = LoggerFactory.getLogger( AssemblerEmployeeRegistrationServiceStub.class );

}
