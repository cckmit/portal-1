package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Set;

public interface ReportControllerAsync {

    void createReport(Report report, AsyncCallback<Long> async);

    void getReport(Long id, AsyncCallback<Report> async);

    void getReportsByQuery(ReportQuery query, AsyncCallback<SearchResult<Report>> async);

    void removeReports(Set<Long> include, Set<Long> exclude, AsyncCallback<Void> async);

    void recreateReport(Long id, AsyncCallback<Void> async);
}
