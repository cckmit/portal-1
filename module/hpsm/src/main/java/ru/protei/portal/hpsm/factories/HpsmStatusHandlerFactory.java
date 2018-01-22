package ru.protei.portal.hpsm.factories;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;

public interface HpsmStatusHandlerFactory {
    HpsmStatusHandler createHandler(CaseObject object, CaseComment comment);
}
