package ru.protei.portal.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.event.AssembledProjectEvent;
import ru.protei.portal.core.service.AssemblerProjectService;

public class AssemblerProjectServiceStub implements AssemblerProjectService {
    @Override
    public void proceed( AssembledProjectEvent sourceEvent ) {
        log.info( "proceed(): Stub of service, event ignored. Event received {}", sourceEvent );
    }

    private static final Logger log = LoggerFactory.getLogger( AssemblerProjectServiceStub.class );

}
