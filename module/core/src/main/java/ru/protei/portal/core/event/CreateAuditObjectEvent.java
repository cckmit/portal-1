package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.struct.AuditObject;

/**
 * Created by butusov on 07.08.17.
 */
public class CreateAuditObjectEvent extends ApplicationEvent {

    private AuditObject auditObject;

    public CreateAuditObjectEvent( Object source, AuditObject auditObject ) {
        super( source );
        this.auditObject = auditObject;
    }

    public AuditObject getAuditObject() {
        return auditObject;
    }
}
