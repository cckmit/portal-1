package ru.protei.portal.core.event;

import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.service.CaseService;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectUpdateEvent extends CaseObjectEvent {

    private CaseObject oldState;

    public CaseObjectUpdateEvent(CaseService source, CaseObject object, CaseObject oldState) {
        super(source, object);
        this.oldState = oldState;
    }

    public CaseObject getOldState() {
        return oldState;
    }
}
