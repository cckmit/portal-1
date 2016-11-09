package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ContactQuery;

import java.util.List;

/**
 * Сервис управления обращениями
 */
public interface CaseService {

    CoreResponse<List<CaseObject>> caseObjectList( CaseQuery query );
    CoreResponse<CaseObject> getCaseObject( long id );
    CoreResponse<CaseObject> saveCaseObject( CaseObject p );
}
