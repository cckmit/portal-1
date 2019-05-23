package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис по работе с компаниями
 */
@RemoteServiceRelativePath( "springGwtServices/CompanyController" )
public interface CompanyController extends RemoteService {

    /**
     * Получение списка компаний
     * @param query запрос
     * @return список компаний
     */
    SearchResult<Company> getCompanies(CompanyQuery query ) throws RequestFailedException;

    /**
     * Получение списка групп компаний
     * @param searchPattern шаблон поиска
     * @return список групп
     */
    List<CompanyGroup> getCompanyGroups( String searchPattern ) throws RequestFailedException;

    /**
     * Сохранение компании ( создание + изменение )
     * @param company компания
     * @return результат сохранения
     */
    Boolean saveCompany ( Company company ) throws RequestFailedException;

    /**
     * Проверка уникальности названия компании
     * @param name название компании
     * @param excludeId идентификатор компании, которую необходимо исключить из проверки
     * @return true/false
     */
    Boolean isCompanyNameExists ( String name, Long excludeId ) throws RequestFailedException;

    /**
     * Проверка уникальности названия группы компаний
     * @param name название компании
     * @param excludeId идентификатор группы, которую необходимо исключить из проверки
     * @return true/false
     */
    Boolean isGroupNameExists ( String name, Long excludeId ) throws RequestFailedException;

    /**
     * Получение компании
     * @param id идентификатор компании
     * @return Company
     */
    Company getCompany( long id ) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления компании (name,id)
     * @return
     */
    List< EntityOption > getCompanyOptionList(CompanyQuery query) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления группы компаний (name,id)
     * @return
     */
    List< EntityOption > getGroupOptionList() throws RequestFailedException;

    /**
     * Получение списка сокращенного представления катогирии компаний (name,id)
     * @return
     */
    List< EntityOption > getCategoryOptionList() throws RequestFailedException;

    /**
     * Обновить адреса подписок своей компании
     *
     * @param value
     */
    List<CompanySubscription> updateSelfCompanySubscription( List< CompanySubscription > value ) throws RequestFailedException;

    /**
     * Получение список рассылок по компании
     *
     * @param companyId
     */
    List<CompanySubscription> getCompanySubscription( Long companyId ) throws RequestFailedException;

    List< CompanySubscription > getCompanyWithParentCompanySubscriptions( Long companyId ) throws RequestFailedException;

    /**
     * Получить список доступных статусов обращения
     */
    List<CaseState> getCompanyCaseStates(Long id) throws RequestFailedException;

}
