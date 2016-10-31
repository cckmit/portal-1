package ru.protei.portal.ui.contact.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/ContactService" )
public interface ContactService extends RemoteService {

    /**
     * Получение списка компаний
     * @param searchPattern шаблон поиска
     * @param company компания
     * @param isFired признак уволенности
     * @param sortField поле для сортировки
     * @param sortDir направление сортировки
     * @return список контактов
     */
    List< Person > getContacts (String searchPattern, Company company, int isFired, En_SortField sortField, Boolean sortDir ) throws RequestFailedException;

}
