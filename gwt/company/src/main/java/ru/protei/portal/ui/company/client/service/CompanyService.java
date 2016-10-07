package ru.protei.portal.ui.company.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с компаниями
 */
@RemoteServiceRelativePath ("springGwtServices/CompanyService")
public interface CompanyService extends RemoteService {

    /**
     * Получение списка компаний
     * @param param шаблон поиска
     * @return список компаний
     */
    List<Company> getCompanies( String param ) throws RequestFailedException;
}
