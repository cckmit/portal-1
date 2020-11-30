package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.winter.core.utils.beans.SearchResult;


/**
 * Асинхронный сервис управления журналом дежурств
 */
public interface DutyLogControllerAsync {

    void getDutyLogs(DutyLogQuery query, AsyncCallback<SearchResult<DutyLog>> async);

    void getDutyLog(Long id, AsyncCallback<DutyLog> async);

    void saveDutyLog(DutyLog value, AsyncCallback<Long> async);

    void createReport(String name, DutyLogQuery query, AsyncCallback<Void> callback);
}
