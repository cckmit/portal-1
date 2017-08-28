package ru.protei.portal.tools.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.CreateAuditObjectEvent;
import ru.protei.portal.core.service.AuditService;

/**
 * Вспомогательный класс для создания записей аудита
 */
public class AuditWriterProcessor {

    @Autowired
    AuditService auditService;

    private final static Logger log = LoggerFactory.getLogger( AuditWriterProcessor.class );

    @EventListener
    public void onCreateAuditObject( CreateAuditObjectEvent event ) {

        if (event.getAuditObject() == null){
            return;
        }

        auditService.saveAuditObject( event.getAuditObject() );
    }
}
