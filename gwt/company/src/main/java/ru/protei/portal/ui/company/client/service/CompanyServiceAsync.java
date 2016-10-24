package ru.protei.portal.ui.company.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Set;

/**
 * Асинхронный сервис по работе с компаниями
 */
public interface CompanyServiceAsync {

    /**
     * Получение списка компаний
     * @param searchPattern шаблон поиска
     * @param group группа компаний
     * @param  sortField поле для сортировки
     * @return список компаний
     */
    void getCompanies( String searchPattern, Set< CompanyCategory > categories, CompanyGroup group, En_SortField sortField, Boolean dirSort, AsyncCallback< List< Company > > async );

    /**
     * Получение списка групп компаний
     * @param searchPattern шаблон поиска
     * @return список групп
     */
    void getCompanyGroups( String searchPattern, AsyncCallback<List < CompanyGroup > > async );

    /**
     * Получение списка категорий компаний
     * @return список категорий
     */
    void getCompanyCategories( AsyncCallback<List <CompanyCategory> > async  );

    /**
     * Сохранение компании
     * @return результат сохранения
     */
    void saveCompany ( Company company, AsyncCallback< Boolean > async );

}
