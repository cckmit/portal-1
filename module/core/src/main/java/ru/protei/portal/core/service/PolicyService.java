package ru.protei.portal.core.service;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.UserRole;

import java.util.Set;

/**
 * Абстракция сервиса для работы с привилегиями
 */
public interface PolicyService {

    boolean hasPrivilegeFor( En_Privilege privilege, Set< UserRole > roles );
}
