package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.struct.MarkedResult;

import java.util.List;

/**
 * Асинхронный сервис управления учетными записями
 */
public interface AccountControllerAsync {

    /**
     * Получение списка учетных записей
     * @param query запрос
     * @return список учетных записей
     */
    void getAccounts( AccountQuery query, long marker, AsyncCallback< MarkedResult< List< UserLogin > > > async );

    void getAccount ( long id, AsyncCallback< UserLogin > callback );

    void getContactAccount (long personId, AsyncCallback< UserLogin > async );

    void saveAccount ( UserLogin userLogin, Boolean sendWelcomeEmail, AsyncCallback< UserLogin > callback );

    void getAccountsCount( AccountQuery query, long marker, AsyncCallback< MarkedResult< Long > > async );

    void isLoginUnique( String login, Long exceptId, AsyncCallback< Boolean > async );

    void removeAccount( Long accountId, AsyncCallback< Boolean > async );

}
