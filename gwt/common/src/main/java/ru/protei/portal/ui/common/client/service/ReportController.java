package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

@RemoteServiceRelativePath( "springGwtServices/ReportController" )
public interface ReportController extends RemoteService {

    Long saveReport(ReportDto report) throws RequestFailedException;

    ReportDto getReport(Long id) throws RequestFailedException;

    SearchResult<ReportDto> getReportsByQuery(ReportQuery query) throws RequestFailedException;

    List<Long> removeReports(Set<Long> include, Set<Long> exclude) throws RequestFailedException;

    Long recreateReport(Long id) throws RequestFailedException;

    Long cancelReport(Long id) throws RequestFailedException;
}
