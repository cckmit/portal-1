package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

/**
 * Асинхронный сервис управления должностными лицами
 */
public interface OfficialServiceAsync {

    void getOfficial(Long id, AsyncCallback< Official > async);

    void getOfficialsByRegions(OfficialQuery query, AsyncCallback<Map<String, List<Official>>> async);

    void getOfficialMember(Long id, AsyncCallback<OfficialMember> asyncCallback);

    void saveOfficialMember(OfficialMember officialMember, AsyncCallback<OfficialMember> asyncCallback);

    void updateOfficial(Official official, AsyncCallback<Official> asyncCallback);

    void createOfficial(Official official, AsyncCallback<Long> asyncCallback);

    void createOfficialMember(OfficialMember officialMember, Long parentId, AsyncCallback<Long> asyncCallback);

    void removeOfficial(Long id, AsyncCallback<Boolean> asyncCallback);

    void removeOfficialMember(Long id, AsyncCallback<Boolean> asyncCallback);
}