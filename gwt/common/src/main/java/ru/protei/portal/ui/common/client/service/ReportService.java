package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления отчетами
 */
@RemoteServiceRelativePath( "springGwtServices/ReportService" )
public interface ReportService extends RemoteService {

    Long createReport(Report report) throws RequestFailedException;

    Report getReport(Long id) throws RequestFailedException;

    List<Report> getReportsByQuery(ReportQuery query) throws RequestFailedException;

    Long getReportsCount(ReportQuery query);

    void removeReports(Set<Long> include, Set<Long> exclude) throws RequestFailedException;
}
