package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Set;

/**
 * Сервис управления отчетами
 */
@RemoteServiceRelativePath( "springGwtServices/ReportController" )
public interface ReportController extends RemoteService {

    Long createReport(Report report) throws RequestFailedException;

    Report getReport(Long id) throws RequestFailedException;

    SearchResult<Report> getReportsByQuery(ReportQuery query) throws RequestFailedException;

    void removeReports(Set<Long> include, Set<Long> exclude) throws RequestFailedException;

    void recreateReport(Long id) throws RequestFailedException;

    Long cancelReport(Long id) throws RequestFailedException;
}
