package ru.protei.portal.core.service.bootstrap;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.core.service.YoutrackService;
import ru.protei.portal.core.svn.document.DocumentSvnApi;
import ru.protei.portal.tools.migrate.struct.ExternalPersonAbsence;
import ru.protei.portal.tools.migrate.struct.ExternalPersonLeave;
import ru.protei.portal.tools.migrate.struct.ExternalReservedIp;
import ru.protei.portal.tools.migrate.struct.ExternalSubnet;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ru.protei.portal.core.model.dict.En_Gender.UNDEFINED;

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
//        uniteSeveralProductsInProjectToComplex();
        //createProjectsForContracts();
        documentBuildFullIndex();
        //fillImportanceLevels();
        migrateIpReservation();
        updateManagerFiltersWithoutManagerCompany();
        //fillWithCrossLinkColumn();
        //transferYoutrackLinks();
        addCommonManager();
        updateHistoryTable();
        updateIssueFiltersDateRanges();
        updateIssueReportDateRanges();
        migratePersonAbsences();
        updateWithCrossLinkColumn();
        transferProjectCrosslinkToYoutrack();
    }

    private void updateWithCrossLinkColumn() {

        log.debug("updateWithCrossLinkColumn(): start");

        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.PROJECT);
        List<CaseObject> projects = caseObjectDAO.getCases(caseQuery);

        log.debug("updateWithCrossLinkColumn(): projects size={}", projects.size());

        for (CaseObject project : projects) {
            try {
                CaseLinkQuery query = new CaseLinkQuery();
                query.setType(En_CaseLink.YT);
                query.setCaseId(project.getId());
                List<CaseLink> ytLinks = caseLinkDAO.getListByQuery(query);

                log.debug("updateWithCrossLinkColumn(): ytLinks={}", ytLinks);

                for (CaseLink caseLink : ytLinks) {
                    caseLink.setWithCrosslink(true);
                    caseLinkDAO.merge(caseLink);
                }

                log.debug("updateWithCrossLinkColumn(): successfully updated project ytLinks={}", ytLinks);
            } catch (Exception e){
                log.error("updateWithCrossLinkColumn(): failed to updated project={}, errorMessage={}", project, e.getMessage(), e);
            }
        }

        log.debug("updateWithCrossLinkColumn(): finish");
    }

    private void transferProjectCrosslinkToYoutrack() {
        if (!config.data().integrationConfig().isYoutrackProjectLinksMigrationEnabled() || !config.data().getCommonConfig().isProductionServer()){
            return;
        }

        log.debug("transferProjectCrosslinkToYoutrack(): start");

        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.PROJECT);
        List<CaseObject> projects = caseObjectDAO.getCases(caseQuery);

        log.debug("transferProjectCrosslinkToYoutrack(): projects size={}", projects.size());

        Set<String> youtrackIssueIds = new HashSet<>();

        for (CaseObject project : projects) {
            try {
                CaseLinkQuery query = new CaseLinkQuery();
                query.setType(En_CaseLink.YT);
                query.setCaseId(project.getId());
                List<CaseLink> ytLinks = caseLinkDAO.getListByQuery(query);

                log.debug("transferProjectCrosslinkToYoutrack(): ytLinks={}", ytLinks);

                for (CaseLink caseLink : ytLinks) {
                    youtrackIssueIds.add(caseLink.getRemoteId());
                }

            } catch (Exception e){
                log.error("transferProjectCrosslinkToYoutrack(): project={}, errorMessage={}", project, e.getMessage(), e);
            }
        }

        log.debug("transferProjectCrosslinkToYoutrack(): youtrackIssueIds size={}", youtrackIssueIds.size());
        youtrackIssueIds.forEach(youtrackIssueId -> {
            try {
                youtrackService.setIssueProjectNumbers(youtrackIssueId, findLinkedCaseIdsByTypeAndYoutrackId(En_CaseType.PROJECT, youtrackIssueId));
                log.debug("transferProjectCrosslinkToYoutrack(): SUCCESS transfer case links to youtrack id={}", youtrackIssueId);
            } catch (Exception e){
                log.error("transferProjectCrosslinkToYoutrack(): ERROR transfer case links to youtrack id={}, error message={}", youtrackIssueId, e.getMessage());
            }
        });

        log.debug("transferProjectCrosslinkToYoutrack(): finish");
    }

    private void fillWithCrossLinkColumn() {
        log.debug("fillWithCrossLinkColumn(): start");

        CaseLinkQuery query = new CaseLinkQuery();
        query.setType(En_CaseLink.YT);
        List<CaseLink> ytLinks = caseLinkDAO.getListByQuery(query);

        query.setType(En_CaseLink.CRM);
        List<CaseLink> crmLinks = caseLinkDAO.getListByQuery(query);

        if (ytLinks.stream().anyMatch(CaseLink::getWithCrosslink) || crmLinks.stream().anyMatch(CaseLink::getWithCrosslink)){
            log.debug("fillWithCrossLinkColumn(): column already filled");
            return;
        }

        //Для CRM ссылок, если флаг еще не заполнен, находим обратную ссылку. Если она есть, то обеим ссылкам ставим true
        for (CaseLink caseLink : crmLinks) {
            try {
                CaseLink crosslink = caseLinkDAO.getCrmLink(En_CaseLink.CRM, NumberUtils.toLong(caseLink.getRemoteId()), caseLink.getCaseId().toString());
                if (crosslink != null) {
                    caseLink.setWithCrosslink(true);
                    crosslink.setWithCrosslink(true);

                    caseLinkDAO.merge(caseLink);
                    caseLinkDAO.merge(crosslink);

                }
                log.debug("fillWithCrossLinkColumn(): successfully updated caseLink={}", caseLink);
            } catch (Exception e){
                log.error("fillWithCrossLinkColumn(): failed to update caseLink={}, errorMessage={}", caseLink, e.getMessage(), e);
            }
        }

        //Для YT ссылок проверяем тим caseObject. Если CRM_SUPPORT, то ставим флаг true. Иначе - false
        for (CaseLink caseLink : ytLinks) {
            try {
                    CaseObject caseObject = caseObjectDAO.get(caseLink.getCaseId());

                    if (caseObject == null) {
                        log.warn("fillWithCrossLinkColumn(): CaseObject is NULL from caseLink={}", caseLink);
                        continue;
                    }

                    if(En_CaseType.CRM_SUPPORT.equals(caseObject.getType())) {
                        caseLink.setWithCrosslink(true);
                        caseLinkDAO.merge(caseLink);
                    }
                log.debug("fillWithCrossLinkColumn(): successfully updated caseLink={}", caseLink);
            } catch (Exception e){
                log.error("fillWithCrossLinkColumn(): failed to update caseLink={}, errorMessage={}", caseLink, e.getMessage(), e);
            }
        }
        log.debug("fillWithCrossLinkColumn(): finish");
    }

    private void transferYoutrackLinks() {
        if (!config.data().integrationConfig().isYoutrackLinksMigrationEnabled() || !config.data().getCommonConfig().isProductionServer()){
            return;
        }

        log.debug("transferYoutrackLinks(): start transfer");

        CaseLinkQuery query = new CaseLinkQuery(null, null);
        query.setType(En_CaseLink.YT);
        query.setWithCrosslink(true);

        List<CaseLink> listByQuery = caseLinkDAO.getListByQuery(query);

        log.debug("transferYoutrackLinks(): quantity of YT case links={}", listByQuery.size());

        Set<String> youtrackIssueIds = listByQuery.stream()
                .map(CaseLink::getRemoteId)
                .collect(Collectors.toSet());

        log.debug("transferYoutrackLinks(): quantity of YT ids={}", youtrackIssueIds.size());

        youtrackIssueIds.forEach(youtrackIssueId -> {
            try {
                youtrackService.setIssueCrmNumbers(youtrackIssueId, findAllCaseNumbersByYoutrackId(youtrackIssueId, true));
                log.debug("transferYoutrackLinks(): SUCCESS transfer case links to youtrack id={}", youtrackIssueId);
            } catch (Exception e){
                log.error("transferYoutrackLinks(): ERROR transfer case links to youtrack id={}, error message={}", youtrackIssueId, e.getMessage());
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
                caseObject.setStateId(CrmConstants.State.CREATED);
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
        params.add(UNDEFINED.getCode());
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

    private void updateManagerFiltersWithoutManagerCompany() {
        List<CaseFilter> allFilters = caseFilterDAO.getAll();

        for (CaseFilter nextFilter : CollectionUtils.emptyIfNull(allFilters)) {
            CaseQuery params = nextFilter.getParams();

            if (CollectionUtils.isEmpty(params.getManagerIds()) || CollectionUtils.isNotEmpty(params.getManagerCompanyIds())) {
                continue;
            }

            params.setManagerCompanyIds(Collections.singletonList(CrmConstants.Company.HOME_COMPANY_ID));
            caseFilterDAO.partialMerge(nextFilter, "params");
        }
    }

    private void addCommonManager() {
        if (personDAO.getByCondition("displayname = 'Тех. поддержка NGN/ВКС'") != null) {
            return;
        }
        log.info("Add Common Manager started");

        Date created = java.sql.Timestamp.valueOf(LocalDateTime.now());
        Stream.of(
                "Тех. поддержка NGN/ВКС",
                "Тех. поддержка ИП",
                "Тех. поддержка Mobile",
                "Тех. поддержка Top Connect",
                "Тех. поддержка Billing",
                "Тех. поддержка ЦОВ",
                "Тех. поддержка 112",
                "Тех. поддержка DPI"
        ).map(name -> {
            Person manager = new Person();
            manager.setCreated(created);
            manager.setCreator("DBA");
            manager.setCompanyId(CrmConstants.Company.HOME_COMPANY_ID);
            manager.setLastName(name);
            manager.setDisplayName(name);
            manager.setDisplayShortName(name);
            manager.setGender(En_Gender.UNDEFINED);
            manager.setLocale(CrmConstants.LocaleTags.RU);
            return manager;
        }).forEach(personDAO::persist);
        log.info("Add Common Manager ended");
    }

    private void updateHistoryTable() {
        List<History> histories = historyDAO.getListByCondition("old_value is null and new_value is null");
        for (History history : histories) {
            if (history.getOldId()!= null) {
                Plan oldPlan = planDAO.get(history.getOldId());
                if (oldPlan != null) {
                    history.setOldValue(oldPlan.getName());
                }
            }
            if (history.getNewId() != null) {
                Plan newPlan = planDAO.get(history.getNewId());
                if (newPlan != null) {
                    history.setNewValue(newPlan.getName());
                }
            }
        }
        historyDAO.mergeBatch(histories);
    }

    /**
     * onetime method
     */
    private void updateIssueFiltersDateRanges() {
        log.info("updateIssueFiltersDateRanges started");

        List<CaseFilter> allFilters = caseFilterDAO.getAll();

        for (CaseFilter filter : CollectionUtils.emptyIfNull(allFilters)) {
            CaseQuery params = filter.getParams();

            boolean isCreatedRangeNeedToUpdate = checkDateRangeExists(params.getCreatedRange(), params.getCreatedFrom(), params.getCreatedTo());
            boolean isModifiedRangeNeedToUpdate = checkDateRangeExists(params.getModifiedRange(), params.getModifiedFrom(), params.getModifiedTo());

            if(isCreatedRangeNeedToUpdate) {
                params.setCreatedRange(createDateRange(params.getCreatedFrom(), params.getCreatedTo()));
            }

            if(isModifiedRangeNeedToUpdate) {
                params.setModifiedRange(createDateRange(params.getCreatedFrom(), params.getCreatedTo()));
            }

            if (isCreatedRangeNeedToUpdate || isModifiedRangeNeedToUpdate) {
                caseFilterDAO.partialMerge(filter, "params");
            }
        }
        log.info("updateIssueFiltersDateRanges ended");
    }

    private void updateIssueReportDateRanges() {
        log.info("updateIssueReportDateRanges started");

        List<Report> reports = reportDAO.getAll();

        for (Report report : CollectionUtils.emptyIfNull(reports)) {
            CaseQuery params = report.getCaseQuery();

            boolean isCreatedRangeNeedToUpdate = checkDateRangeExists(params.getCreatedRange(), params.getCreatedFrom(), params.getCreatedTo());
            boolean isModifiedRangeNeedToUpdate = checkDateRangeExists(params.getModifiedRange(), params.getModifiedFrom(), params.getModifiedTo());

            if(isCreatedRangeNeedToUpdate) {
                params.setCreatedRange(createDateRange(params.getCreatedFrom(), params.getCreatedTo()));
            }

            if(isModifiedRangeNeedToUpdate) {
                params.setModifiedRange(createDateRange(params.getCreatedFrom(), params.getCreatedTo()));
            }

            if (isCreatedRangeNeedToUpdate || isModifiedRangeNeedToUpdate) {
                reportDAO.partialMerge(report, "case_query");
            }
        }
        log.info("updateIssueReportDateRanges ended");
    }

    private boolean checkDateRangeExists(DateRange range, Date from, Date to) {
        return range == null && (from != null || to != null);
    }

    private DateRange createDateRange(Date from, Date to) {
        return new DateRange(En_DateIntervalType.FIXED, from, to);
    }

    private void migratePersonAbsences() {

        log.info("Migrate absences started");

        try {
            List<PersonAbsence> absences = personAbsenceDAO.getAll();
            if (CollectionUtils.isNotEmpty(absences)) {
                log.info("Need no to migrate absences data cause some data is already exist");
                return;
            }

            String migrationFromDate = dateTimeFormatter.format(setFirstDayOfMonth(new Date()));
            log.debug("Migration from date = {}", migrationFromDate);

            List<ExternalPersonAbsence> extAbsences = legacySystemDAO.getExternalAbsences(migrationFromDate);

            if (CollectionUtils.isEmpty(extAbsences)) {
                log.info("Need no to migrate absences data cause source is empty");
                return;
            }

            log.info("Count of absences {}", extAbsences.size());
            extAbsences.forEach(extAbsence -> {

                try {
                    PersonAbsence absence = new PersonAbsence();
                    absence.setCreated(extAbsence.getCreated());
                    Long creatorId = personDAO.getEmployeeByOldId(extAbsence.getSubmitterID()).getId();
                    Long personId = personDAO.getEmployeeByOldId(extAbsence.getPersonID()).getId();
                    absence.setCreatorId(creatorId);
                    absence.setPersonId(personId);
                    absence.setFromTime(extAbsence.getFromTime() == null ? extAbsence.getFromDate() : joinDateTime(extAbsence.getFromDate(), extAbsence.getFromTime()));
                    absence.setTillTime(extAbsence.getToTime() == null ?  setEndOfDay(extAbsence.getToDate()) : joinDateTime(extAbsence.getToDate(), extAbsence.getToTime()));
                    absence.setReason(absenceReasonMap.get(extAbsence.getReasonID()));
                    absence.setUserComment(extAbsence.getComment());
                    personAbsenceDAO.persist(absence);
                } catch (Exception e) {
                    log.warn("Not saved absence entry with id {}", extAbsence.getId());
                }
            });

            List<ExternalPersonLeave> extLeaves = legacySystemDAO.getExternalLeaves(migrationFromDate);

            if (CollectionUtils.isEmpty(extLeaves)) {
                log.info("Need no to migrate leaves data cause source is empty");
                return;
            }

            log.info("Count of leaves {}", extLeaves.size());
            extLeaves.forEach(extLeave -> {

                try {
                    PersonAbsence absence = new PersonAbsence();
                    absence.setCreated(extLeave.getCreated());
                    Long creatorId = personDAO.getEmployeeByOldId(extLeave.getSubmitterID()).getId();
                    Long personId = personDAO.getEmployeeByOldId(extLeave.getPersonID()).getId();
                    absence.setCreatorId(creatorId);
                    absence.setPersonId(personId);
                    absence.setFromTime(extLeave.getFromDate());
                    absence.setTillTime(setEndOfDay(extLeave.getToDate()));
                    absence.setReason(En_AbsenceReason.LEAVE);
                    absence.setUserComment(extLeave.getComment());
                    personAbsenceDAO.persist(absence);
                } catch (Exception e) {
                    log.warn("Not saved leave entry with id {}", extLeave.getId());
                }
            });

        } catch (Exception e) {
            log.warn("Unable to get absences data from prod DB", e);
        }

        log.info("Migrate absences ended");
    }

    private Date joinDateTime(Date date, Date time) {
        Date date_part = new Date(date.getTime());
        Date time_part = new Date(time.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time_part);
        date_part = DateUtils.setHours(date_part, calendar.get(Calendar.HOUR_OF_DAY));
        date_part = DateUtils.setMinutes(date_part, calendar.get(Calendar.MINUTE));
        return date_part;
    }

    private Date setEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        return calendar.getTime();
    }

    private Date setFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private List<CaseObject> findCaseObjectsByTypeAndYoutrackId(En_CaseType caseType, String youtrackId, Boolean withCrosslink){
        List<Long> linkedCaseIds = findAllCaseIdsByYoutrackId(youtrackId, withCrosslink);

        if (CollectionUtils.isEmpty(linkedCaseIds)){
            return new ArrayList<>();
        }

        CaseQuery query = new CaseQuery();
        query.setCaseIds(linkedCaseIds);
        query.setType(caseType);
        return caseObjectDAO.getCases(query);
    }

    private List<Long> findAllCaseIdsByYoutrackId(String youtrackId, Boolean withCrosslink) {
        return findAllCaseLinksByYoutrackId(youtrackId, withCrosslink).stream()
                .map(CaseLink::getCaseId)
                .collect(Collectors.toList());
    }

    private List<CaseLink> findAllCaseLinksByYoutrackId(String youtrackId, Boolean withCrosslink){
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setRemoteId( youtrackId );
        caseLinkQuery.setType( En_CaseLink.YT );
        caseLinkQuery.setWithCrosslink(withCrosslink);
        return caseLinkDAO.getListByQuery(caseLinkQuery);
    }

    private List<Long> findLinkedCaseIdsByTypeAndYoutrackId(En_CaseType caseType, String youtrackId){
        List<CaseObject> cases = findCaseObjectsByTypeAndYoutrackId(caseType, youtrackId, true);

        if (CollectionUtils.isEmpty(cases)){
            return new ArrayList<>();
        }

        return cases.stream()
                .map(caseObject -> caseObject.getId())
                .collect(Collectors.toList());
    }

    private List<Long> findAllCaseNumbersByYoutrackId(String youtrackId, Boolean withCrosslink){
        List<CaseObject> caseObjects = caseObjectDAO.getListByKeys(findAllCaseIdsByYoutrackId(youtrackId, withCrosslink));

        return caseObjects.stream()
                .map(CaseObject::getCaseNumber)
                .collect(Collectors.toList());
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
    ReportDAO reportDAO;
    @Autowired
    YoutrackService youtrackService;
    @Autowired
    PortalConfig config;
    @Autowired
    HistoryDAO historyDAO;
    @Autowired
    PlanDAO planDAO;
    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private static Map<Long, En_AbsenceReason> absenceReasonMap = new HashMap<>();
    static {
        absenceReasonMap.put(1L, En_AbsenceReason.BUSINESS_TRIP);
        absenceReasonMap.put(2L, En_AbsenceReason.LEAVE);
        absenceReasonMap.put(3L, En_AbsenceReason.DISEASE);
        absenceReasonMap.put(4L, En_AbsenceReason.PERSONAL_AFFAIR);
        absenceReasonMap.put(5L, En_AbsenceReason.LOCAL_BUSINESS_TRIP);
        absenceReasonMap.put(6L, En_AbsenceReason.STUDY);
        absenceReasonMap.put(7L, En_AbsenceReason.SICK_LEAVE);
        absenceReasonMap.put(8L, En_AbsenceReason.GUEST_PASS);
        absenceReasonMap.put(9L, En_AbsenceReason.NIGHT_WORK);
        absenceReasonMap.put(10L, En_AbsenceReason.LEAVE_WITHOUT_PAY);
        absenceReasonMap.put(11L, En_AbsenceReason.DUTY);
        absenceReasonMap.put(12L, En_AbsenceReason.REMOTE_WORK);
    }
}
