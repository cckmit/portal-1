package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;

/**
 * Created by butusov on 09.08.17.
 */
public class LongAuditableObject extends AuditableObject {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    @Override
    public String getAuditType() {
        return "LongAuditableObject";
    }
}
