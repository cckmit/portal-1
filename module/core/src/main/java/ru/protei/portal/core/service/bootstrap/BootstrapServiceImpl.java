package ru.protei.portal.core.service.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Locale.forLanguageTag;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.protei.portal.core.model.dict.En_Privilege.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.util.CrmConstants.LocaleTags.EN;
import static ru.protei.portal.core.model.util.CrmConstants.LocaleTags.RU;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class BootstrapServiceImpl implements BootstrapService {

    private static Logger log = LoggerFactory.getLogger( BootstrapServiceImpl.class );

    @Transactional
    @Override
    public void bootstrapApplication() {
        log.info( "bootstrapApplication(): BootstrapService begin."  );

        /**
         * begin Спринт 68 */
        if (!bootstrapAppDAO.isActionExists("updateContactItemsAccessType")) {
            this.updateContactItemsAccessType();
            bootstrapAppDAO.createAction("updateContactItemsAccessType");
        }
        /**
         *  end Спринт 68 */

        /**
         * begin Спринт 72 */
        if (!bootstrapAppDAO.isActionExists("changePersonToSingleCompany")) {
            this.changePersonToSingleCompany();
            bootstrapAppDAO.createAction("changePersonToSingleCompany");
        }

        if (!bootstrapAppDAO.isActionExists("changePersonToSingleCompanyForNotActiveWorkerEntry")) {
            this.changePersonToSingleCompanyForNotActiveWorkerEntry();
            bootstrapAppDAO.createAction("changePersonToSingleCompanyForNotActiveWorkerEntry");
        }

        if (!bootstrapAppDAO.isActionExists("changeIssueInitiatorCompany")) {
            this.changeIssueInitiatorCompany();
            bootstrapAppDAO.createAction("changeIssueInitiatorCompany");
        }

        /**
         *  end Спринт 72 */

        /**
         * begin Спринт 73 */
        bootstrapAppDAO.removeByCondition("name in "+ HelperFunc.makeInArg(
                Arrays.asList("addDeliveryCaseType", "addDeliveryCaseStates", "addDeliveryCaseStateMatrix"), true)
        );
        /**
         *  end Спринт */

        /**
         * begin Спринт 80 */
        if (!bootstrapAppDAO.isActionExists("youtrackWorkDictionaries")) {
            this.youtrackWorkDictionaries();
            bootstrapAppDAO.createAction("youtrackWorkDictionaries");
        }
        /**
         *  end Спринт 80 */

        /**
         * begin Спринт 81 */
        if (!bootstrapAppDAO.isActionExists("importCardTypes")) {
            this.importCardTypes();
            bootstrapAppDAO.createAction("importCardTypes");
        }
        /**
         *  end Спринт */

        /**
         * begin Спринт 82 */
        if (!bootstrapAppDAO.isActionExists("addCardAndCardBatchRolesAndPrivileges")) {
            this.addCardRolesAndPrivileges();
            bootstrapAppDAO.createAction("addCardAndCardBatchRolesAndPrivileges");
        }
        /**
         *  end Спринт */

        /**
         * begin Спринт 83 */
        if (!bootstrapAppDAO.isActionExists("setProbationPeriodEndDateForProbationNotExpired")) {
            this.setProbationPeriodEndDateForProbationNotExpired();
            bootstrapAppDAO.createAction("setProbationPeriodEndDateForProbationNotExpired");
        }
        /**
         *  end Спринт */

        /**
         * begin Спринт 85 */
        if (!bootstrapAppDAO.isActionExists("addIssueGroupManagerId")) {
            this.addIssueGroupManagerId();
            bootstrapAppDAO.createAction("addIssueGroupManagerId");
        }

        if (!bootstrapAppDAO.isActionExists("setSubscriptionTypeToContactItems")) {
            this.setSubscriptionTypeToContactItems();
            bootstrapAppDAO.createAction("setSubscriptionTypeToContactItems");
        }
        /**
         *  end Спринт */

        log.info( "bootstrapApplication(): BootstrapService complete."  );
    }

    private void addIssueGroupManagerId() {
        log.debug("addIssueGroupManagerId(): start");

        String bundle = "dashboardTableFilterCreationNewIssues";
        String ruFilterName = lang.getFor(forLanguageTag(RU)).get(bundle);
        String enFilterName = lang.getFor(forLanguageTag(EN)).get(bundle);
        List<CaseFilter> filters = caseFilterDAO.getListByCondition("params like ? and type = ? and (name = ? or name = ?)",
                                                                    "%managerIds%", En_CaseFilterType.CASE_OBJECTS.name(),
                                                                    ruFilterName, enFilterName);
        for (CaseFilter filter: filters) {
            try {
                CaseQuery query = objectMapper.readValue(filter.getParams(), CaseQuery.class);
                List<Long> managerIds = query.getManagerIds();
                if (managerIds.contains(CrmConstants.Employee.UNDEFINED)) {
                    managerIds.add(CrmConstants.Employee.GROUP_MANAGER);
                    query.setManagerIds(managerIds);
                    filter.setParams(objectMapper.writeValueAsString(query));
                    caseFilterDAO.partialMerge(filter, "params");
                    log.info("addIssueGroupManagerId(): filter with id={} updated", filter.getId());
                }
            } catch (IOException e) {
                log.warn("addIssueGroupManagerId(): cannot update filter with id={}", filter.getId());
            }
        }

        log.debug("addIssueGroupManagerId(): finish");
    }

    private void setSubscriptionTypeToContactItems() {
        log.debug("setSubscriptionTypeToContactItems(): start");

        List<ContactItem> contactItems = contactItemDAO.getListByCondition(
                "item_type = ? AND access_type = ?", En_ContactItemType.EMAIL.getId(), En_ContactDataAccess.INTERNAL.getId());
        contactItems.forEach(item -> item.modify(En_ContactEmailSubscriptionType.SUBSCRIPTION_TO_END_OF_PROBATION));
        contactItemDAO.saveOrUpdateBatch(contactItems);

        List<ContactItem> contactItemsWithoutSubscription = contactItemDAO.getListByCondition("subscription_type is null");
        contactItemsWithoutSubscription.forEach(item -> item.modify(En_ContactEmailSubscriptionType.WITHOUT_SUBSCRIPTION));
        contactItemDAO.saveOrUpdateBatch(contactItemsWithoutSubscription);

        log.debug("setSubscriptionTypeToContactItems(): finish");
    }

    private void setProbationPeriodEndDateForProbationNotExpired() {
        log.debug("setProbationPeriodEndDateForProbationNotExpired(): start");
        List<EmployeeRegistration> probationNotExpired = employeeRegistrationDAO.getListByCondition(
                "CURDATE() <= DATE_ADD(employment_date, INTERVAL probation_period MONTH)");

        Calendar calendar = new GregorianCalendar();
        for (EmployeeRegistration employeeRegistration : probationNotExpired) {
            calendar.setTime(employeeRegistration.getEmploymentDate());
            calendar.add(Calendar.MONTH, employeeRegistration.getProbationPeriodMonth());
            employeeRegistration.setProbationPeriodEndDate(calendar.getTime());

            employeeRegistrationDAO.partialMerge(employeeRegistration, "probation_period_end_date");
            log.info("setProbationPeriodEndDateForProbationNotExpired(): " +
                    "employee registration with id={} updated", employeeRegistration.getId());
        }

        log.debug("setProbationPeriodEndDateForProbationNotExpired(): finish");
    }

    private void addCardRolesAndPrivileges() {
        log.debug("addCardRolesAndPrivileges(): start");
        UserRole mainAdmin = userRoleDAO.partialGetByCondition("role_code = ?",
                Collections.singletonList("Главный администратор"), "id", "privileges");
        if (mainAdmin != null) {
            Set<En_Privilege> mainAdmPrivs = mainAdmin.getPrivileges();
            mainAdmPrivs.add(CARD_VIEW);
            mainAdmPrivs.add(CARD_CREATE);
            mainAdmPrivs.add(CARD_EDIT);
            mainAdmPrivs.add(CARD_REMOVE);
            mainAdmPrivs.add(CARD_BATCH_VIEW);
            mainAdmPrivs.add(CARD_BATCH_CREATE);
            mainAdmPrivs.add(CARD_BATCH_EDIT);
            mainAdmPrivs.add(CARD_BATCH_REMOVE);
            userRoleDAO.partialMerge(mainAdmin, "privileges");
        }

        UserRole mainAdminReadOnly = userRoleDAO.partialGetByCondition("role_code = ?",
                Collections.singletonList("Главный администратор (Read only)"), "id", "privileges");
        if (mainAdminReadOnly != null) {
            Set<En_Privilege> mainAdmReadOnlyPrivs = mainAdminReadOnly.getPrivileges();
            mainAdmReadOnlyPrivs.add(PLAN_VIEW);
            mainAdmReadOnlyPrivs.add(ABSENCE_VIEW);
            mainAdmReadOnlyPrivs.add(DUTY_LOG_VIEW);
            mainAdmReadOnlyPrivs.add(EDUCATION_VIEW);
            mainAdmReadOnlyPrivs.add(DELIVERY_VIEW);
            mainAdmReadOnlyPrivs.add(CARD_VIEW);
            mainAdmReadOnlyPrivs.add(CARD_BATCH_VIEW);
            userRoleDAO.partialMerge(mainAdminReadOnly, "privileges");
        }

        UserRole cardRole = new UserRole();
        cardRole.setCode("Управление платами");
        cardRole.setInfo("Управление платами");
        cardRole.setPrivileges(new HashSet<>(Arrays.asList(CARD_VIEW, CARD_CREATE, CARD_EDIT, CARD_REMOVE)));
        cardRole.setScope(En_Scope.SYSTEM);
        userRoleDAO.saveOrUpdate(cardRole);

        UserRole cardBatchRole = new UserRole();
        cardBatchRole.setCode("Управление партиями плат");
        cardBatchRole.setInfo("Управление партиями плат");
        cardBatchRole.setPrivileges(new HashSet<>(Arrays.asList(CARD_BATCH_VIEW, CARD_BATCH_CREATE, CARD_BATCH_EDIT, CARD_BATCH_REMOVE)));
        cardBatchRole.setScope(En_Scope.SYSTEM);
        userRoleDAO.saveOrUpdate(cardBatchRole);
        log.debug("addCardRolesAndPrivileges(): finish");
    }

    private void importCardTypes() {
        log.debug("importCardTypes(): start");
        List<CardType> cardTypes = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://192.168.110.68:3306/resv3", "portal", "BvF4B2!p");
                 PreparedStatement statement = connection.prepareStatement("select * from tm_cardtype;")) {

                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    cardTypes.add(new CardType(
                            rs.getString("strValue"),
                            "0" + rs.getString("strDisplay"),
                            rs.getBoolean("lContainer"),
                            rs.getBoolean("lDisplay"))
                    );
                }
                log.info("importCardTypes(): get {} card types from resv3", cardTypes.size());
            } catch (Exception e) {
                log.error("importCardTypes(): error while importing card types from resv3", e);
            }
        } catch (ClassNotFoundException e) {
            log.error("importCardTypes(): error while driver registration", e);
        }
        cardTypeDAO.persistBatch(cardTypes);
        log.debug("importCardTypes(): finish");
    }

    private void changeIssueInitiatorCompany() {
        log.debug("changeIssueInitiatorCompany(): start");
        List<Company> companies = companyDAO.getSingleHomeCompanies();
        for (Company company : companies) {

            if (CrmConstants.Company.HOME_COMPANY_ID == company.getId()) {
                continue;
            }

            WorkerEntryQuery workerEntryQuery = new WorkerEntryQuery(company.getId(), 1);
            List<WorkerEntry> workerEntryShortViewList = workerEntryDAO.listByQuery(workerEntryQuery);
            List<Long> personIds = workerEntryShortViewList.stream()
                    .map(WorkerEntry::getPersonId)
                    .distinct()
                    .collect(Collectors.toList());

            changeIssueInitiatorCompany(personIds, company.getId());
        }
        log.debug("changeIssueInitiatorCompany(): finish");
    }

    private void changePersonToSingleCompanyForNotActiveWorkerEntry() {

        log.debug("changePersonToSingleCompanyForNotActiveWorkerEntry(): start");

        List<Long> notActiveWorkerEntryPersonIds =
                workerEntryDAO.partialGetListByCondition("active = 0 and personId in (SELECT personId from worker_entry GROUP BY personId HAVING COUNT(*) < 2)",
                        null, "personId").stream().map(workerEntry -> workerEntry.getPersonId()).collect(toList());

        List<Company> companies = companyDAO.getSingleHomeCompanies();
        for (Company company : companies) {

            if (CrmConstants.Company.HOME_COMPANY_ID == company.getId()) {
                continue;
            }

            List<WorkerEntry> workerEntryShortViewList = workerEntryDAO.partialGetByPersonIds(notActiveWorkerEntryPersonIds, company.getId());
            List<Long> personIds = workerEntryShortViewList.stream()
                    .map(WorkerEntry::getPersonId)
                    .distinct()
                    .collect(Collectors.toList());

            changePersonToSingleCompany(personIds, company.getId());
        }

        log.debug("changePersonToSingleCompanyForNotActiveWorkerEntry(): finish");
    }

    private void changePersonToSingleCompany() {

        log.debug("changePersonToSingleCompany(): start");

        List<Company> companies = companyDAO.getSingleHomeCompanies();
        for (Company company : companies) {

            if (CrmConstants.Company.HOME_COMPANY_ID == company.getId()) {
                continue;
            }

            WorkerEntryQuery workerEntryQuery = new WorkerEntryQuery(company.getId(), 1);
            List<WorkerEntry> workerEntryShortViewList = workerEntryDAO.listByQuery(workerEntryQuery);
            List<Long> personIds = workerEntryShortViewList.stream()
                    .map(WorkerEntry::getPersonId)
                    .distinct()
                    .collect(Collectors.toList());

            changePersonToSingleCompany(personIds, company.getId());
        }

        log.debug("changePersonToSingleCompany(): finish");
    }

    private void changePersonToSingleCompany(List<Long> personIds, Long companyId) {
        if (CollectionUtils.isEmpty(personIds)) {
            return;
        }

        // Update person company
        personIds.forEach(personId -> {
            personDAO.partialMerge(new Person(personId, companyId), "company_id");
            log.info("changePersonToSingleCompany(): person with id={} updated", personId);
        });

        // Update manager company of issue
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.CRM_SUPPORT);
        caseQuery.setManagerIds(personIds);
        caseShortViewDAO.listByQuery(caseQuery).forEach(caseShortView -> {
            caseShortView.setManagerCompanyId(companyId);
            caseShortViewDAO.partialMerge(caseShortView, "manager_company_id");
            log.info("changePersonToSingleCompany(): issue with id={} updated", caseShortView.getId());
        });

        // Update initiator company of issue
        changeIssueInitiatorCompany(personIds, companyId);

        // Update manager company of filter
        List<CaseFilter> filters = caseFilterDAO.getListByCondition("params like ? and type = ?", "%managerIds%", En_CaseFilterType.CASE_OBJECTS.name());
        for (CaseFilter filter : filters) {
            try {
                CaseQuery query = objectMapper.readValue(filter.getParams(), CaseQuery.class);
                List<Long> managerCompanyIds = emptyIfNull(query.getManagerCompanyIds());
                if (emptyIfNull(query.getManagerIds()).stream().anyMatch(personIds::contains) &&
                        !managerCompanyIds.contains(companyId)) {
                    managerCompanyIds.add(companyId);
                    query.setManagerCompanyIds(managerCompanyIds);
                    filter.setParams(objectMapper.writeValueAsString(query));
                    caseFilterDAO.partialMerge(filter, "params");
                    log.info("changePersonToSingleCompany(): filter with id={} updated", filter.getId());
                }
            } catch (IOException e) {
                log.warn("changePersonToSingleCompany(): cannot update filter with id={}", filter.getId());
                continue;
            }
        }

        // Update manager company of report
        List<Report> reports = reportDAO.getListByCondition("case_query like ? and type=? and is_removed=?", "%managerIds%", En_ReportType.CASE_OBJECTS.name(), false);
        for (Report report : reports) {
            try {
                CaseQuery query = objectMapper.readValue(report.getQuery(), CaseQuery.class);
                List<Long> managerCompanyIds = emptyIfNull(query.getManagerCompanyIds());
                if (emptyIfNull(query.getManagerIds()).stream().anyMatch(personIds::contains) &&
                        !managerCompanyIds.contains(companyId)) {
                    managerCompanyIds.add(companyId);
                    query.setManagerCompanyIds(managerCompanyIds);
                    report.setQuery(objectMapper.writeValueAsString(query));
                    reportDAO.partialMerge(report, "case_query");
                    log.info("changePersonToSingleCompany(): report with id={} updated", report.getId());
                }
            } catch (IOException e) {
                log.warn("changePersonToSingleCompany(): cannot update report with id={}", report.getId());
                continue;
            }
        }
    }

    private void changeIssueInitiatorCompany(List<Long> personIds, Long companyId) {
        // Update initiator company of issue
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.CRM_SUPPORT);
        caseQuery.setInitiatorIds(personIds);
        caseShortViewDAO.listByQuery(caseQuery).forEach(caseShortView -> {
            caseShortView.setInitiatorCompanyId(companyId);
            caseShortViewDAO.partialMerge(caseShortView, "initiator_company");
            log.info("changeIssueInitiatorCompany(): issue with id={} updated", caseShortView.getId());
        });
    }

    private void updateContactItemsAccessType() {
        List<ContactItem> contactItems = contactItemDAO.getListByCondition("access_type is null");
        contactItems.forEach(item -> item.modify(En_ContactDataAccess.PUBLIC));
        contactItemDAO.saveOrUpdateBatch(contactItems);
    }

    private void youtrackWorkDictionaries() {
        Map<String, List<String>> niokrs = new HashMap<>();
        niokrs.put("Программа автоматического детектирования и распознавания автомобильных регистрационных знаков", Arrays.asList("EREQUESTS"));
        niokrs.put("Программное обеспечение \"Детектор человека\"", Arrays.asList("VAD", "VP"));
        niokrs.put("Программное обеспечение \"Детектор транспорта\"", Arrays.asList("VAD", "VP"));
        niokrs.put("Многофункциональный комплекс связи «Гелиос-5»", Arrays.asList("HWGELIOS", "HWD", "HWS", "HWT", "QAH", "HW_EQ"));
        niokrs.put("Программа сервера приложений TAS AS для реализации услуги PUSH-TO-TALK для критических применений (MCPTT AS)", Arrays.asList("MCPTT", "TAS"));
        niokrs.put("программное обеспечение Системы PROTEI Signaling Firewall.", Arrays.asList("Mobile_SS7FW"));
        niokrs.put("Разработка технологий производства и создания оборудования беспроводного широкополосного доступа на основе спецификаций 3GPP LTE " +
                "/ Программное обеспечение «Ядро сети + МСРТТ» (для Минпромторг)", Arrays.asList("MCPTT", "TAS"));
        niokrs.put("Компас", Arrays.asList("eth_switch"));
        niokrs.put("ПО для построения корпоративных услуг Unified Communication", Arrays.asList("UC"));
        niokrs.put("ПО «AMF, AUSF, UDM/UDR»", Arrays.asList("Mobile_AUSF", "Mobile_UDM", "AMF"));
        niokrs.put("ПО «SMF/UPF, NRF»", Arrays.asList("MobileCorePacketTester"));
        niokrs.put("ПО «PCF»", Arrays.asList("PCRF"));
        niokrs.put("Программное обеспечение \"Платформа Видеоаналитики\"", Arrays.asList("VAD", "VP"));
        niokrs.put("ПО Protei TAS", Arrays.asList("MKD", "TAS", "ATE"));
        niokrs.put("ПО Protei SCC-AS", Arrays.asList("SCC-AS", "TAS"));

        Map<String, List<String>> nmas = new HashMap<>();
        nmas.put("СПО КТСО МУССОН", Arrays.asList("MC", "PHMONSOON", "HWMONSOON", "HWR", "EQA"));
        nmas.put("СПО \"Кругозор\"", Arrays.asList("VAD"));
        nmas.put("Специальное ПО Безопасный город", Arrays.asList("EAS", "AT", "VAD", "VP", "ER", "WP", "CC", "SB", "SN", "PNR", "CPE", "Mobile_SMSC", "DAD_DDF",
                "DAD_DAD", "DAD_EXP", "EWC", "POST", "sppr", "SC_int", "DAD_PKN", "DA", "GB", "EQA"));
        nmas.put("Система перехвата TDM трафика. LIME1", Arrays.asList("LI", "EMSC", "PHYSICAL", "ITG", "SNGI"));
        nmas.put("Система обработки информации о чрезвычайных ситуациях", Arrays.asList("ER", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "PNR", "SNGI", "EQA"));
        nmas.put("Система мониторинга сигнальных каналов", Arrays.asList("Mobile_SigMonitor"));
        nmas.put("Система законного перехвата IP трафика. LIS", Arrays.asList("SORM", "LIS"));
        nmas.put("Программный модуль GTP-Probe", Arrays.asList("PLP"));
        nmas.put("Программное обеспечение Компонентов ядра сети IMS - PROTEI IMS Core", Arrays.asList("PCSCF", "ICSCF", "SCSCF", "SCCAS", "CLI", "IMST"));
        nmas.put("Программное обеспечение PROTEI EPC", Arrays.asList("MME", "SGW", "PDN", "GGSN", "Mobile_HLR", "PCRF", "CPE"));
        nmas.put("Программная подсистема обработки пакетного трафика", Arrays.asList("DPI", "DS", "DPI_TO"));
        nmas.put("Программа Центра обслуживания вызовов \"ПРОТЕЙ\"", Arrays.asList("CC", "SB", "SN", "TLG", "EACD", "RFW", "CPE"));
        nmas.put("Программа Системы глубокого анализа и применения политик для управления пакетным трафиком PROTEI_DPI", Arrays.asList("DPI", "DS", "DPI_TO"));
        nmas.put("Программа преобразований речевой и сигнальной информации Шлюза PRIN", Arrays.asList("HWR"));
        nmas.put("Программа комплекса ПРОТЕЙ-ВКС", Arrays.asList("MVP", "VCST", "VCSM", "VCSS", "CRS", "DBRL", "GB"));
        nmas.put("Программа для ЭВМ \"Система управления подключенными устройствами PROTEI M2M/C2M\"", Arrays.asList("GEO", "OB"));
        nmas.put("Программа для ЭВМ \"Домашний регистр местоположения/регистр абонентских данных PROTEI HLR/HSS (Версия 2)", Arrays.asList("Mobile_HLR"));
        nmas.put("Программа для ЭВМ \"Автоматизированная система расчетов PROTEI OCS\"", Arrays.asList("OB", "billing-qa"));
        nmas.put("ПО Узла PROTEI SCP", Arrays.asList("Mobile_CAPL"));
        nmas.put("ПО Системы PROTEI Signaling Firewall", Arrays.asList("Mobile_SS7FW"));
        nmas.put("ПО Система-112", Arrays.asList("ER", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "SPLSIP", "GB", "EQA"));
        nmas.put("ПО Комплекса управления роумингом", Arrays.asList("RG", "GLR", "Mobile_MI", "WSMS"));
        nmas.put("ПО Комплекса управления дополнительными услугами", Arrays.asList("xVLR", "Mobile_Bulk", "SNGI"));
        nmas.put("ПО комплекса предоставления услуг Messaging (SMSC/USSDC/SMS Firewall/IP-SM-GW)", Arrays.asList("Mobile_SMSC", "Mobile_SMSFW", "Mobile_IPSMGW", "Mobile_SCL", "CBC", "SGW"));
        nmas.put("ПО комплекса автоматизации обработки вызовов экстренных оперативных служб по единому номеру 112", Arrays.asList("ER", "WP", "CC", "SB", "SN", "TLG", "TM", "EACD", "RFW", "CPE", "SPLSIP"));
        nmas.put("ПО комплекса предоставления услуг роуминга с использованием технологии Multi-IMSI", Arrays.asList("Mobile_MI"));
        nmas.put("ПО \"ПРОТЕЙ-GMSC\"", Arrays.asList("GMSC", "SSW4"));
        nmas.put("ПО \"Мониторинг потенциально опасных объектов\"", Arrays.asList("EAS"));
        nmas.put("ПК \"Протей-imSwitch\"", Arrays.asList("MKD", "MKD-Test"));
        nmas.put("ПК \"МАК\"", Arrays.asList("MAK", "ITG", "ITG.UI"));
        nmas.put("ПК \"ИП \"Протей\"", Arrays.asList("SB", "SN", "VO", "CPE", "PNR", "RP", "IVR", "PRBT", "pstorage", "TSIM", "VOT", "SDP", "TK", "SPLSIP"));
        nmas.put("ПК \"Единый центр оперативного реагирования\"", Arrays.asList("ER"));
        nmas.put("Пакетный шлюз PROTEI GGSN/PDN-GW", Arrays.asList("GGSN", "Mobile_HLR", "SGW"));
        nmas.put("Программа системы видеонаблюдения Видеопортал", Arrays.asList("VP", "CRS"));
        nmas.put("WIX", Arrays.asList("Mobile_WIX"));

        Map<String, YoutrackProject> allYtProjects = youtrackApi.getAllProjects(0, 1000)
                .getData().stream().map(this::convertYtProject).collect(toMap(YoutrackProject::getShortName, Function.identity()));

        Map<String, YoutrackProject> processedProjects = new HashMap<>();
        niokrs.forEach((niokr, projects) ->
                saveDictionary(En_YoutrackWorkType.NIOKR, niokr, projects, allYtProjects, processedProjects));
        nmas.forEach((nma, projects) ->
                saveDictionary(En_YoutrackWorkType.NMA, nma, projects, allYtProjects, processedProjects));
    }

    private YoutrackProject convertYtProject(YtProject ytProject) {
        YoutrackProject project = new YoutrackProject();
        project.setYoutrackId(ytProject.id);
        project.setShortName(ytProject.shortName);
        return project;
    }

    private void saveDictionary(En_YoutrackWorkType type, String name, List<String> projects,
                                Map<String, YoutrackProject> allYtProjects, Map<String, YoutrackProject> processedProjects) {
        YoutrackWorkDictionary dictionary = new YoutrackWorkDictionary();
        dictionary.setType(type);
        dictionary.setName(name);
        dictionary.setYoutrackProjects(
                projects.stream().map(projectName -> selectYoutrackProject(projectName, allYtProjects))
                        .collect(toList()));
        youtrackWorkDictionaryDAO.persist(dictionary);
        dictionary.setYoutrackProjects(saveProjects(dictionary.getYoutrackProjects(), processedProjects));
        jdbcManyRelationsHelper.persist(dictionary, YoutrackWorkDictionary.Fields.YOUTRACK_PROJECTS);
    }

    private YoutrackProject selectYoutrackProject(String projectName, Map<String, YoutrackProject> allYtProjects) {
        return allYtProjects.compute(projectName, (name, project) -> {
            if (project == null) {
                log.warn("youtrackWorkDictionaries. NO YT PROJECT FOUND: {}", name);
                YoutrackProject noFoundedProject = new YoutrackProject();
                noFoundedProject.setShortName(name);
                noFoundedProject.setYoutrackId(name);
                return noFoundedProject;
            }
            return project;
        });
    }

    private List<YoutrackProject> saveProjects(List<YoutrackProject> projects, Map<String, YoutrackProject> processedProjects) {
        return projects.stream().map(project -> processedProjects.compute(project.getShortName(), (name, persistedProject) -> {
            if (persistedProject == null) {
                youtrackProjectDAO.persist(project);
                return project;
            }
            return persistedProject;
        })).collect(Collectors.toList());
    }

    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseFilterDAO caseFilterDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    BootstrapAppDAO bootstrapAppDAO;
    @Autowired
    ReportDAO reportDAO;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ContactItemDAO contactItemDAO;
    @Autowired
    WorkerEntryDAO workerEntryDAO;
    @Autowired
    CaseShortViewDAO caseShortViewDAO;
    @Autowired
    YoutrackApi youtrackApi;
    @Autowired
    YoutrackWorkDictionaryDAO youtrackWorkDictionaryDAO;
    @Autowired
    YoutrackProjectDAO youtrackProjectDAO;
    @Autowired
    CardTypeDAO cardTypeDAO;
    @Autowired
    UserRoleDAO userRoleDAO;
    @Autowired
    Lang lang;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
}
