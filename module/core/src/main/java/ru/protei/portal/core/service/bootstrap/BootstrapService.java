package ru.protei.portal.core.service.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.PhoneUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.ProductServiceImpl;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

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
        updateCompanyCaseTags();
        patchNormalizeWorkersPhoneNumbers(); // remove once executed
        uniteSeveralProductsInProjectToComplex();
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
            for (Platform platform : result.getResults()) {
                CaseObject caseObject = new CaseObject();
                caseObject.setCaseType(En_CaseType.SF_PLATFORM);
                caseObject.setCaseNumber(platform.getId());
                caseObject.setCreated(new Date());
                caseObject.setName(platform.getName());
                caseObject.setState(En_CaseState.CREATED);
                Long caseId = caseObjectDAO.persist(caseObject);
                platform.setCaseId(caseId);
                platformDAO.partialMerge(platform, "case_id");
            }
            if (result.getResults().size() < limit) {
                break;
            } else {
                offset += limit;
            }
        }

        log.info("Site folder platform database migration has ended");
    }

    private void updateCompanyCaseTags() {
        Long companyId = companyGroupHomeDAO.mainCompanyId();
        if (companyId == null) {
            log.info( "Main company id not found. Aborting" );
            return;
        }

        log.info("Start update tags where company id is null, set company id {} ", companyId);

        List<CaseTag> result = caseTagDAO.getListByCondition("case_tag.company_id is null");
        if (CollectionUtils.isEmpty(result)) {
            log.info( "Not found tags. Aborting" );
            return;
        }
        result.forEach(caseTag -> {
            caseTag.setCompanyId(companyId);
            caseTagDAO.merge(caseTag);
        });
        log.info("Correction company id in tags completed successfully");
    }

    private void patchNormalizeWorkersPhoneNumbers() {

        final String sqlCondition = "sex <> ? AND company_id IN (SELECT id FROM company WHERE category_id = ?)";
        final List<Object> params = new ArrayList<>();
        params.add(En_Gender.UNDEFINED.getCode());
        params.add(5);

        log.info("Patch for workers phone number normalization has started");

        final int limit = 50;
        int offset = 0;
        for (;;) {
            SearchResult<Person> result = personDAO.partialGetListByCondition(sqlCondition, params, offset, limit, "id", "contactInfo");
            for (Person person : result.getResults()) {
                ContactInfo ci = person.getContactInfo();
                PlainContactInfoFacade facade = new PlainContactInfoFacade(ci);
                facade.allPhonesStream().forEach(cci -> {
                    String normalized = PhoneUtils.normalizePhoneNumber(cci.value());
                    cci.modify(normalized);
                });
                person.setContactInfo(ci);
                personDAO.partialMerge(person, "contactInfo");
            }
            if (result.getResults().size() < limit) {
                break;
            } else {
                offset += limit;
            }
        }

        log.info("Patch for workers phone number normalization has ended");
    }

    private void uniteSeveralProductsInProjectToComplex() {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.PROJECT);
        caseQuery.setSortDir(En_SortDir.ASC);
        caseQuery.setSortField(En_SortField.case_name);

        List<CaseObject> projects = caseObjectDAO.listByQuery(caseQuery);

        jdbcManyRelationsHelper.fill(projects, "products");

        if (projects.isEmpty()) {
            return;
        }

        projects = projects
                .stream()
                .filter(project -> project.getProducts() != null && project.getProducts().size() > 1)
                .collect(toList());

        if (projects.isEmpty()) {
            return;
        }

        projects.forEach(project -> {
            DevUnit productMaybeWithCompositeName = Collections.max(project.getProducts(), Comparator.comparingInt(prod -> prod.getName().length()));

            String complexName = project.getProducts()
                    .stream()
                    .filter(prod -> !prod.getId().equals(productMaybeWithCompositeName.getId()))
                    .map(DevUnit::getName)
                    .reduce((name1, name2) -> name1 + " " + name2)
                    .get();

            if (productMaybeWithCompositeName.getName().equals(complexName)) {
                return;
            }

            complexName += " " + productMaybeWithCompositeName.getName();

            DevUnit complex = new DevUnit();
            complex.setName(complexName);
            complex.setStateId(En_DevUnitState.ACTIVE.getId());
            complex.setTypeId(En_DevUnitType.COMPLEX.getId());
            complex.setChildren(new ArrayList<>(project.getProducts()));
            complex.setCreated(new Date());

            Long complexId = devUnitDAO.persist(complex);
            complex.setId(complexId);
            jdbcManyRelationsHelper.persist(complex, "children");

            projectToProductDAO.persist(new ProjectToProduct(project.getId(), complexId));
        });
    }

    @Inject
    UserRoleDAO userRoleDAO;
    @Inject
    DecimalNumberDAO decimalNumberDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    PlatformDAO platformDAO;
    @Autowired
    CaseTagDAO caseTagDAO;
    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;
    @Autowired
    CaseFilterDAO caseFilterDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    DevUnitDAO devUnitDAO;
    @Autowired
    DevUnitChildRefDAO devUnitChildRefDAO;
    @Autowired
    ProjectToProductDAO projectToProductDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
}
