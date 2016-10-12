package ru.protei.portal.ui.group.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с группами компаний
 */
@RemoteServiceRelativePath("springGwtServices/CompanyGroupService")
public interface CompanyGroupService extends RemoteService {

    /**
     * Получение списка групп компаний
     * @param param шаблон поиска
     * @return список компаний
     */
    List<CompanyGroup> getCompanyGroups( String param ) throws RequestFailedException;

}
