package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Сервис управления учетными записями
 */
public interface AccountService {
    CoreResponse< List< UserLogin > > accountList( AccountQuery query );
    CoreResponse< Long > count( AccountQuery query );
    CoreResponse< UserLogin > getAccount( long id );
    CoreResponse< UserLogin > saveAccount( UserLogin userLogin );
    CoreResponse< Boolean > checkUniqueLogin( String login, Long excludeId );
    CoreResponse< Boolean > removeAccount( UserLogin userLogin );

    CoreResponse< List< UserRole > > roleList();
}
