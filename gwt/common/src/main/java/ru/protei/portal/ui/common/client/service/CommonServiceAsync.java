package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.List;

/**
 * Created by bondarenko on 10.11.16.
 */
public interface CommonServiceAsync {

    /**
     * Получение списка статусов
     * @param caseType тип кейса
     * @return список статусов
     */
    void getStatesByCaseType(En_CaseType caseType, AsyncCallback<List<En_CaseState>> async);

}
