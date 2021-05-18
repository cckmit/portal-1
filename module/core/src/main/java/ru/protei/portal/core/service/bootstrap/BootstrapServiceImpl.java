package ru.protei.portal.core.service.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

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
        /**
         *  end Спринт 72 */

        /**
         * begin Спринт 73 */
        bootstrapAppDAO.removeByCondition("name in "+ HelperFunc.makeInArg(
                Arrays.asList("addDeliveryCaseType", "addDeliveryCaseStates", "addDeliveryCaseStateMatrix"), true)
        );
        /**
         *  end Спринт */

        log.info( "bootstrapApplication(): BootstrapService complete."  );
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

            if (CollectionUtils.isEmpty(personIds)) {
                continue;
            }

            // Update person company
            personIds.forEach(personId -> {
                personDAO.partialMerge(new Person(personId, company.getId()), "company_id");
                log.info("changePersonToSingleCompany(): person with id={} updated", personId);
            });

            // Update manager company of issue
            CaseQuery caseQuery = new CaseQuery();
            caseQuery.setType(En_CaseType.CRM_SUPPORT);
            caseQuery.setManagerIds(personIds);
            caseShortViewDAO.listByQuery(caseQuery).forEach(caseShortView -> {
                caseShortView.setManagerCompanyId(company.getId());
                caseShortViewDAO.partialMerge(caseShortView, "manager_company_id");
                log.info("changePersonToSingleCompany(): issue with id={} updated", caseShortView.getId());
            });

            // Update manager company of filter
            List<CaseFilter> filters = caseFilterDAO.getListByCondition("params like ? and type = ?", "%managerIds%", En_CaseFilterType.CASE_OBJECTS.name());
            for (CaseFilter filter : filters) {
                try {
                    CaseQuery query = objectMapper.readValue(filter.getParams(), CaseQuery.class);
                    List<Long> managerCompanyIds = emptyIfNull(query.getManagerCompanyIds());
                    if (emptyIfNull(query.getManagerIds()).stream().anyMatch(personIds::contains) &&
                            !managerCompanyIds.contains(company.getId())) {
                        managerCompanyIds.add(company.getId());
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
                            !managerCompanyIds.contains(company.getId())) {
                        managerCompanyIds.add(company.getId());
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

        log.debug("changePersonToSingleCompany(): finish");
    }

    private void updateContactItemsAccessType() {
        List<ContactItem> contactItems = contactItemDAO.getListByCondition("access_type is null");
        contactItems.forEach(item -> item.modify(En_ContactDataAccess.PUBLIC));
        contactItemDAO.saveOrUpdateBatch(contactItems);
    }

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
    CaseStateDAO caseStateDAO;
    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private static final long CASE_STATE_DELIVERY_PRELIMINARY_ID = 39L;
    private static final long CASE_STATE_DELIVERY_PRE_RESERVE_ID = 40L;
    private static final long CASE_STATE_DELIVERY_RESERVE_ID = 41L;
    private static final long CASE_STATE_DELIVERY_ASSEMBLY_ID = 42L;
    private static final long CASE_STATE_DELIVERY_TEST_ID = 43L;
    private static final long CASE_STATE_DELIVERY_READY_ID = 44L;
    private static final long CASE_STATE_DELIVERY_SENT_ID = 45L;
    private static final long CASE_STATE_DELIVERY_WORK_ID = 46L;
}
