package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

/**
 * Сервис управления должностными лицами
 */
@RemoteServiceRelativePath( "springGwtServices/OfficialService")
public interface OfficialService extends RemoteService {

    Official getOfficial(long id);

    Map<String, List<Official>> getOfficialsByRegions(OfficialQuery query) throws RequestFailedException;

    Map<String, List<OfficialMember>> getOfficialMembersByProducts(Long id);

    OfficialMember getOfficialMember(Long id);

    void saveOfficialMember(OfficialMember officialMember);

    void initMembers();
}
