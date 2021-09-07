package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с yt сущностями
 */
@RemoteServiceRelativePath( "springGwtServices/YoutrackReportDictionaryController" )
public interface YoutrackReportDictionaryController extends RemoteService {

    List<YoutrackReportDictionary> getDictionaries(En_ReportYoutrackWorkType type) throws RequestFailedException;

    YoutrackReportDictionary createDictionary(YoutrackReportDictionary dictionary) throws RequestFailedException;

    YoutrackReportDictionary updateDictionary(YoutrackReportDictionary dictionary) throws RequestFailedException;

    YoutrackReportDictionary removeDictionary(YoutrackReportDictionary dictionary) throws RequestFailedException;
}
