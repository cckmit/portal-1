package ru.protei.portal.core.service.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.SVNException;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.index.document.DocumentStorageIndex;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.PhoneUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.YoutrackService;
import ru.protei.portal.core.svn.document.DocumentSvnApi;
import ru.protei.portal.tools.migrate.struct.ExternalReservedIp;
import ru.protei.portal.tools.migrate.struct.ExternalSubnet;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static ru.protei.portal.api.struct.Result.error;

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
    };

    @PostConstruct
    public void init() {
        migrateUserRoleScopeToSingleValue();
        removeObsoletePrivileges();
//        autoPatchDefaultRoles();
        createSFPlatformCaseObjects();
        updateCompanyCaseTags();
        //patchNormalizeWorkersPhoneNumbers(); // remove once executed
        uniteSeveralProductsInProjectToComplex();
        //createProjectsForContracts();
        documentBuildFullIndex();
        //fillImportanceLevels();
        migrateIpReservation();
       // transferYoutrackLinks();
    }

    private void transferYoutrackLinks() {
        //Добавить проверку, что метод уже выполнялся
        if (!config.data().youtrack().getAdminProject().equals("ACRM")
                || !config.data().integrationConfig().isYoutrackEnabled()) {
            return;
        }

        CaseLinkQuery query = new CaseLinkQuery(null, null);
        query.setType(En_CaseLink.YT);

        List<CaseLink> listByQuery = caseLinkDAO.getListByQuery(query);

        listByQuery.forEach(caseLink -> {
            Long caseNumber = caseObjectDAO.getCaseNumberById(caseLink.getCaseId());
            String youtrackId = caseLink.getRemoteId();

            //может надо добавить только если >= 1000000?
            if (caseNumber != null) {
                youtrackService.addIssueCrmNumber(youtrackId, caseNumber);
            }
        });
    }

    private void fillImportanceLevels() {
        if(importanceLevelDAO.getAll().size() == 4){
            importanceLevelDAO.persist(new ImportanceLevel(5L, "medium", "medium"));
        }

        if (!companyImportanceItemDAO.getAll().isEmpty()){
            return;
        }

        List<Company> companies = companyDAO.getAll();
        List<CompanyImportanceItem> importanceItems = new ArrayList<>();

        for (Company company : companies) {
            if (company.getId() > 0) {
                for (En_ImportanceLevel value : En_ImportanceLevel.values(true)) {
                    importanceItems.add(new CompanyImportanceItem(company.getId(), value.getId(), value.getId()));
                }
            }
        }
        companyImportanceItemDAO.persistBatch(importanceItems);
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
                caseObject.setType(En_CaseType.SF_PLATFORM);
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
            String complexName = project.getProducts()
                    .stream()
                    .map(DevUnit::getName)
                    .reduce((name1, name2) -> name1 + " " + name2)
                    .get();

            DevUnit complex = new DevUnit();
            complex.setName(complexName);
            complex.setStateId(En_DevUnitState.ACTIVE.getId());
            complex.setType(En_DevUnitType.COMPLEX);
            complex.setChildren(project.getProducts().stream().filter(DevUnit::isProduct).collect(toList()));
            complex.setCreated(new Date());

            Long complexId = devUnitDAO.persist(complex);
            complex.setId(complexId);
            jdbcManyRelationsHelper.persist(complex, "children");

            projectToProductDAO.removeAllProductsFromProject(project.getId());
            projectToProductDAO.persist(new ProjectToProduct(project.getId(), complexId));
        });
    }

//    private void createProjectsForContracts() {
//        List<Contract> contracts = contractDAO.getAll();
//
//        if (contracts == null) {
//            return;
//        }
//
//        contracts
//                .stream()
//                .filter(contract -> contract.getProjectId() == null)
//                .forEach(contract -> {
//                    CaseObject contractAsCaseObject = caseObjectDAO.get(contract.getId());
//
//                    CaseObject project = new CaseObject();
//                    project.setName("Проект для договора №" + contract.getNumber());
//                    project.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.PROJECT));
//                    project.setType(En_CaseType.PROJECT);
//                    project.setCreated(new Date());
//                    project.setStateId(En_RegionState.UNKNOWN.getId());
//                    project.setLocal(En_CustomerType.COMMERCIAL_PROTEI.getId());
//                    project.setInitiatorCompanyId(contractAsCaseObject.getInitiatorCompanyId());
//                    project.setProductId(contractAsCaseObject.getProductId());
//                    project.setManagerId(contractAsCaseObject.getManagerId());
//
//                    Long caseId = caseObjectDAO.persist(project);
//
//                    if (contractAsCaseObject.getManagerId() != null) {
//                        CaseMember caseMember = new CaseMember();
//                        caseMember.setCaseId(caseId);
//                        caseMember.setRole(En_DevUnitPersonRoleType.HEAD_MANAGER);
//                        caseMember.setMemberId(contractAsCaseObject.getManagerId());
//                        caseMemberDAO.persist(caseMember);
//                    }
//                    contract.setProjectId(caseId);
//                    contractDAO.merge(contract);
//                });
//    }

    private void documentBuildFullIndex() { // Данный метод создаст индексы для всех существующих документов
if(true) return; //TODO remove
        try {
            if (!Objects.equals(config.data().getCommonConfig().getCrmUrlCurrent(), config.data().getCommonConfig().getCrmUrlInternal())) {
                // disable index at non internal stand
                return;
            }
            if (documentStorageIndex.isIndexExists()) {
                log.warn("Document build full index - execution prevented. Consider to disable documentBuildFullIndex() method.");
                return;
            }
        } catch (IOException e) {
            log.warn("Document build full index - execution prevented. Consider to disable documentBuildFullIndex() method.", e);
            return;
        }

        log.info("Document index full build has started");

        List<Document> partialDocuments = documentDAO.partialGetAll("id", "project_id");
        int size = partialDocuments.size();
        for (int i = 0; i < size; i++) {
            Long documentId = partialDocuments.get(i).getId();
            Long projectId = partialDocuments.get(i).getProjectId();
            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                documentSvnApi.getDocument(projectId, documentId, En_DocumentFormat.PDF, out);
                final byte[] fileData = out.toByteArray();
                if (fileData.length == 0) {
                    log.warn("Content for document({}) not found, {}/{}", documentId, i + 1, size);
                    continue;
                }
                documentStorageIndex.addPdfDocument(fileData, documentId, projectId);
                log.info("Index created for document({}), {}/{}", documentId, i + 1, size);
            } catch (SVNException e) {
                log.warn("Content for document(" + documentId + ") not found, " + (i + 1) + "/" + size, e);
            } catch (Exception e) {
                log.warn("Failed to create index for document(" + documentId + "), " + (i + 1) + "/" + size, e);
            }
        }

        log.info("Document index full build has ended");
    }

    private void migrateIpReservation() {
        log.info("Migrate subnets and reservedIps started");

        try {
            List<Subnet> subnets = subnetDAO.listByQuery(
                    new ReservedIpQuery(null, En_SortField.address, En_SortDir.ASC));
            if (CollectionUtils.isNotEmpty(subnets)) {
                log.info("Need no to migrate IP reservation data cause some data is already exist");
                return;
            }

            List<ExternalSubnet> extSubnets = legacySystemDAO.getExternalSubnets();

            if (CollectionUtils.isEmpty(extSubnets)) {
                log.info("Need no to migrate IP reservation data cause source is empty");
                return;
            }
            List<ExternalReservedIp> extReservedIps = legacySystemDAO.getExternalReservedIps();

            extSubnets.forEach( extSubnet -> {

                Subnet subnet = new Subnet();
                subnet.setCreated(extSubnet.getCreated());

                Long subnetCreatorId = getEmployeeIdByFIO(extSubnet.getCreator(), 288L);

                subnet.setCreatorId(subnetCreatorId);
                subnet.setAddress(extSubnet.getSubnetAddress());
                subnet.setMask(CrmConstants.IpReservation.SUBNET_MASK);
                subnet.setComment(extSubnet.getComment());

                Long subnetId = subnetDAO.persist(subnet);
                if (subnetId != null) {
                    extReservedIps.stream().filter( extReservedIp ->
                            extReservedIp.getSubnetId().equals(extSubnet.getId()))
                            .forEach( extReservedIp -> {
                                ReservedIp reservedIp = new ReservedIp();

                                Long ownerId = personDAO.getEmployeeByOldId(extReservedIp.getCustomerID()).getId();
                                Long ipCreatorId = getEmployeeIdByFIO(extReservedIp.getCreator(), ownerId);

                                reservedIp.setCreatorId(ipCreatorId);
                                reservedIp.setOwnerId(ownerId);
                                reservedIp.setCreated(extReservedIp.getCreated());
                                reservedIp.setSubnetId(subnetId);
                                reservedIp.setIpAddress(extReservedIp.getIpAddress());
                                reservedIp.setReserveDate(extReservedIp.getDtReserve());
                                if (!extReservedIp.isForLongTime()) {
                                    reservedIp.setReleaseDate(extReservedIp.getDtRelease());
                                }
                                reservedIp.setComment(extReservedIp.getComment());

                                reservedIpDAO.persist(reservedIp);
                            });
                }
            });

        } catch (Exception e) {
            log.warn("Unable to get IP reservation data from prod DB", e);
        }

        log.info("Migrate subnets and reservedIps ended");
    }

    private Long getEmployeeIdByFIO(String fio, Long defaultId) {
        try {
            EmployeeQuery query = new EmployeeQuery();
            query.setLastName(fio.substring(0, fio.indexOf(" ")));
            List<Person> employees = personDAO.getEmployees(query);
            if (CollectionUtils.isNotEmpty(employees)) {
                return employees.get(0).getId();
            }
        } catch (Exception e ) {}
        return defaultId;
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
    ContractDAO contractDAO;
    @Autowired
    CaseMemberDAO caseMemberDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    DocumentDAO documentDAO;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    CompanyImportanceItemDAO companyImportanceItemDAO;
    @Autowired
    ImportanceLevelDAO importanceLevelDAO;
    @Autowired
    DocumentSvnApi documentSvnApi;
    @Autowired
    DocumentStorageIndex documentStorageIndex;
    @Autowired
    LegacySystemDAO legacySystemDAO;
    @Autowired
    SubnetDAO subnetDAO;
    @Autowired
    ReservedIpDAO reservedIpDAO;
    @Autowired
    CaseLinkDAO caseLinkDAO;
    @Autowired
    YoutrackService youtrackService;
    @Autowired
    PortalConfig config;
}
