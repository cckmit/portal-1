package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

public interface PersonSubscriptionService {

    @Privileged({ En_Privilege.COMMON_PROFILE_EDIT })
    Result<Set<PersonShortView>> getPersonSubscriptions(AuthToken token);

    @Privileged({ En_Privilege.COMMON_PROFILE_EDIT })
    @Auditable( En_AuditType.SUBSCRIPTION_MODIFY )
    Result<Set<PersonShortView>> updatePersonSubscriptions(AuthToken token, Set<PersonShortView> persons);
}
