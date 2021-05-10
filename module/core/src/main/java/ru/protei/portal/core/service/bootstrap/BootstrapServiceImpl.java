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
}
