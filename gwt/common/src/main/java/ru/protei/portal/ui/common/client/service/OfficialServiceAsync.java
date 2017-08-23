package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Official;

import java.util.List;
import java.util.Map;

/**
 * Асинхронный сервис управления должностными лицами
 */
public interface OfficialServiceAsync {

    void getOfficialList(AsyncCallback< List< Official > > async);

    void getOfficial(long id, AsyncCallback< Official > async);

    void getOfficialCount(AsyncCallback<Long> async);

    void getOfficialsByRegions(AsyncCallback<Map<String, List<Official>>> async);
}
