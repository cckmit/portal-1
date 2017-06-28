package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.AccountQuery;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления учетными записями
 */
public interface AccountService {
    CoreResponse< List< UserLogin > > accountList( AccountQuery query, Set< UserRole > roles );
    CoreResponse< Long > count( AccountQuery query, Set< UserRole > roles );
    CoreResponse< UserLogin > getAccount( long id, Set< UserRole > roles );
    CoreResponse< UserLogin > saveAccount( UserLogin userLogin, Set< UserRole > roles );
    CoreResponse< Boolean > checkUniqueLogin( String login, Long excludeId );
    CoreResponse< Boolean > removeAccount( Long accountId, Set< UserRole > roles );

    CoreResponse< List< UserRole > > roleList();
}
