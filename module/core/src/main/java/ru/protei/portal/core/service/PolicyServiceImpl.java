package ru.protei.portal.core.service;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.UserRole;

import java.util.Set;

/**
 * Сервис для работы с привилегиями
 */
public class PolicyServiceImpl implements PolicyService {

    @Override
    public boolean hasPrivilegeFor( En_Privilege privilege, Set< UserRole > roles ) {
        if ( roles == null ) {
            return false;
        }
        //TODO напрягает каждый запрос на ядро итерироваться в поиске привилегии
        //TODO тут выход только расширить/пересмотреть UserSessionDescriptor и писать туда одну коллекцию из всех привилегий как в AuthServiceImpl.makeProfileByDescriptor()
        for ( UserRole role : roles ) {
            if ( role.getPrivileges().contains( privilege ) ) {
                return true;
            }
        }
        return false;
    }
}
