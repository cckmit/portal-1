package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с компаниями
 */
@RemoteServiceRelativePath( "springGwtServices/CompanyService" )
public interface CompanyService extends RemoteService {

    /**
     * Получение списка компаний
     * @param query запрос
     * @return список компаний
     */
    List<Company> getCompanies( CompanyQuery query ) throws RequestFailedException;

    /**
     * Получение списка групп компаний
     * @param searchPattern шаблон поиска
     * @return список групп
     */
    List<CompanyGroup> getCompanyGroups( String searchPattern ) throws RequestFailedException;

    /**
     * Получение списка категорий компаний
     * @return список категорий
     */
    List<CompanyCategory> getCompanyCategories() throws RequestFailedException;

    /**
     * Сохранение компании ( создание + изменение )
     * @param company компания
     * @param group группа
     * @return результат сохранения
     */
    Boolean saveCompany ( Company company, CompanyGroup group ) throws RequestFailedException;

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
    Company getCompanyById ( long id ) throws RequestFailedException;

}
