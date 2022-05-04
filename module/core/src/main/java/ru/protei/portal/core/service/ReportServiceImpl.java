package ru.protei.portal.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.event.ProcessNewReportsEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.*;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isNotBlank;

public class ReportServiceImpl implements ReportService {

    private static Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    private final static String LOCALE_RU = Locale.forLanguageTag("ru").toString();
    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private static final Map<En_ReportType, Pair<En_Privilege, List<En_Scope>>> type2privilege = new HashMap<En_ReportType, Pair<En_Privilege, List<En_Scope>>>() {{
        put(En_ReportType.CASE_OBJECTS, new Pair<>(En_Privilege.ISSUE_REPORT, listOf()));
        put(En_ReportType.CASE_TIME_ELAPSED, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.CASE_RESOLUTION_TIME, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.PROJECT, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.CONTRACT, new Pair<>(En_Privilege.CONTRACT_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.NIGHT_WORK, new Pair<>(En_Privilege.ISSUE_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.YT_WORK, new Pair<>(En_Privilege.YT_REPORT, listOf(En_Scope.SYSTEM)));
        put(En_ReportType.TRANSPORTATION_REQUEST, new Pair<>(En_Privilege.TRANSPORTATION_REQUEST_REPORT, listOf(En_Scope.SYSTEM)));
    }};

    @Autowired
    ReportDAO reportDAO;
    @Autowired
    AuthService authService;
    @Autowired
    ReportStorageService reportStorageService;
    @Autowired
    PolicyService policyService;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    CompanyService companyService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PortalConfig config;

    @Override
    @Transactional
    public Result<Long> saveReport(AuthToken token, ReportDto reportDto) {

        if (token == null || reportDto == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Report report = reportDto.getReport();
        if (report == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        En_ReportType reportType = report.getReportType();
        if (reportType == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean hasAccess = canEdit(token, reportType);
        boolean hasGrantAccess = hasGrantAccess(token, reportType);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        BaseQuery query = reportDto.getQuery();
        if (query == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        query = applyFilterByScope(token, report, query);
        if (isQueryNotValid(query)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean isCreate = (report.getId() == null);
        Date now = new Date();
        if (isCreate) {
            report.setCreatorId(token.getPersonId());
            report.setCreated(now);
            report.setRestricted(!hasGrantAccess);
            report.setSystemId(systemId);
            if (StringUtils.isBlank(report.getLocale())) {
                report.setLocale(LOCALE_RU);
            }
            if (StringUtils.isBlank(report.getName())) {
                report.setName(makeReportName(report.getReportType(), report.getLocale()));
            }
        }

        report.setStatus(En_ReportStatus.CREATED);
        report.setModified(now);
        report.setQuery(serializeQuery(query));

        reportDAO.saveOrUpdate(report);

        publisherService.publishEvent(new ProcessNewReportsEvent(this));

        return ok(report.getId());
    }

    @Override
    @Transactional
    public Result<Long> recreateReport(AuthToken token, Long id) {

        if (token == null || id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        Report report = reportDAO.getReport(token.getPersonId(), id);
        if (report == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ReportType reportType = report.getReportType();
        if (reportType == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!Objects.equals(report.getSystemId(), systemId)) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        boolean hasAccess = canEdit(token, reportType);
        boolean hasGrantAccess = hasGrantAccess(token, reportType);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        boolean isError = report.getStatus() == En_ReportStatus.ERROR;
        boolean isCancelled = report.getStatus() == En_ReportStatus.CANCELLED;
        if (!isError && !isCancelled) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        report.setStatus(En_ReportStatus.CREATED);
        report.setModified(new Date());
        report.setRestricted(!hasGrantAccess);

        if (reportDAO.merge(report)) {
            publisherService.publishEvent(new ProcessNewReportsEvent(this));
        }

        return ok(id);
    }

    @Override
    public Result<ReportDto> getReport(AuthToken token, Long id) {

        if (token == null || id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        Report report = reportDAO.getReport(token.getPersonId(), id);
        if (report == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!Objects.equals(report.getSystemId(), systemId)) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        boolean hasAccess = canView(token, report.getReportType());
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        ReportDto reportDto = convertReportToDto(report).getData();
        return ok(reportDto);
    }

    @Override
    public Result<SearchResult<ReportDto>> getReports(AuthToken token, ReportQuery query) {

        if (token == null || query == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        query.setSystemId(systemId);
        List<En_ReportType> reportTypes = availableReportTypes(token);

        SearchResult<Report> result = reportDAO.getSearchResult(token.getPersonId(), query, null);
        List<ReportDto> reports = stream(result.getResults())
                .filter(report -> reportTypes.contains(report.getReportType()))
                .map(report -> convertReportToDto(report).getData())
                .collect(Collectors.toList());

        return ok(new SearchResult<>(reports));
    }

    @Override
    public Result<ReportContent> downloadReport(AuthToken token, Long id) {

        if (token == null || id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        Report report = reportDAO.getReport(token.getPersonId(), id);
        if (report == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!Objects.equals(report.getSystemId(), systemId)) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        boolean hasAccess = canView(token, report.getReportType());
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (report.getStatus() != En_ReportStatus.READY) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        return reportStorageService.getContent(report.getId());
    }

    @Override
    @Transactional
    public Result<List<Long>> removeReports(AuthToken token, Set<Long> includeIds, Set<Long> excludeIds) {

        if (token == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        List<En_ReportType> reportTypes = availableReportTypes(token);

        List<Report> reports = stream(reportDAO.getReportsByIds(token.getPersonId(), includeIds, excludeIds, systemId))
                .filter(report -> reportTypes.contains(report.getReportType()))
                .collect(Collectors.toList());
        List<Long> ids = removeReports(reports).getData();

        return ok(ids);
    }

    @Override
    @Transactional
    public Result<List<Long>> removeReports(AuthToken token, ReportQuery query, Set<Long> exclude) {

        if (token == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        query.setSystemId(systemId);
        List<En_ReportType> reportTypes = availableReportTypes(token);

        List<Report> reports = stream(reportDAO.getSearchResult(token.getPersonId(), query, exclude).getResults())
                .filter(report -> reportTypes.contains(report.getReportType()))
                .collect(Collectors.toList());
        List<Long> ids = removeReports(reports).getData();

        return ok(ids);
    }

    @Override
    @Transactional
    public Result<Long> cancelReport(AuthToken token, Long id) {

        if (token == null || id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        Report report = reportDAO.getReport(token.getPersonId(), id);
        if (report == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!Objects.equals(report.getSystemId(), systemId)) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        boolean hasAccess = canEdit(token, report.getReportType());
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (report.getStatus() == En_ReportStatus.PROCESS) {
            report.setStatus(En_ReportStatus.CANCELLED);
            report.setModified(new Date());
            reportDAO.merge(report);
        }

        return ok(report.getId());
    }

    @Override
    @Transactional
    public Result<List<Long>> removeReports(List<Report> reports) {

        final String systemId = config.data().getCommonConfig().getSystemId();
        if (HelperFunc.isEmpty(systemId)) {
            return error(En_ResultStatus.REPORTING_SERVICE_NOT_CONFIGURED);
        }

        List<Long> idsToRemove = stream(reports)
                .filter(report -> Objects.equals(report.getSystemId(), systemId))
                .map(Report::getId)
                .collect(Collectors.toList());

        reportStorageService.removeContent(idsToRemove);

        Date now = new Date();
        reports.forEach(report -> {
            report.setRemoved(true);
            report.setModified(now);
        });
        reportDAO.mergeBatch(reports);

        return ok(idsToRemove);
    }

    @Override
    public Result<ReportDto> convertReportToDto(Report report) {
        try {
            if (report == null) {
                return ok(null);
            }
            switch (report.getReportType()) {
                case CASE_OBJECTS:
                case CASE_TIME_ELAPSED:
                case CASE_RESOLUTION_TIME:
                case NIGHT_WORK: return ok(new ReportCaseQuery(
                        report,
                        objectMapper.readValue(report.getQuery(), CaseQuery.class)
                ));
                case CONTRACT: return ok(new ReportContractQuery(
                        report,
                        objectMapper.readValue(report.getQuery(), ContractQuery.class)
                ));
                case PROJECT: return ok(new ReportProjectQuery(
                        report,
                        objectMapper.readValue(report.getQuery(), ProjectQuery.class)
                ));
                case YT_WORK: return ok(new ReportYoutrackWorkQuery(
                        report,
                        objectMapper.readValue(report.getQuery(), YoutrackWorkQuery.class)
                ));
                case TRANSPORTATION_REQUEST: return ok(new ReportTransportationRequestQuery(
                        report,
                        objectMapper.readValue(report.getQuery(), TransportationRequestQuery.class)
                ));
            }
            throw new IllegalStateException("No switch branch matched for En_ReportType");
        } catch (IOException e) {
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR, e);
        }
    }

    @Override
    public Result<String> getReportFilename(Long reportId, ReportDto reportDto) {
        if (reportDto == null || reportDto.getReport() == null) {
            return ok(getDefaultFilename(reportId));
        }
        String name = reportDto.getReport().getName() + ".xlsx";
        if (isNotBlank(name)) {
            return ok(name);
        }
        return ok(getDefaultFilename(reportId));
    }

    private String getDefaultFilename(Long reportId) {
        return reportStorageService.getFileName(String.valueOf(reportId)).getData();
    }

    private Lang getLang() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("Lang");
        messageSource.setDefaultEncoding("UTF-8");
        return new Lang(messageSource);
    }

    private <T extends BaseQuery> boolean isQueryNotValid(T query) {
        return query == null || !query.isParamsPresent();
    }

    private String makeReportName(En_ReportType reportType, String locale) {
        String langKey = "report_at";
        switch (reportType) {
            case CASE_OBJECTS: langKey = "report_case_objects_at"; break;
            case CASE_TIME_ELAPSED: langKey = "report_case_time_elapsed_at"; break;
            case NIGHT_WORK: langKey = "report_night_work_at"; break;
            case PROJECT: langKey = "report_project_at"; break;
            case CONTRACT: langKey = "report_contract_at"; break;
        }
        Lang.LocalizedLang localizedLang = getLang().getFor(Locale.forLanguageTag(locale));
        return localizedLang.get(langKey) + " " + dateFormat.format(new Date());
    }

    private String serializeQuery(BaseQuery query) {
        try {
            return objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR, e);
        }
    }

    private BaseQuery applyFilterByScope(AuthToken token, Report report, BaseQuery query) {
        En_ReportType reportType = report.getReportType();
        switch (reportType) {
            case CASE_OBJECTS:
            case CASE_TIME_ELAPSED:
            case CASE_RESOLUTION_TIME:
            case NIGHT_WORK: {
                if (!hasGrantAccess(token, reportType)) {
                    report.setReportType(En_ReportType.CASE_OBJECTS);
                    CaseQuery caseQuery = (CaseQuery) query;

                    Company company = companyService.getCompanyOmitPrivileges(token, token.getCompanyId()).getData();
                    if (company.getCategory() == En_CompanyCategory.SUBCONTRACTOR) {
                        caseQuery.setManagerCompanyIds(
                                acceptAllowedCompanies(caseQuery.getManagerCompanyIds(), token.getCompanyAndChildIds()));
                    } else {
                        caseQuery.setCompanyIds(
                                acceptAllowedCompanies(caseQuery.getCompanyIds(), token.getCompanyAndChildIds()));
                    }
                    caseQuery.setAllowViewPrivate(false);

                    log.info("applyFilterByScope(): CaseQuery modified: {}", caseQuery);
                    return caseQuery;
                }
                break;
            }
        }
        return query;
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }

    // Access methods

    private boolean canView(AuthToken token, En_ReportType reportType) {
        Pair<En_Privilege, List<En_Scope>> entry = type2privilege.get(reportType);
        En_Privilege privilege = entry.getA();
        List<En_Scope> scopes = entry.getB();
        return hasAccess(token, privilege, scopes);
    }

    private boolean canEdit(AuthToken token, En_ReportType reportType) {
        return canView(token, reportType);
    }

    private boolean hasAccess(AuthToken token, En_Privilege privilege, List<En_Scope> scopes) {
        Set<UserRole> roles = token.getRoles();
        if (isEmpty(scopes)) {
            return policyService.hasPrivilegeFor(privilege, roles);
        } else {
            return stream(scopes)
                    .allMatch(scope -> policyService.hasScopeForPrivilege(roles, privilege, scope));
        }
    }

    private List<En_ReportType> availableReportTypes(AuthToken token) {
        List<En_ReportType> types = new ArrayList<>();
        for (Map.Entry<En_ReportType, Pair<En_Privilege, List<En_Scope>>> entry : type2privilege.entrySet()) {
            En_ReportType type = entry.getKey();
            En_Privilege privilege = entry.getValue().getA();
            List<En_Scope> scopes = entry.getValue().getB();
            boolean hasAccess = hasAccess(token, privilege, scopes);
            if (hasAccess) {
                types.add(type);
            }
        }
        return types;
    }

    private boolean hasGrantAccess(AuthToken token, En_ReportType reportType) {
        Pair<En_Privilege, List<En_Scope>> entry = type2privilege.get(reportType);
        En_Privilege privilege = entry.getA();
        Set<UserRole> roles = token.getRoles();
        return policyService.hasGrantAccessFor(roles, privilege);
    }
}
