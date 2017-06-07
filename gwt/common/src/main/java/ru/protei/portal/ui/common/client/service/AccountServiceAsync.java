package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;

import java.util.List;

/**
 * Асинхронный сервис управления учетными записями
 */
public interface AccountServiceAsync {

    /**
     * Получение списка учетных записей
     * @param query запрос
     * @return список учетных записей
     */
    void getAccounts( AccountQuery query, AsyncCallback< List< UserLogin > > async );

    void getAccount ( long id, AsyncCallback< UserLogin > callback );

    void saveAccount ( UserLogin userLogin, AsyncCallback< UserLogin > callback );

    void getAccountsCount( AccountQuery query, AsyncCallback< Long > async );

    void isLoginUnique( String login, Long exceptId, AsyncCallback< Boolean > async );
}
