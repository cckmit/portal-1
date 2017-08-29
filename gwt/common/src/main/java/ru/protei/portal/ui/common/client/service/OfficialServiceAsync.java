package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;

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

    void getOfficialMembersByProducts(AsyncCallback<Map<String, List<OfficialMember>>> async);

    void getOfficialMember(Long id, AsyncCallback<OfficialMember> asyncCallback);

    void saveOfficialMember(OfficialMember officialMember, AsyncCallback<OfficialMember> asyncCallback);

    void initMembers(AsyncCallback<Boolean> callback);
}
