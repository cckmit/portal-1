package ru.protei.portal.ui.group.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CompanyGroup;

import java.util.List;

/**
 * Асинхронный сервис по работе с группами компаний
 */
public interface CompanyGroupServiceAsync {

    /**
     * Получение списка групп компаний
     * @param param шаблон поиска
     * @return список компаний
     */
    void getCompanyGroups( String param, AsyncCallback<List<CompanyGroup>> async );

}
