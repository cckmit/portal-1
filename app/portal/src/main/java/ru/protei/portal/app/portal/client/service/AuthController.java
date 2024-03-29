package ru.protei.portal.app.portal.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * Сервис авторизации
 */
@RemoteServiceRelativePath( "springGwtServices/AuthController" )
public interface AuthController extends RemoteService {

    /**
     * Авторизация пользователя
     * @param login       имя пользователя
     * @param password    пароль
     * @return профиль пользователя
     */
    Profile authenticate( String login, String password) throws RequestFailedException;

    /**
     * Выход пользователя
     */
    void logout() throws RequestFailedException;
}
