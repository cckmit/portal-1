package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.service.user.AuthService;

import java.util.*;

public class ReportServiceImpl implements ReportService {

    private final static String LOCALE_RU = Locale.forLanguageTag("ru").toString();

    @Autowired
    ReportDAO reportDAO;

    @Autowired
    ReportControlService reportControlService;

    @Autowired
    AuthService authService;

    @Autowired
    ReportStorageService reportStorageService;

    @Override
    public CoreResponse<Long> createReport(AuthToken token, Report report) {
        if (report == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserSessionDescriptor descriptor = authService.findSession(token);

        Date now = new Date();
        report.setCreatorId(descriptor.getPerson().getId());
        report.setCreated(now);
        report.setModified(now);
        report.setStatus(En_ReportStatus.CREATED);
        if (report.getLocale() == null) {
            report.setLocale(LOCALE_RU);
        }

        Long id = reportDAO.persist(report);

        reportControlService.processNewReports();

        return new CoreResponse<Long>().success(id);
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
    public CoreResponse<List<Report>> getReportsByQuery(AuthToken token, ReportQuery query) {

        UserSessionDescriptor descriptor = authService.findSession(token);
        List<Report> reports = reportDAO.getReportsByQuery(descriptor.getPerson().getId(), query, null);

        return new CoreResponse<List<Report>>().success(reports);
    }

    @Override
    public CoreResponse<Long> count(AuthToken authToken, ReportQuery query) {

        Long count = reportDAO.count(query);

        if (count == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR, 0L);
        }

        return new CoreResponse<Long>().success(count);
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
        List<Report> reports = reportDAO.getReportsByQuery(descriptor.getPerson().getId(), query, exclude);
        removeReports(reports);

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
}
