package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления учетными записями
 */
public interface AccountService {

    @Privileged({ En_Privilege.ACCOUNT_VIEW })
    Result<SearchResult<UserLogin>> getAccounts( AuthToken token, AccountQuery query);

    @Privileged({ En_Privilege.ACCOUNT_VIEW })
    Result< UserLogin > getAccount( AuthToken authToken, long id );

    @Privileged({ En_Privilege.CONTACT_VIEW })
    Result<List<UserLogin>> getContactAccount(AuthToken authToken, long personId );

    @Privileged( requireAny = { En_Privilege.ACCOUNT_EDIT, En_Privilege.ACCOUNT_CREATE })
    @Auditable( En_AuditType.ACCOUNT_MODIFY )
    Result< UserLogin > saveAccount( AuthToken token, UserLogin userLogin, Boolean sendWelcomeEmail );

    @Privileged( requireAny = { En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE })
    @Auditable( En_AuditType.ACCOUNT_MODIFY )
    Result< UserLogin > saveContactAccount( AuthToken token, UserLogin userLogin, Boolean sendWelcomeEmail );

    Result< Boolean > checkUniqueLogin( String login, Long excludeId );

    @Privileged({ En_Privilege.ACCOUNT_REMOVE })
    @Auditable( En_AuditType.ACCOUNT_MODIFY )
    Result< Boolean > removeAccount( AuthToken authToken, Long accountId );

    Result<?> updateAccountPassword( AuthToken token, Long loginId, String currentPassword, String newPassword );
}
