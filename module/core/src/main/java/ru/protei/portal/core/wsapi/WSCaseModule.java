package ru.protei.portal.core.wsapi;

import ru.protei.portal.core.model.ent.CaseObject;

import javax.jws.WebService;

/**
 * Created by Mike on 01.05.2017.
 */
@WebService
public interface WSCaseModule {

    CaseObject getCaseObject (long id);

}
