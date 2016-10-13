package ru.protei.portal.ui.company.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;

import java.util.List;

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
    void getCompanies( String searchPattern, CompanyGroup group, En_SortField sortField, Boolean dirSort, AsyncCallback< List< Company > > async );

    /**
     * Получение списка групп компаний
     * @param searchPattern шаблон поиска
     * @return список компаний
     */
    void getCompanyGroups( String searchPattern, AsyncCallback<List < CompanyGroup > > async );

}
