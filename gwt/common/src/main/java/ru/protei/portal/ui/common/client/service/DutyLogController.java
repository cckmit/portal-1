package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * Сервис управления журналами дежурств
 */
@RemoteServiceRelativePath( "springGwtServices/DutyLogController" )
public interface DutyLogController extends RemoteService {

    SearchResult<DutyLog> getDutyLogs(DutyLogQuery query) throws RequestFailedException;

    DutyLog getDutyLog(Long id) throws RequestFailedException;

    Long saveDutyLog(DutyLog value) throws RequestFailedException;

    void createReport(String name, DutyLogQuery query) throws RequestFailedException;
}
