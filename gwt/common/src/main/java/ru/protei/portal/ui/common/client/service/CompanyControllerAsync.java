package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * Асинхронный сервис по работе с компаниями
 */
public interface CompanyControllerAsync {

    /**
     * Получение списка компаний
     * @param query запрос
     * @return список компаний
     */
    void getCompanies( CompanyQuery query, AsyncCallback< SearchResult< Company >> async );

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
     * Получение списка сокращенного представления компании-субподрядчика через проекты (name,id)
     * @param callback
     */
    void getSubcontractorOptionList(Long companyId, boolean isActive, AsyncCallback<List<EntityOption>> callback);

    /**
     * Получение списка сокращенного представления компании-инициатора через проекты (name,id)
     * @param callback
     */
    void getInitiatorOptionList(Long subcontractorId, boolean isActive, AsyncCallback<List<EntityOption>> callback);

    /**
     * Получение списка сокращенного представления компании (name,id) игнорируя область видимости и привилегии
     * @param callback
     */
    void getCompanyOptionListIgnorePrivileges(CompanyQuery query, AsyncCallback<List<EntityOption>> callback);

    /**
     * Получение списка сокращенного представления группы компаний (name,id)
     * @param callback
     */
    void getGroupOptionList( AsyncCallback< List< EntityOption > > callback );

    /**
     * Получение списка сокращенного представления категории компаний (name,id)
     * @param callback
     */
    void getCategoryOptionList( AsyncCallback< List<En_CompanyCategory> > callback );

    /**
     * Получение список рассылок по компании
     *
     * @param companyId
     */
    void getCompanySubscription( Long companyId, AsyncCallback< List< CompanySubscription > > async );
    void getCompanyWithParentCompanySubscriptions(Set<Long> companyIds, AsyncCallback<List<CompanySubscription>> async );

    /**
     * Получить список доступных статусов обращения
     */
    void getCompanyCaseStates(Long id, AsyncCallback<List<CaseState>> async);

    void updateState(Long id, boolean isArchived, AsyncCallback<Boolean> async);

    void getImportanceLevels(Long id, AsyncCallback<List<En_ImportanceLevel>> async);

    void getAllHomeCompanies(AsyncCallback<List<EntityOption>> async);

    void getCompanyOmitPrivileges(long id, AsyncCallback<Company> async);

    void getCompanyImportanceItems(Long companyId, AsyncCallback<List<CompanyImportanceItem>> async);
}
