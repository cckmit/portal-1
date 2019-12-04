package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class ReportServiceImpl implements ReportService {

    private final static String LOCALE_RU = Locale.forLanguageTag("ru").toString();
    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @Autowired
    ReportDAO reportDAO;
    @Autowired
    ReportControlService reportControlService;
    @Autowired
    AuthService authService;
    @Autowired
    ReportStorageService reportStorageService;
    @Autowired
    PolicyService policyService;

    @Override
    public Result<Long> createReport( AuthToken token, Report report) {
        if (report == null || report.getReportType() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isQueryNotValid(report.getCaseQuery())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        applyFilterByScope(token, report);

        Date now = new Date();
        report.setCreatorId(token.getPersonId());
        report.setCreated(now);
        report.setModified(now);
        report.setStatus(En_ReportStatus.CREATED);
        report.setRestricted(!hasGrantAccessForReport(token));
        if (StringUtils.isBlank(report.getLocale())) {
            report.setLocale(LOCALE_RU);
        }
        if (StringUtils.isBlank(report.getName())) {
            String langKey = "report_at";
            switch (report.getReportType()) {
                case CASE_OBJECTS: langKey = "report_case_objects_at"; break;
                case CASE_TIME_ELAPSED: langKey = "report_case_time_elapsed_at"; break;
            }
            Lang.LocalizedLang localizedLang = getLang().getFor(Locale.forLanguageTag(report.getLocale()));
            report.setName(localizedLang.get(langKey) + " " + dateFormat.format(now));
        }

        Long id = reportDAO.persist(report);

        reportControlService.processNewReports();

        return ok(id);
    }

    @Override
    public Result recreateReport( AuthToken token, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Report report = reportDAO.getReport(token.getPersonId(), id);

        if (report == null || report.getStatus() != En_ReportStatus.ERROR) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        report.setStatus(En_ReportStatus.CREATED);
        report.setModified(new Date());
        report.setRestricted(!hasGrantAccessForReport(token));

        reportDAO.merge(report);

        reportControlService.processNewReports();

        return ok();
    }

    @Override
    public Result<Report> getReport( AuthToken token, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Report report = reportDAO.getReport(token.getPersonId(), id);
        return ok(report);
    }

    @Override
    public Result<SearchResult<Report>> getReports( AuthToken token, ReportQuery query) {
        SearchResult<Report> sr = reportDAO.getSearchResult(token.getPersonId(), query, null);
        return ok(sr);
    }

    @Override
    public Result<ReportContent> downloadReport( AuthToken token, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Report report = reportDAO.getReport(token.getPersonId(), id);

        if (report == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        if (report.getStatus() != En_ReportStatus.READY) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        return reportStorageService.getContent(report.getId());
    }

    @Override
    public Result removeReports( AuthToken token, Set<Long> include, Set<Long> exclude) {

        List<Report> reports = reportDAO.getReportsByIds(token.getPersonId(), include, exclude);
        removeReports(reports);

        return ok();
    }

    @Override
    public Result removeReports( AuthToken token, ReportQuery query, Set<Long> exclude) {

        SearchResult<Report> sr = reportDAO.getSearchResult(token.getPersonId(), query, exclude);
        removeReports(sr.getResults());

        return ok();
    }

    private void removeReports(List<Report> reports) {
        List<Long> idsToRemove = new ArrayList<>();
        for (Report report : reports) {
            idsToRemove.add(report.getId());
        }
        reportStorageService.removeContent(idsToRemove);
        reportDAO.removeByKeys(idsToRemove);
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

    private void applyFilterByScope( AuthToken token, Report report) {
        if (!hasGrantAccessForReport(token)) {
            report.setReportType( En_ReportType.CASE_OBJECTS);
            CaseQuery query = report.getCaseQuery();
            query.setCompanyIds(acceptAllowedCompanies(query.getCompanyIds(), token.getCompanyAndChildIds()));
            query.setAllowViewPrivate(false);
        }
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }

    private boolean hasGrantAccessForReport(AuthToken token) {
        Set< UserRole > roles = token.getRoles();
        return policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_REPORT);
    }
}
