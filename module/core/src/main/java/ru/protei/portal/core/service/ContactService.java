package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления контактами
 */
public interface ContactService {

    @Privileged({ En_Privilege.CONTACT_VIEW })
    Result<SearchResult<Person>> getContactsSearchResult( AuthToken token, ContactQuery query);

    Result<List<PersonShortView>> shortViewList( AuthToken token, ContactQuery query);

    @Privileged( En_Privilege.CONTACT_VIEW )
    Result<Person> getContact( AuthToken token, long id );

    @Privileged( requireAny = { En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE })
    @Auditable( En_AuditType.CONTACT_MODIFY )
    Result<Person> saveContact( AuthToken token, Person p );

    @Privileged( En_Privilege.CONTACT_EDIT )
    @Auditable(En_AuditType.CONTACT_FIRE)
    Result<Boolean> fireContact( AuthToken token, long id );

    @Privileged( En_Privilege.CONTACT_REMOVE )
    @Auditable(En_AuditType.CONTACT_DELETE)
    Result<Long> removeContact( AuthToken token, long id );
}
