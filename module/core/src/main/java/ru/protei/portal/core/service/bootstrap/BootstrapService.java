package ru.protei.portal.core.service.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dao.PlatformDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class BootstrapService {

    private static Logger log = LoggerFactory.getLogger( BootstrapService.class );

    private final static En_Privilege[] OBSOLETE_DB_PRIVILEGES = {
            En_Privilege.ISSUE_COMPANY_EDIT,
            En_Privilege.ISSUE_PRODUCT_EDIT,
            En_Privilege.ISSUE_MANAGER_EDIT,
            En_Privilege.ISSUE_PRIVACY_VIEW,
            En_Privilege.DASHBOARD_ALL_COMPANIES_VIEW,
            En_Privilege.DOCUMENT_TYPE_REMOVE
    };

    @PostConstruct
    public void init() {
        migrateUserRoleScopeToSingleValue();
        removeObsoletePrivileges();
//        autoPatchDefaultRoles();
        createSFPlatformCaseObjects();
    }

    private void autoPatchDefaultRoles () {
        userRoleDAO.getDefaultCustomerRoles();
        userRoleDAO.getDefaultEmployeeRoles();
    }

    private void migrateUserRoleScopeToSingleValue() {
        log.info( "Start migrate user role scope to single values" );
        userRoleDAO.trimScopeToSingleValue();
    }

    private void removeObsoletePrivileges() {
        List<En_Privilege> obsoletePrivileges = Arrays.asList(OBSOLETE_DB_PRIVILEGES);
        log.info( "Start remove obsolete privileges from user role = {}", obsoletePrivileges );
        List< UserRole > all = userRoleDAO.getAll();

        if ( all == null ) {
            log.info( "Not found roles. Aborting" );
            return;
        }

        List< UserRole > rolesHasObsoletePrivileges = all.stream()
                .filter( role -> role.getPrivileges() != null && !Collections.disjoint( role.getPrivileges(), obsoletePrivileges ) )
                .peek( role -> role.getPrivileges().removeAll( obsoletePrivileges ) )
                .collect( toList() );

        if ( rolesHasObsoletePrivileges.isEmpty() ) {
            log.info( "Not found roles with obsolete privileges" );
            return;
        }

        userRoleDAO.mergeBatch( rolesHasObsoletePrivileges );
        log.info( "Correction roles with obsolete privileges success" );
    }

    private void createSFPlatformCaseObjects() {

        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.SF_PLATFORM);
        Long count = caseObjectDAO.count(query);
        if (count == null || count > 0) {
            // guard for more than one execution
            return;
        }

        log.info("Site folder platform database migration has started");

        final int limit = 50;
        int offset = 0;
        while (true) {
            SearchResult<Platform> result = platformDAO.getAll(offset, limit);
            List<CaseObject> caseObjectListToPersist = new ArrayList<>();
            for (Platform platform : result.getResults()) {
                CaseObject caseObject = new CaseObject();
                caseObject.setCaseType(En_CaseType.SF_PLATFORM);
                caseObject.setCaseNumber(platform.getId());
                caseObject.setCreated(new Date());
                caseObjectListToPersist.add(caseObject);
            }
            caseObjectDAO.persistBatch(caseObjectListToPersist);
            if (result.getTotalCount() < limit) {
                break;
            } else {
                offset += limit;
            }
        }

        log.info("Site folder platform database migration has ended");
    }

    @Inject
    UserRoleDAO userRoleDAO;
    @Inject
    DecimalNumberDAO decimalNumberDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    PlatformDAO platformDAO;
}
