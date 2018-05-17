package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.winter.repo.model.dto.Content;

import java.util.List;
import java.util.Set;

public class ReportServiceImpl implements ReportService {

    @Override
    public CoreResponse<Long> createReport(AuthToken authToken, Report report) {
        return null;
    }

    @Override
    public CoreResponse<Report> getReport(AuthToken authToken, Long id) {
        return null;
    }

    @Override
    public CoreResponse<List<Report>> getReportsByQuery(Long creatorId, ReportQuery query) {
        return null;
    }

    @Override
    public CoreResponse<Content> downloadReport(AuthToken authToken, Long id) {
        return null;
    }

    @Override
    public CoreResponse removeReports(AuthToken authToken, Set<Long> include, Set<Long> exclude) {
        return null;
    }

    @Override
    public CoreResponse removeReports(AuthToken authToken, ReportQuery query, Set<Long> exclude) {
        return null;
    }
}
