package ru.protei.portal.ui.company.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с компаниями
 */
@RemoteServiceRelativePath ("springGwtServices/CompanyService")
public interface CompanyService extends RemoteService {

    /**
     * Получение списка компаний
     * @param searchPattern шаблон поиска
     * @param group группа компаний
     * @param sortField поле для сортировки
     * @return список компаний
     */
    List<Company> getCompanies( String searchPattern, CompanyGroup group, En_SortField sortField ) throws RequestFailedException;

    /**
     * Получение списка групп компаний
     * @param searchPattern шаблон поиска
     * @return список компаний
     */
    List<CompanyGroup> getCompanyGroups( String searchPattern ) throws RequestFailedException;

}
