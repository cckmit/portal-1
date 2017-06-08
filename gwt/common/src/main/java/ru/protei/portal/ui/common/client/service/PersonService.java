package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления person
 */
@RemoteServiceRelativePath( "springGwtServices/PersonService" )
public interface PersonService extends RemoteService {

    /**
     * Получение списка сокращенного представления person
     * @return
     */
    List< PersonShortView > getPersonViewList( PersonQuery query ) throws RequestFailedException;

}
