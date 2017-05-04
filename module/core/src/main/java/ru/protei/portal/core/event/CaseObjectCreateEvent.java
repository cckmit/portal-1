package ru.protei.portal.core.event;

import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.service.CaseService;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectCreateEvent extends CaseObjectEvent {

    public CaseObjectCreateEvent(CaseService source, CaseObject object) {
        super(source, object);
    }
}
