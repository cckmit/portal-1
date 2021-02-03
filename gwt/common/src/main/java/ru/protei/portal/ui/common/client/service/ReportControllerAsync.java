package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

public interface ReportControllerAsync {

    void saveReport(ReportDto report, AsyncCallback<Long> async);

    void getReport(Long id, AsyncCallback<ReportDto> async);

    void getReportsByQuery(ReportQuery query, AsyncCallback<SearchResult<ReportDto>> async);

    void removeReports(Set<Long> include, Set<Long> exclude, AsyncCallback<List<Long>> async);

    void recreateReport(Long id, AsyncCallback<Long> async);

    void cancelReport(Long id, AsyncCallback<Long> async);
}
