package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;
import java.util.Map;

public interface ModuleService {

    @Privileged({ En_Privilege.DELIVERY_VIEW })
    Result<Module> getModule(AuthToken token, Long id);

    @Privileged({ En_Privilege.DELIVERY_VIEW })
    Result<Map<Module, List<Module>>> getModulesByKitId(AuthToken token, Long kitId);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.MODULE_MODIFY )
    Result<Module> updateMeta(AuthToken token, Module meta);

    Result<String> generateSerialNumber(AuthToken token, Long kitId);
}
