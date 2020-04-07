package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Сервис управления person
 */
@RemoteServiceRelativePath( "springGwtServices/PersonController" )
public interface PersonController extends RemoteService {

    /**
     * Получение списка сокращенного представления person
     * @return
     */
    List< PersonShortView > getPersonViewList( PersonQuery query ) throws RequestFailedException;

    Map<Long, String> getPersonNames(Collection<Long> ids) throws RequestFailedException;

    Person getPerson(Long id) throws RequestFailedException;

    List< PersonShortView > getCaseMembersList( En_DevUnitPersonRoleType role ) throws RequestFailedException;
}
