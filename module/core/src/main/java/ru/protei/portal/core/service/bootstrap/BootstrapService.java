package ru.protei.portal.core.service.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.ent.UserRole;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class BootstrapService {

    private static Logger log = LoggerFactory.getLogger( BootstrapService.class );

    private enum ObsoletePrivilege {
        ISSUE_COMPANY_EDIT, ISSUE_PRODUCT_EDIT, ISSUE_MANAGER_EDIT, ISSUE_PRIVACY_VIEW, DASHBOARD_ALL_COMPANIES_VIEW
    }

    @Inject
    private UserRoleDAO userRoleDAO;

    @PostConstruct
    public void init() {
        removeObsoletePrivileges();
    }

    private void removeObsoletePrivileges() {
        List< ObsoletePrivilege > obsoletePrivileges = Arrays.asList( ObsoletePrivilege.values() );

        log.info( "Start remove obsolete privileges = {}", obsoletePrivileges );
        List< UserRole > all = userRoleDAO.getAll();

        if ( all == null ) {
            log.info( "Not found roles. Aborting" );
            return;
        }

        List< UserRole > rolesHasObsoletePrivileges = all.stream()
                .filter( role -> !Collections.disjoint( role.getPrivileges(), obsoletePrivileges ) )
                .peek( role -> role.getPrivileges().retainAll( obsoletePrivileges ) )
                .collect( toList() );

        if ( rolesHasObsoletePrivileges.isEmpty() ) {
            log.info( "Not found roles with obsolete privileges" );
            return;
        }

        userRoleDAO.mergeBatch( rolesHasObsoletePrivileges );
        log.info( "Correction roles with obsolete privileges success" );
    }
}
