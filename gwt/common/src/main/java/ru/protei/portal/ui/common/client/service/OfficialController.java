package ru.protei.portal.ui.common.client.service;

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
@RemoteServiceRelativePath( "springGwtServices/OfficialController")
public interface OfficialController extends RemoteService {

    Official getOfficial(Long id) throws RequestFailedException;

    Map<String, List<Official>> getOfficialsByRegions(OfficialQuery query) throws RequestFailedException;

    OfficialMember getOfficialMember(Long id) throws RequestFailedException;

    Long createOfficialMember(OfficialMember officialMember, Long parentId) throws RequestFailedException;

    OfficialMember saveOfficialMember(OfficialMember officialMember) throws RequestFailedException;

    Long createOfficial(Official official) throws RequestFailedException;

    Official updateOfficial(Official official) throws RequestFailedException;

    boolean removeOfficial(Long id) throws RequestFailedException;

    boolean removeOfficialMember(Long id) throws RequestFailedException;
}
