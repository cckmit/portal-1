package ru.protei.portal.hpsm.handlers;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;

public interface HpsmStatusHandler {
    void handle(CaseObject object, CaseComment comment);
}
