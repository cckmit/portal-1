package ru.protei.portal.hpsm.annotations;

import ru.protei.portal.hpsm.api.HpsmStatus;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Handler.class)
public @interface CaseHandler {
    HpsmStatus hpsmStatus();
}
