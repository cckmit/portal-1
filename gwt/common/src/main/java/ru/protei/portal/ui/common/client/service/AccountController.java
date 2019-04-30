package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.struct.MarkedResult;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления учетными записями
 */
@RemoteServiceRelativePath( "springGwtServices/AccountController" )
public interface AccountController extends RemoteService {

    /**
     * Получение списка учетных записей
     * @param query запрос
     * @return список учетных записей
     */
    MarkedResult< List< UserLogin > > getAccounts ( AccountQuery query, long marker ) throws RequestFailedException;

    UserLogin getAccount ( long id ) throws RequestFailedException;

    UserLogin getContactAccount (long personId ) throws RequestFailedException;

    UserLogin saveAccount ( UserLogin userLogin, Boolean sendWelcomeEmail ) throws RequestFailedException;

    MarkedResult<Long> getAccountsCount( AccountQuery query, long marker ) throws RequestFailedException;

    boolean isLoginUnique( String login, Long exceptId ) throws RequestFailedException;

    boolean removeAccount( Long accountId ) throws RequestFailedException;
}
