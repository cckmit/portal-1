package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

public interface ReportService {

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<Long> saveReport(AuthToken authToken, ReportDto report);

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<Long> recreateReport(AuthToken authToken, Long reportId);

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<ReportDto> getReport(AuthToken authToken, Long reportId);

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<SearchResult<ReportDto>> getReports(AuthToken token, ReportQuery query);

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<ReportContent> downloadReport(AuthToken authToken, Long reportId);

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<List<Long>> removeReports(AuthToken authToken, Set<Long> includeIds, Set<Long> excludeIds);

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<List<Long>> removeReports(AuthToken authToken, ReportQuery query, Set<Long> excludeIds);

    @Privileged(requireAny = { En_Privilege.ISSUE_REPORT, En_Privilege.CONTRACT_REPORT, En_Privilege.YT_REPORT })
    Result<Long> cancelReport(AuthToken authToken, Long reportId);

    Result<List<Long>> removeReports(List<Report> reports);

    Result<ReportDto> convertReportToDto(Report report);

    Result<String> getReportFilename(Long reportId, ReportDto reportDto);
}
