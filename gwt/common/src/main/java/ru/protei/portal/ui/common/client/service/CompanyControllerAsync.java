package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.List;

/**
 * Асинхронный сервис по работе с компаниями
 */
public interface CompanyControllerAsync {

    /**
     * Получение кол-ва компаний
     * @param query запрос
     */
    void getCompaniesCount( CompanyQuery query, AsyncCallback< Long > async );

    /**
     * Получение списка компаний
     * @param query запрос
     * @return список компаний
     */
    void getCompanies( CompanyQuery query, AsyncCallback< List< Company > > async );

    /**
     * Получение списка групп компаний
     * @param searchPattern шаблон поиска
     * @return список групп
     */
    void getCompanyGroups( String searchPattern, AsyncCallback<List < CompanyGroup > > async );

    /**
     * Сохранение компании ( создание + изменение )
     * @param company компания
     * @return результат сохранения
     */
    void saveCompany ( Company company, AsyncCallback< Boolean > async );

    /**
     * Проверка уникальности названия компании
     * @param name название компании
     * @param excludeId идентификатор компании, которую необходимо исключить из проверки
     * @return true/false
     */
    void isCompanyNameExists ( String name, Long excludeId, AsyncCallback< Boolean > async );

    /**
     * Проверка уникальности названия группы компаний
     * @param name название компании
     * @param excludeId идентификатор группы, которую необходимо исключить из проверки
     * @return true/false
     */
    void isGroupNameExists ( String name, Long excludeId, AsyncCallback< Boolean > async );

    /**
     * Получение компании по ID
     * @param id
     * @param callback
     */
    void getCompany( long id, AsyncCallback< Company > callback );

    /**
     * Получение списка сокращенного представления компании (name,id)
     * @param callback
     */
    void getCompanyOptionList(CompanyQuery query, AsyncCallback<List<EntityOption>> callback);

    /**
     * Получение списка сокращенного представления группы компаний (name,id)
     * @param callback
     */
    void getGroupOptionList( AsyncCallback< List< EntityOption > > callback );

    /**
     * Получение списка сокращенного представления категории компаний (name,id)
     * @param callback
     */
    void getCategoryOptionList( AsyncCallback< List< EntityOption > > callback );

    /**
     * Обновить адреса подписок своей компании
     * @param value
     * @param callback
     */
    void updateSelfCompanySubscription( List<CompanySubscription> value, AsyncCallback<List<CompanySubscription>> callback );

    /**
     * Получение список рассылок по компании
     *
     * @param companyId
     */
    void getCompanySubscription( Long companyId, AsyncCallback< List< CompanySubscription > > async );

    /**
     * Получить список доступных статусов обращения
     */
    void getCompanyCaseStates(Long id, AsyncCallback<List<CaseState>> async);

}
