package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;

import java.util.List;

/**
 * Сервис управления учетными записями
 */
public interface AccountService {
    @Privileged({ En_Privilege.ACCOUNT_VIEW })
    CoreResponse< List< UserLogin > > accountList( AuthToken authToken, AccountQuery query );

    @Privileged({ En_Privilege.ACCOUNT_VIEW })
    CoreResponse< Long > count( AuthToken authToken, AccountQuery query );

    @Privileged({ En_Privilege.ACCOUNT_VIEW })
    CoreResponse< UserLogin > getAccount( AuthToken authToken, long id );

    @Privileged({ En_Privilege.ACCOUNT_VIEW })
    CoreResponse< UserLogin > getAccountByPersonId( AuthToken authToken, long personId );

    @Privileged( requireAny = { En_Privilege.ACCOUNT_EDIT, En_Privilege.ACCOUNT_CREATE })
    @Auditable( En_AuditType.ACCOUNT_MODIFY )
    CoreResponse< UserLogin > saveAccount( AuthToken token, UserLogin userLogin );

    CoreResponse< Boolean > checkUniqueLogin( String login, Long excludeId );

    @Privileged({ En_Privilege.ACCOUNT_REMOVE })
    @Auditable( En_AuditType.ACCOUNT_MODIFY )
    CoreResponse< Boolean > removeAccount( AuthToken authToken, Long accountId );
}
