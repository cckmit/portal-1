package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления учетными записями
 */
@RemoteServiceRelativePath( "springGwtServices/AccountController" )
public interface AccountController extends RemoteService {

    SearchResult<UserLogin> getAccounts(AccountQuery query) throws RequestFailedException;

    List<UserLoginShortView> getUserLoginShortViewList(UserLoginShortViewQuery query) throws RequestFailedException;

    UserLogin getAccount (long id ) throws RequestFailedException;

    List<UserLogin> getContactAccount (long personId ) throws RequestFailedException;

    UserLogin saveAccount ( UserLogin userLogin, Boolean sendWelcomeEmail ) throws RequestFailedException;

    boolean isLoginUnique( String login, Long exceptId ) throws RequestFailedException;

    Long removeAccount( Long accountId ) throws RequestFailedException;

    void updateAccountPassword(Long loginId, String currentPassword, String newPassword) throws RequestFailedException;

    String getLoginByPersonId(Long personId) throws RequestFailedException;
}
