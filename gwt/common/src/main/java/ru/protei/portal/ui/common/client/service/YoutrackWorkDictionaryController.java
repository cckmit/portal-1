package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с yt сущностями
 */
@RemoteServiceRelativePath( "springGwtServices/YoutrackWorkDictionaryController" )
public interface YoutrackWorkDictionaryController extends RemoteService {

    List<YoutrackWorkDictionary> getDictionaries(En_YoutrackWorkType type) throws RequestFailedException;

    YoutrackWorkDictionary createDictionary(YoutrackWorkDictionary dictionary) throws RequestFailedException;

    YoutrackWorkDictionary updateDictionary(YoutrackWorkDictionary dictionary) throws RequestFailedException;

    YoutrackWorkDictionary removeDictionary(YoutrackWorkDictionary dictionary) throws RequestFailedException;
}
