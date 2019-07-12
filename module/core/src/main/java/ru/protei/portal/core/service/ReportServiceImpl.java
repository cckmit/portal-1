package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public CoreResponse<Long> createReport(AuthToken token, Report report) {
        if (report == null || report.getReportType() == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isQueryNotValid(report.getCaseQuery())) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        applyFilterByScope(token, report);

        UserSessionDescriptor descriptor = authService.findSession(token);

        Date now = new Date();
        report.setCreatorId(descriptor.getPerson().getId());
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

        return new CoreResponse<Long>().success(id);
    }

    @Override
    public CoreResponse recreateReport(AuthToken token, Long id) {
        if (id == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserSessionDescriptor descriptor = authService.findSession(token);
        Report report = reportDAO.getReport(descriptor.getPerson().getId(), id);

        if (report == null || report.getStatus() != En_ReportStatus.ERROR) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        report.setStatus(En_ReportStatus.CREATED);
        report.setModified(new Date());
        report.setRestricted(!hasGrantAccessForReport(token));

        reportDAO.merge(report);

        reportControlService.processNewReports();

        return new CoreResponse<>().success(null);
    }

    @Override
    public CoreResponse<Report> getReport(AuthToken token, Long id) {
        if (id == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserSessionDescriptor descriptor = authService.findSession(token);
        Report report = reportDAO.getReport(descriptor.getPerson().getId(), id);

        return new CoreResponse<Report>().success(report);
    }

    @Override
    public CoreResponse<SearchResult<Report>> getReports(AuthToken token, ReportQuery query) {

        UserSessionDescriptor descriptor = authService.findSession(token);
        SearchResult<Report> sr = reportDAO.getSearchResult(descriptor.getPerson().getId(), query, null);
        return new CoreResponse<SearchResult<Report>>().success(sr);
    }

    @Override
    public CoreResponse<ReportContent> downloadReport(AuthToken token, Long id) {
        if (id == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserSessionDescriptor descriptor = authService.findSession(token);
        Report report = reportDAO.getReport(descriptor.getPerson().getId(), id);

        if (report == null) {
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);
        }
        if (report.getStatus() != En_ReportStatus.READY) {
            return new CoreResponse().error(En_ResultStatus.NOT_AVAILABLE);
        }

        return reportStorageService.getContent(report.getId());
    }

    @Override
    public CoreResponse removeReports(AuthToken token, Set<Long> include, Set<Long> exclude) {

        UserSessionDescriptor descriptor = authService.findSession(token);
        List<Report> reports = reportDAO.getReportsByIds(descriptor.getPerson().getId(), include, exclude);
        removeReports(reports);

        return new CoreResponse<>().success(null);
    }

    @Override
    public CoreResponse removeReports(AuthToken token, ReportQuery query, Set<Long> exclude) {

        UserSessionDescriptor descriptor = authService.findSession(token);
        SearchResult<Report> sr = reportDAO.getSearchResult(descriptor.getPerson().getId(), query, exclude);
        removeReports(sr.getResults());

        return new CoreResponse<>().success(null);
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
        UserSessionDescriptor descriptor = authService.findSession(token);
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_REPORT)) {
            report.setReportType( En_ReportType.CASE_OBJECTS);
            CaseQuery query = report.getCaseQuery();
            query.setCompanyIds(acceptAllowedCompanies(query.getCompanyIds(), descriptor.getAllowedCompaniesIds()));
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
        UserSessionDescriptor descriptor = authService.findSession(token);
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        return policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_REPORT);
    }
}
