package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Асинхронный сервис управления учетными записями
 */
public interface AccountControllerAsync {

    void getAccounts(AccountQuery query, AsyncCallback<SearchResult<UserLogin>> async);

    void getAccount ( long id, AsyncCallback< UserLogin > callback );

    void getContactAccount (long personId, AsyncCallback<List<UserLogin>> async );

    void saveAccount ( UserLogin userLogin, Boolean sendWelcomeEmail, AsyncCallback< UserLogin > callback );

    void isLoginUnique( String login, Long exceptId, AsyncCallback< Boolean > async );

    void removeAccount( Long accountId, AsyncCallback< Long > async );

    void updateAccountPassword(Long loginId, String currentPassword, String newPassword, AsyncCallback<Void> async);

    void getUserLoginShortViewList(UserLoginShortViewQuery query, AsyncCallback<List<UserLoginShortView>> async);

    void getLoginByPersonId(Long personId, AsyncCallback<String> async);
}
