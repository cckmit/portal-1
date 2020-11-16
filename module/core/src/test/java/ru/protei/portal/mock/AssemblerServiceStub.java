package ru.protei.portal.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.service.AssemblerService;

public class AssemblerServiceStub implements AssemblerService {

    @Override
    public void proceed( AssembledCaseEvent sourceEvent ) {
        log.info( "proceed(): Stub of service, event ignored. Event received {}", sourceEvent );
    }

    private static final Logger log = LoggerFactory.getLogger( AssemblerServiceStub.class );

}
