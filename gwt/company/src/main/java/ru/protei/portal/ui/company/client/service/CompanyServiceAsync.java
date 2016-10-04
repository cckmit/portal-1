package ru.protei.portal.ui.company.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Company;

import java.util.List;

/**
 * Асинхронный сервис по работе с компаниями
 */
public interface CompanyServiceAsync {

    /**
     * Получение списка компаний
     * @param param шаблон поиска
     * @return список компаний
     */
    void getCompanies( String param, AsyncCallback<List<Company>> async );
}
