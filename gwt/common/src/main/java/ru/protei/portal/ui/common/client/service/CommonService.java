package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Общий сервис
 */
@RemoteServiceRelativePath( "springGwtServices/CommonService" )
public interface CommonService extends RemoteService {

    /**
     * Получение списка статусов
     * @param caseType тип кейса
     * @return список статусов
     */
    List<En_CaseState> getStatesByCaseType(En_CaseType caseType) throws RequestFailedException;

}
