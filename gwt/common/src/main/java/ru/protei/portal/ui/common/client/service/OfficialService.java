package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Official;
import java.util.List;
import java.util.Map;

/**
 * Сервис управления должностными лицами
 */
@RemoteServiceRelativePath( "springGwtServices/OfficialService")
public interface OfficialService extends RemoteService {

    List<Official> getOfficialList();

    Official getOfficial(long id);

    Long getOfficialCount();

    Map<String, List<Official>> getOfficialsByRegions();
}
