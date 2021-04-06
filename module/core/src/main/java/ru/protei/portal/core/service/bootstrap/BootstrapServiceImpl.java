package ru.protei.portal.core.service.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.index.document.DocumentStorageIndex;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.PortalBaseJdbcDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ReportCaseQuery;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.PhoneUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.YoutrackService;
import ru.protei.portal.core.svn.document.DocumentSvnApi;
import ru.protei.portal.tools.migrate.struct.ExternalPersonAbsence;
import ru.protei.portal.tools.migrate.struct.ExternalPersonLeave;
import ru.protei.portal.tools.migrate.struct.ExternalReservedIp;
import ru.protei.portal.tools.migrate.struct.ExternalSubnet;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcDAO;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;
import ru.protei.winter.jdbc.JdbcObjectMapperRegistrator;
import ru.protei.winter.jdbc.annotations.ConverterType;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class BootstrapServiceImpl implements BootstrapService {

    private static Logger log = LoggerFactory.getLogger( BootstrapServiceImpl.class );

    private final static En_Privilege[] OBSOLETE_DB_PRIVILEGES = {
            En_Privilege.ISSUE_COMPANY_EDIT,
            En_Privilege.ISSUE_PRODUCT_EDIT,
            En_Privilege.ISSUE_MANAGER_EDIT,
            En_Privilege.ISSUE_PRIVACY_VIEW,
            En_Privilege.DASHBOARD_ALL_COMPANIES_VIEW,
    };

    @Transactional
    @Override
    public void bootstrapApplication() {
        log.info( "bootstrapApplication(): BootstrapService begin."  );
        /**
         *  begin Спринт 58 */

        migrateUserRoleScopeToSingleValue();
        removeObsoletePrivileges();
//        autoPatchDefaultRoles();
        createSFPlatformCaseObjects();
        updateCompanyCaseTags();
        //patchNormalizeWorkersPhoneNumbers(); // remove once executed
//        uniteSeveralProductsInProjectToComplex();
        //createProjectsForContracts();
//        documentBuildFullIndex();
        //fillImportanceLevels();
        migrateIpReservation();
        updateManagerFiltersWithoutManagerCompany();
        //fillWithCrossLinkColumn();
        //transferYoutrackLinks();
        //addCommonManager();
        updateHistoryTable();
        updateIssueFiltersDateRanges();
        updateIssueReportDateRanges();
        migratePersonAbsences();
        if (!bootstrapAppDAO.isActionExists( "updateWithCrossLinkColumn" )) {
            updateWithCrossLinkColumn();
            bootstrapAppDAO.createAction( "updateWithCrossLinkColumn" );
        }
        if(!bootstrapAppDAO.isActionExists( "transferProjectCrosslinkToYoutrack" )) {
            transferProjectCrosslinkToYoutrack();
            bootstrapAppDAO.createAction("transferProjectCrosslinkToYoutrack");
        }
        updateUserDashboardOrders();
        if(!bootstrapAppDAO.isActionExists( "ContactInfoPersonMigration" )) {
            ContactInfoPersonMigration.migrate( applicationContext );
            bootstrapAppDAO.createAction("ContactInfoPersonMigration");
        }
        if(!bootstrapAppDAO.isActionExists( "ContactInfoCompanyMigration" )) {
            ContactInfoCompanyMigration.migrate( applicationContext );
            bootstrapAppDAO.createAction("ContactInfoCompanyMigration");
        }
        if(!bootstrapAppDAO.isActionExists("normalizeEmails")) {
            normalizeEmails();
            bootstrapAppDAO.createAction("normalizeEmails");
        }
        if(!bootstrapAppDAO.isActionExists("updateIssueFiltersCompanyManager")) {
            updateIssueFiltersManager();
            bootstrapAppDAO.createAction("updateIssueFiltersCompanyManager");
        }
        if(!bootstrapAppDAO.isActionExists("updateIssueReportsCompanyManager")) {
            updateIssueReportsManager();
            bootstrapAppDAO.createAction("updateIssueReportsCompanyManager");
        }

        /**
         *  end Спринт 58 */

        /**
         *  begin Спринт 62 */

        if(!bootstrapAppDAO.isActionExists( "AddContractEmployee" )) {
            this.addContractEmployee();
            bootstrapAppDAO.createAction("AddContractEmployee");
        }

        /**
         *  end Спринт 62 */

        /**
         *  begin Спринт 63 */
        if(!bootstrapAppDAO.isActionExists( "normalizePhoneNumbers" )) {
            this.normalizePhoneNumbers();
            bootstrapAppDAO.createAction("normalizePhoneNumbers");
        }
        /**
         *  end Спринт */

        log.info( "bootstrapApplication(): BootstrapService complete."  );

        /**
         * begin Спринт 67 */

        if (!bootstrapAppDAO.isActionExists("migrateEmployeeRegistrationHistory")) {
            this.migrateEmployeeRegistrationHistory();
            bootstrapAppDAO.createAction("migrateEmployeeRegistrationHistory");
        }

        if (!bootstrapAppDAO.isActionExists("removeStateImportanceManagerComments")) {
            this.removeStateImportanceManagerComments();
            bootstrapAppDAO.createAction("removeStateImportanceManagerComments");
        }

        if (!bootstrapAppDAO.isActionExists("correctCaseStateNamesInHistory")) {
            this.correctCaseStateNamesInHistory();
            bootstrapAppDAO.createAction("correctCaseStateNamesInHistory");
        }

        /**
         *  end Спринт */

        /**
         * begin Спринт 68 */
        if (!bootstrapAppDAO.isActionExists("updateContactItemsAccessType")) {
            this.updateContactItemsAccessType();
            bootstrapAppDAO.createAction("updateContactItemsAccessType");
        }

        /**
         *  end Спринт */
    }

    private void updateContactItemsAccessType() {
        List<ContactItem> contactItems = contactItemDAO.getListByCondition("access_type is null");
        contactItems.forEach(item -> item.modify(En_ContactDataAccess.PUBLIC));
        contactItemDAO.saveOrUpdateBatch(contactItems);
    }

    private void correctCaseStateNamesInHistory() {
        Map<Long, String> caseStateToName = caseStateDAO.getAll()
                .stream()
                .collect(Collectors.toMap(CaseState::getId, CaseState::getState));

        HistoryQuery historyQuery = new HistoryQuery();
        historyQuery.addValueType(En_HistoryType.CASE_STATE);

        List<History> caseStateHistoryList = historyDAO.getListByQuery(historyQuery);

        caseStateHistoryList.forEach(history -> {
            if (history.getOldId() != null) {
                history.setOldValue(caseStateToName.get(history.getOldId()));
            }

            if (history.getNewId() != null) {
                history.setNewValue(caseStateToName.get(history.getNewId()));
            }
        });

        historyDAO.mergeBatch(caseStateHistoryList);
    }

    private void migrateEmployeeRegistrationHistory() {
        List<Long> employeeRegistrationIds = toList(employeeRegistrationDAO.getAll(), EmployeeRegistration::getId);

        if (!employeeRegistrationIds.isEmpty()) {
            historyDAO.removeByCondition("history.case_object_id IN " + HelperFunc.makeInArg(employeeRegistrationIds, false));
        }

        CaseCommentQuery caseCommentQuery = new CaseCommentQuery();
        caseCommentQuery.setCaseObjectIds(employeeRegistrationIds);

        Map<Long, List<CaseComment>> caseIdToComments = caseCommentDAO.getCaseComments(caseCommentQuery)
                .stream()
                .collect(Collectors.toMap(CaseComment::getCaseId, Arrays::asList, CollectionUtils::mergeLists));

        for (Map.Entry<Long, List<CaseComment>> nextEntry : caseIdToComments.entrySet()) {
            List<CaseComment> caseComments = getStateCommentsSortedByDate(nextEntry.getValue());

            if (isEmpty(caseComments)) {
                continue;
            }

            Map<Long, CaseComment> remoteLinkIdToLastComment = new HashMap<>();

            for (CaseComment caseComment : caseComments) {
                History history = historyFromComments(caseComment, remoteLinkIdToLastComment.get(caseComment.getRemoteLinkId()));
                Long historyId = historyDAO.persist(history);

                EmployeeRegistrationHistory employeeRegistrationHistory
                        = createEmployeeRegistrationHistory(historyId, caseComment);

                employeeRegistrationHistoryDAO.persist(employeeRegistrationHistory);

                remoteLinkIdToLastComment.put(caseComment.getRemoteLinkId(), caseComment);
            }
        }
    }

    private void removeStateImportanceManagerComments() {
        Condition removeRedundantCommentsCondition = condition()
                .and("case_comment.comment_text").isNull(true)
                .and(condition()
                        .or("case_comment.CSTATE_ID").not().isNull(true)
                        .or("case_comment.cimp_level").not().isNull(true)
                        .or("case_comment.cmanager_id").not().isNull(true)
                        .or(
                                condition()
                                        .and("case_comment.CSTATE_ID").isNull(true)
                                        .and("case_comment.cimp_level").isNull(true)
                                        .and("case_comment.cmanager_id").isNull(true)
                                        .and("case_comment.time_elapsed").isNull(true))
                );

        caseCommentDAO.removeByCondition(removeRedundantCommentsCondition.getSqlCondition());
    }

    private EmployeeRegistrationHistory createEmployeeRegistrationHistory(Long historyId, CaseComment caseComment) {
        EmployeeRegistrationHistory employeeRegistrationHistory = new EmployeeRegistrationHistory();

        employeeRegistrationHistory.setHistoryId(historyId);
        employeeRegistrationHistory.setRemoteLinkId(caseComment.getRemoteLinkId());
        employeeRegistrationHistory.setOriginalAuthorName(
                caseComment.getOriginalAuthorFullName() != null ?
                        caseComment.getOriginalAuthorFullName() :
                        caseComment.getOriginalAuthorName()
        );

        return employeeRegistrationHistory;
    }

    private History historyFromComments(CaseComment caseComment, CaseComment lastComment) {
        History history = new History();

        history.setCaseObjectId(caseComment.getCaseId());
        history.setDate(caseComment.getCreated());
        history.setType(En_HistoryType.CASE_STATE);
        history.setAction(lastComment == null ? En_HistoryAction.ADD : En_HistoryAction.CHANGE);
        history.setOldId(lastComment == null ? null : lastComment.getCaseStateId());
        history.setOldValue(lastComment == null ? null : lastComment.getCaseStateName());
        history.setNewId(caseComment.getCaseStateId());
        history.setNewValue(caseComment.getCaseStateName());
        history.setInitiatorId(caseComment.getAuthorId());

        return history;
    }

    private List<CaseComment> getStateCommentsSortedByDate(List<CaseComment> comments) {
        return stream(comments)
                .filter(caseComment -> caseComment.getCaseStateId() != null)
                .sorted(this::compareCommentsByDate)
                .collect(toList());
    }

    private int compareCommentsByDate(CaseComment comment1, CaseComment comment2) {
        if (comment1.getCreated().before(comment2.getCreated())) {
            return -1;
        } else if (comment2.getCreated().before(comment1.getCreated())) {
            return 1;
        } else {
            return 0;
        }
    }

    private void normalizeEmails() {

        log.debug("normalizeEmails(): start");

        Condition cnd = query().where("person.id").in(query()
                .select("cip.person_id").from("contact_item_person AS cip")
                .where("cip.contact_item_id").in(query()
                        .select("ci.id").from("contact_item AS ci")
                        .where("ci.item_type").equal(En_ContactItemType.EMAIL.getId())
                        .and("ci.value").like("%,%").asQuery()
                ).asQuery()
        );

        List<Person> personList = personDAO.getListByCondition(cnd.getSqlCondition(), cnd.getSqlParameters());
        jdbcManyRelationsHelper.fill(personList, "contactItems");

        personList.forEach(person ->  {

            List<ContactItem> emails = person.getContactItems().stream()
                    .filter(contactItem -> En_ContactItemType.EMAIL.equals(contactItem.type()))
                    .filter(contactItem -> contactItem.value().contains(","))
                    .map(contactItem ->
                        Arrays.stream(contactItem.value().split(","))
                                .filter(HelperFunc::isNotEmpty)
                                .map(s -> new ContactItem(En_ContactItemType.EMAIL, contactItem.accessType()).modify(s.trim()))
                                .collect(Collectors.toList())
                    )
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            person.getContactItems().addAll(emails);

            contactItemDAO.saveOrUpdateBatch(person.getContactItems());
            jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);
        });

        contactItemDAO.removeByKeys(personList.stream()
                .map(Person::getContactItems)
                .flatMap(Collection::stream)
                .filter(contactItem -> En_ContactItemType.EMAIL.equals(contactItem.type()))
                .filter(contactItem -> contactItem.value().contains(","))
                .map(ContactItem::id)
                .collect(Collectors.toList()));

        log.debug("normalizeEmails(): stop");
    }

    private void updateIssueFiltersManager() {

        log.debug("updateIssueFiltersManager(): start");

        List<CaseFilter> filterList = caseFilterDAO.getListByCondition("type=?", En_CaseFilterType.CASE_OBJECTS.name());
        filterList.forEach(filter ->  {

            UserLogin userLogin = userLoginDAO.get(filter.getLoginId());

            if (userLogin.getCompanyId() != CrmConstants.Company.HOME_COMPANY_ID) {
                CaseQuery caseQuery;
                try {
                    caseQuery = objectMapper.readValue(filter.getParams(), CaseQuery.class);
                } catch (IOException e) {
                    log.debug("updateIssueFiltersManager(): cannot read filter params. params={}", filter.getParams());
                    e.printStackTrace();
                    return;
                }

                boolean isManagerCompanyIdsNeedToUpdate = isNotEmpty(caseQuery.getManagerCompanyIds());
                boolean isManagerIdsNeedToUpdate = isNotEmpty(caseQuery.getManagerIds());
                if (isManagerCompanyIdsNeedToUpdate) {
                    caseQuery.getManagerCompanyIds().remove(userLogin.getCompanyId());
                }
                if (isManagerIdsNeedToUpdate) {
                    caseQuery.getManagerIds().clear();
                }
                if (isManagerCompanyIdsNeedToUpdate || isManagerIdsNeedToUpdate) {
                    caseFilterDAO.partialMerge(filter, "params");
                }
            }
        });

        log.debug("updateIssueFiltersManager(): stop");
    }

    private void updateIssueReportsManager() {

        log.debug("updateIssueReportsManager(): start");

        List<Report> reportList = reportDAO.getListByCondition("type=? and is_removed=?", En_ReportType.CASE_OBJECTS.name(), false);
        reportList.forEach(report ->  {
            try {
                if (report.getCreator().getCompanyId() != CrmConstants.Company.HOME_COMPANY_ID) {

                    CaseQuery caseQuery = new ReportCaseQuery(
                            report,
                            objectMapper.readValue(report.getQuery(), CaseQuery.class)
                    ).getQuery();

                    boolean isManagerCompanyIdsNeedToUpdate = isNotEmpty(caseQuery.getManagerCompanyIds());
                    boolean isManagerIdsNeedToUpdate = isNotEmpty(caseQuery.getManagerIds());
                    if (isManagerCompanyIdsNeedToUpdate) {
                        caseQuery.getManagerCompanyIds().remove(report.getCreator().getCompanyId());
                    }
                    if (isManagerIdsNeedToUpdate) {
                        caseQuery.getManagerIds().clear();
                    }
                    if (isManagerCompanyIdsNeedToUpdate || isManagerIdsNeedToUpdate) {
                        report.setQuery(objectMapper.writeValueAsString(caseQuery));
                        reportDAO.partialMerge(report, "case_query");
                    }
                }
            } catch (Exception e) {
                log.error("updateIssueReportsManager(): failed to update issue report with id = " + report.getId(), e);
            }
        });

        log.debug("updateIssueReportsManager(): stop");
    }

    private void updateUserDashboardOrders() {
        List<UserDashboard> dashboards = userDashboardDAO.getAll();

        if (dashboards.stream().allMatch(userDashboard -> userDashboard.getOrderNumber() != null)) {
            return;
        }

        Map<Long, List<UserDashboard>> loginIdToDashboards = dashboards.stream().collect(Collectors.groupingBy(UserDashboard::getLoginId, toList()));

        loginIdToDashboards.forEach((key, userDashboards) -> {
            fillOrders(userDashboards);
            userDashboardDAO.mergeBatch(userDashboards);
        });
    }

    private void fillOrders(List<UserDashboard> dashboards) {
        for (int i = 0; i < dashboards.size(); i++) {
            dashboards.get(i).setOrderNumber(i);
        }
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


    private void migrateUserRoleScopeToSingleValue() {
        log.info( "Start migrate user role scope to single values" );
        userRoleDAO.trimScopeToSingleValue();
    }

    private void removeObsoletePrivileges() {
        List<En_Privilege> obsoletePrivileges = asList(OBSOLETE_DB_PRIVILEGES);
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

    private void migrateIpReservation() {
        log.info("Migrate subnets and reservedIps started");

        try {
            List<Subnet> subnets = subnetDAO.listByQuery(
                    new ReservedIpQuery(null, En_SortField.address, En_SortDir.ASC));
            if (isNotEmpty(subnets)) {
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
            List<PersonShortView> employees = personShortViewDAO.getEmployees(query);
            if (isNotEmpty(employees)) {
                return employees.get(0).getId();
            }
        } catch (Exception e ) {}
        return defaultId;
    }

    private void updateManagerFiltersWithoutManagerCompany() {

        List<CaseFilter> allFilters = caseFilterDAO.getListByFilterTypes(En_CaseFilterType.getTypesByClass(CaseQuery.class));

        for (CaseFilter nextFilter : emptyIfNull(allFilters)) {
            CaseQuery query;

            try {
                query = objectMapper.readValue(nextFilter.getParams(), CaseQuery.class);
            } catch (IOException e) {
                log.info("updateManagerFiltersWithoutManagerCompany: cannot read filter params. params={}", nextFilter.getParams());
                e.printStackTrace();
                return;
            }

            if (CollectionUtils.isEmpty(query.getManagerIds()) || isNotEmpty(query.getManagerCompanyIds())) {
                continue;
            }

            query.setManagerCompanyIds(Collections.singletonList(CrmConstants.Company.HOME_COMPANY_ID));
            caseFilterDAO.partialMerge(nextFilter, "params");
        }
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

        List<CaseFilter> allFilters = caseFilterDAO.getListByFilterTypes(En_CaseFilterType.getTypesByClass(CaseQuery.class));

        for (CaseFilter filter : emptyIfNull(allFilters)) {
            CaseQuery query;

            try {
                query = objectMapper.readValue(filter.getParams(), CaseQuery.class);
            } catch (IOException e) {
                log.warn("updateIssueFiltersDateRanges: cannot read filter params. params={}", filter.getParams());
                e.printStackTrace();
                return;
            }

            boolean isCreatedRangeNeedToUpdate = checkDateRangeExists(query.getCreatedRange(), query.getCreatedFrom(), query.getCreatedTo());
            boolean isModifiedRangeNeedToUpdate = checkDateRangeExists(query.getModifiedRange(), query.getModifiedFrom(), query.getModifiedTo());

            if(isCreatedRangeNeedToUpdate) {
                query.setCreatedRange(createDateRange(query.getCreatedFrom(), query.getCreatedTo()));
            }

            if(isModifiedRangeNeedToUpdate) {
                query.setModifiedRange(createDateRange(query.getCreatedFrom(), query.getCreatedTo()));
            }

            if (isCreatedRangeNeedToUpdate || isModifiedRangeNeedToUpdate) {
                caseFilterDAO.partialMerge(filter, "params");
            }
        }
        log.info("updateIssueFiltersDateRanges ended");
    }

    private void updateIssueReportDateRanges() {
        log.info("updateIssueReportDateRanges started");

        ReportQuery query = new ReportQuery();
        query.setTypes(asList(
                En_ReportType.CASE_OBJECTS,
                En_ReportType.CASE_RESOLUTION_TIME,
                En_ReportType.CASE_TIME_ELAPSED,
                En_ReportType.PROJECT
        ));
        List<Report> reports = reportDAO.getReports(query);

        for (Report report : emptyIfNull(reports)) {
            CaseQuery params;
            try {
                params = objectMapper.readValue(report.getQuery(), CaseQuery.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
            if (isNotEmpty(absences)) {
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

    private void addContractEmployee() {
        log.info("addContractEmployee started");
        final Date now = java.sql.Timestamp.valueOf(LocalDateTime.now());

        final Person person = new Person();
        person.setCreated(now);
        person.setCreator("BootstrapService");
        person.setCompanyId(companyGroupHomeDAO.mainCompanyId());
        person.setPosition("Не определена");
        person.setDepartment("не определено");
        person.setFirstName("Сотрудник");
        person.setLastName("по");
        person.setSecondName("контракту");
        person.setDisplayName("Сотрудник по контракту");
        person.setDisplayShortName("Сотрудник по контракту");
        person.setGender(En_Gender.UNDEFINED);
        person.setInfo("Сотрудник по контракту - PORTAL-1538");
        person.setLocale(CrmConstants.LocaleTags.RU);
        personDAO.persist(person);

        final UserLogin login = new UserLogin();
        login.setUlogin("contractemployee@protei.ru");
        login.setCreated(now);
        login.setAdminStateId(En_AdminState.UNLOCKED.getId());
        login.setPersonId(person.getId());
        login.setInfo("Сотрудник по контракту - PORTAL-1538");
        login.setRoles(userRoleDAO.getDefaultEmployeeRoles());

        // для тестирования
        login.setUpass("e4b48fd541b3dcb99cababc87c2ee88f");
        login.setAuthType(En_AuthType.LOCAL);
        login.setIpMaskAllow(Arrays.asList("172.18.240.0/22", // vpn
                                           "172.18.249.0/24",
                                           "172.16.202.0/24", // НЕОС
                                           "172.16.201.0/24", // СИГУРД
                                           "192.168.0.0/17",  // local
                                           "127.0.0.1",       // host
                                           "0.0.0.0"));

        userLoginDAO.persist(login);
        log.info("addContractEmployee ended");
    }

    private void normalizePhoneNumbers() {
        log.info("normalizePhoneNumbers started");

        applicationContext.getBean(JdbcObjectMapperRegistrator.class).registerMapper(ContactItemPerson.class);
        registerBeanDefinition(applicationContext, ContactItemPerson.ContactItemPersonDAO.class);
        ContactItemPerson.ContactItemPersonDAO contactItemPersonDAO = applicationContext.getBean(ContactItemPerson.ContactItemPersonDAO.class);

        List<Long> homeCompanyIds = stream(companyGroupHomeDAO.getAll()).map(CompanyHomeGroupItem::getCompanyId).collect(toList());
        List<Long> contactItemsIds = contactItemDAO.listColumnValue("id", Long.class,
                " value is not null AND TRIM(value) <> '' " +
                        "  AND item_type IN " + HelperFunc.makeInArg(Arrays.asList(En_ContactItemType.MOBILE_PHONE.getId(), En_ContactItemType.GENERAL_PHONE.getId()), false) +
                        "  AND id IN (SELECT cip.contact_item_id FROM contact_item_person cip join person p on p.id = cip.person_id " +
                        "             WHERE p.company_id IN " + HelperFunc.makeInArg(homeCompanyIds, false) + ")"
        );

        List<Pair<ContactItem, Long>> contactItemAndIdForPersonIdList = new ArrayList<>();
        int chunk = 1000;
        for(int i = 0; i < contactItemsIds.size() / chunk + 1; i++) {
            int lowerBorder = i * chunk;
            int upperBorder = (i+1) * chunk;
            contactItemAndIdForPersonIdList.addAll(
                    normalizePhones(contactItemDAO.getListByKeys(
                            contactItemsIds.subList(lowerBorder, Math.min(upperBorder, contactItemsIds.size()))                            )
                    )
            );
            persistContactItems(contactItemPersonDAO, contactItemAndIdForPersonIdList, false);
        }
        if (!contactItemAndIdForPersonIdList.isEmpty()) {
            persistContactItems(contactItemPersonDAO, contactItemAndIdForPersonIdList, true);
        }

        removeBeanDefinition(applicationContext, ContactItemPerson.ContactItemPersonDAO.class);
        removeBeanDefinition(applicationContext, ContactItemPerson.class);

        log.info("normalizePhoneNumbers ended");
    }

    private List<Pair<ContactItem, Long>> normalizePhones(List<ContactItem> phones) {
        List<Pair<ContactItem, Long>> result = new ArrayList<>();
        phones.forEach(phone -> {
            String phoneNumber = replaceExtOfPhoneNumber(
                    phone.value().trim()
            );
            if (!isSimpleCheckValid(phoneNumber)) {
                log.error("normalizePhone: phone is contains non valid characters (@Z-z), phoneId={}, value={}", phone.id(), phone.value());
                return;
            }
            final List<String> split = split(phoneNumber);
            if (split != null) {    // несколько номеров
                boolean isMainPhoneModify = false;
                for (String s : split) {
                    final String otherPhone = PhoneUtils.normalizePhoneNumber(s);
                    if (StringUtils.isEmpty(otherPhone)) {
                        continue;
                    }
                    if (!isMainPhoneModify) {
                        phone.modify(otherPhone);
                        isMainPhoneModify = true;
                        result.add(new Pair<>(phone, null));
                        continue;
                    }

                    ContactItem contactItem = new ContactItem(phone.type(), phone.accessType());
                    contactItem.modify(otherPhone);

                    result.add(new Pair<>(contactItem, phone.id()));
                }
            } else {
                phone.modify(PhoneUtils.normalizePhoneNumber(phoneNumber));
                result.add(new Pair<>(phone, null));
            }
        });
        return result;
    }

    private String replaceExtOfPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("д\\.|доб|доп|добавочный|вн|внут|внутр|Внутр|внутренний|корп|местн.тел|ext|Ext|al|\\*", "#");
    }

    private boolean isSimpleCheckValid(String phoneNumber) {
        return !phoneNumber.matches(".*[@Z-z].*");
    }

    private Pattern splitPattern = Pattern.compile("\\+|,| или | и |;");
    private List<String> split(String phoneNumber) {
        if (StringUtils.isNotEmpty(phoneNumber) && phoneNumber.length() > 1) {
            Matcher matcher = splitPattern.matcher(phoneNumber.substring(1));       // костыль на первый "+" в номере
            if (matcher.find()) {
                List<String> result = new ArrayList<>();
                int mark = 0;
                do {
                    result.add(phoneNumber.substring(
                            mark,
                            isIncludeSplitMark(matcher.group()) ? matcher.start() + 1 : matcher.end() + 1)   // костыль наносит ответный удар
                    );
                    mark = matcher.end();
                } while (matcher.find());
                result.add(phoneNumber.substring(mark));
                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    private boolean isIncludeSplitMark(String group) {
        return "+".equals(group);
    }

    private Long getContactItemPersonId(ContactItemPerson.ContactItemPersonDAO dao, Long contactItemId) {
        return dao.getByCondition(
                    condition().and("contact_item_id").equal(contactItemId).getSqlCondition(),
                    contactItemId)
                .personId;
    }

    private void persistContactItems(ContactItemPerson.ContactItemPersonDAO dao,
                                     List<Pair<ContactItem, Long>> contactItemAndContactItemIdForPersonIdList, boolean force) {
        if (force || contactItemAndContactItemIdForPersonIdList.size() >= 1000) {
            List<ContactItem> contactItems = contactItemAndContactItemIdForPersonIdList.stream().map(Pair::getA).collect(toList());
            contactItemDAO.saveOrUpdateBatch(contactItems);

            List<ContactItemPerson> contactItemPerson = contactItemAndContactItemIdForPersonIdList.stream()
                    .filter(pair -> pair.getB() != null)
                    .map(pair -> new ContactItemPerson(getContactItemPersonId(dao, pair.getB()), pair.getA().id()))
                    .collect(toList());
            dao.persistBatch(contactItemPerson);

            contactItemAndContactItemIdForPersonIdList.clear();
        }
    }

    @JdbcEntity(table = "contact_item_person")
    public static class ContactItemPerson{
        @JdbcColumn(name = "person_id")
        public Long personId;
        @JdbcColumn(name = "contact_item_id")
        public Long contactItemId;

        public ContactItemPerson() {
        }

        public ContactItemPerson(Long personId, Long contactItemId) {
            this.personId = personId;
            this.contactItemId = contactItemId;
        }

        public static class ContactItemPersonDAO
                extends JdbcBaseDAO<ContactItemPerson, ContactItemPerson>
                implements JdbcDAO<ContactItemPerson, ContactItemPerson> {}
    }

    private static class ContactInfoPersonMigration {

        public static void migrate(ApplicationContext applicationContext) {
            log.info("contactInfoPersonMigration(): start");

            applicationContext.getBean(JdbcObjectMapperRegistrator.class).registerMapper(ContactItemPerson.class);
            registerBeanDefinition(applicationContext, ContactItemPersonDAO.class);
            if (applicationContext.getBean(ContactItemPersonDAO.class).getObjectsCount() > 0) {
                log.info("contactInfoPersonMigration(): stop | 'contact_item_person' table not empty");
                return;
            }

            applicationContext.getBean(JdbcObjectMapperRegistrator.class).registerMapper(PersonOld.class);
            registerBeanDefinition(applicationContext, PersonOldDAO.class);
            PersonOldDAO personDAOOld = applicationContext.getBean(PersonOldDAO.class);
            ContactItemDAO contactItemDAO = applicationContext.getBean(ContactItemDAO.class);
            JdbcManyRelationsHelper jdbcManyRelationsHelper = applicationContext.getBean(JdbcManyRelationsHelper.class);

            List<En_ContactItemType> supportedItemTypes = asList(
                    En_ContactItemType.EMAIL,
                    En_ContactItemType.MOBILE_PHONE,
                    En_ContactItemType.GENERAL_PHONE
            );

            final int limit = 100;
            int offset = 0;
            while (true) {
                log.info("contactInfoPersonMigration(): step | {}-{}", offset, offset + limit);
                SearchResult<PersonOld> result = personDAOOld.getAll(offset, limit);
                for (PersonOld personOld : result.getResults()) {
                    List<ContactItem> contactItems = stream(personOld.contactInfo != null ? personOld.contactInfo.getItems() : new ArrayList<>())
                            .filter(not(ContactItem::isEmptyValue))
                            .filter(contactItem -> supportedItemTypes.contains(contactItem.type()))
                            .collect(toList());
                    if (isEmpty(contactItems)) {
                        continue;
                    }
                    contactItemDAO.persistBatch(contactItems);
                    Person person = new Person();
                    person.setId(personOld.id);
                    person.setContactInfo(new ContactInfo(contactItems));
                    jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);
                }
                if (result.getResults().size() < limit) {
                    break;
                } else {
                    offset += limit;
                }
            }

            removeBeanDefinition(applicationContext, ContactItemPersonDAO.class);
            removeBeanDefinition(applicationContext, ContactItemPerson.class);
            removeBeanDefinition(applicationContext, PersonOldDAO.class);
            removeBeanDefinition(applicationContext, PersonOld.class);

            log.info("contactInfoPersonMigration(): end");
        }

        @JdbcEntity(table = "person")
        public static class PersonOld {
            @JdbcId(name = "id")
            public Long id;
            @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
            public ContactInfo contactInfo;
        }

        @JdbcEntity(table = "contact_item_person")
        public static class ContactItemPerson {}

        private static class PersonOldDAO extends PortalBaseJdbcDAO<PersonOld> {}
        private static class ContactItemPersonDAO extends PortalBaseJdbcDAO<ContactItemPerson> {}
    }

    private static class ContactInfoCompanyMigration {

        public static void migrate(ApplicationContext applicationContext) {
            log.info("contactInfoCompanyMigration(): start");

            applicationContext.getBean(JdbcObjectMapperRegistrator.class).registerMapper(ContactItemCompany.class);
            registerBeanDefinition(applicationContext, ContactItemCompanyDAO.class);
            if (applicationContext.getBean(ContactItemCompanyDAO.class).getObjectsCount() > 0) {
                log.info("contactInfoCompanyMigration(): stop | 'contact_item_company' table not empty");
                return;
            }

            applicationContext.getBean(JdbcObjectMapperRegistrator.class).registerMapper(CompanyOld.class);
            registerBeanDefinition(applicationContext, CompanyOldDAO.class);
            CompanyOldDAO companyDAOOld = applicationContext.getBean(CompanyOldDAO.class);
            ContactItemDAO contactItemDAO = applicationContext.getBean(ContactItemDAO.class);
            JdbcManyRelationsHelper jdbcManyRelationsHelper = applicationContext.getBean(JdbcManyRelationsHelper.class);

            List<En_ContactItemType> supportedItemTypes = asList(
                    En_ContactItemType.EMAIL,
                    En_ContactItemType.ADDRESS,
                    En_ContactItemType.ADDRESS_LEGAL,
                    En_ContactItemType.FAX,
                    En_ContactItemType.MOBILE_PHONE,
                    En_ContactItemType.GENERAL_PHONE,
                    En_ContactItemType.WEB_SITE
            );

            final int limit = 100;
            int offset = 0;
            while (true) {
                log.info("contactInfoCompanyMigration(): step | {}-{}", offset, offset + limit);
                SearchResult<CompanyOld> result = companyDAOOld.getAll(offset, limit);
                for (CompanyOld companyOld : result.getResults()) {
                    List<ContactItem> contactItems = stream(companyOld.contactInfo != null ? companyOld.contactInfo.getItems() : new ArrayList<>())
                            .filter(not(ContactItem::isEmptyValue))
                            .filter(contactItem -> supportedItemTypes.contains(contactItem.type()))
                            .collect(toList());
                    if (isEmpty(contactItems)) {
                        continue;
                    }
                    contactItemDAO.persistBatch(contactItems);
                    Company company = new Company();
                    company.setId(companyOld.id);
                    company.setContactInfo(new ContactInfo(contactItems));
                    jdbcManyRelationsHelper.persist(company, Company.Fields.CONTACT_ITEMS);
                }
                if (result.getResults().size() < limit) {
                    break;
                } else {
                    offset += limit;
                }
            }

            removeBeanDefinition(applicationContext, ContactItemCompanyDAO.class);
            removeBeanDefinition(applicationContext, ContactItemCompany.class);
            removeBeanDefinition(applicationContext, CompanyOldDAO.class);
            removeBeanDefinition(applicationContext, CompanyOld.class);

            log.info("contactInfoCompanyMigration(): end");
        }

        @JdbcEntity(table = "company")
        public static class CompanyOld {
            @JdbcId(name = "id")
            public Long id;
            @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
            public ContactInfo contactInfo;
        }

        @JdbcEntity(table = "contact_item_company")
        public static class ContactItemCompany {}

        private static class CompanyOldDAO extends PortalBaseJdbcDAO<CompanyOld> {}
        private static class ContactItemCompanyDAO extends PortalBaseJdbcDAO<ContactItemCompany> {}
    }

    private static <T> void registerBeanDefinition(ApplicationContext context, Class<T> clazz) {
        String beanName = clazz.getName();
        String beanAlias = clazz.getSimpleName();
        GenericBeanDefinition gbd = new GenericBeanDefinition();
        gbd.setBeanClass(clazz);
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        if (registry.containsBeanDefinition(beanName)) {
            registry.removeBeanDefinition(beanName);
        }
        registry.registerBeanDefinition(beanName, gbd);
        registry.registerAlias(beanName, beanAlias);
    }

    private static <T> void removeBeanDefinition(ApplicationContext context, Class<T> clazz) {
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        if (registry.containsBeanDefinition(clazz.getName())) {
            registry.removeBeanDefinition(clazz.getName());
        }
    }

    @Inject
    UserRoleDAO userRoleDAO;

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
    PersonShortViewDAO personShortViewDAO;
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
    BootstrapAppDAO bootstrapAppDAO;
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
    EmployeeRegistrationHistoryDAO employeeRegistrationHistoryDAO;
    @Autowired
    PlanDAO planDAO;
    @Autowired
    PersonAbsenceDAO personAbsenceDAO;
    @Autowired
    UserDashboardDAO userDashboardDAO;
    @Autowired
    UserLoginDAO userLoginDAO;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ContactItemDAO contactItemDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseStateDAO caseStateDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

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
