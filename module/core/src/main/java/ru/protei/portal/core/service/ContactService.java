package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Сервис управления контактами
 */
public interface ContactService {

    CoreResponse<List<PersonShortView>> shortViewList(AuthToken token, ContactQuery query);

    @Privileged( En_Privilege.CONTACT_VIEW )
    CoreResponse<Long> count( AuthToken token, ContactQuery query );

    @Privileged( En_Privilege.CONTACT_VIEW )
    CoreResponse<List<Person>> contactList(AuthToken token, ContactQuery query);

    @Privileged( En_Privilege.CONTACT_VIEW )
    CoreResponse<Person> getContact( AuthToken token, long id );

    @Privileged( requireAny = { En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE })
    @Auditable( En_AuditType.CONTACT_MODIFY )
    CoreResponse<Person> saveContact( AuthToken token, Person p );

    @Privileged( En_Privilege.CONTACT_EDIT )
    CoreResponse fireContact( AuthToken token, long id );

    @Privileged( En_Privilege.CONTACT_REMOVE )
    CoreResponse removeContact( AuthToken token, long id );
}
