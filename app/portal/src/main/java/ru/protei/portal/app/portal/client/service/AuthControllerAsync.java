package ru.protei.portal.app.portal.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * Сервис авторизации
 */
public interface AuthControllerAsync {
    /**
     * Авторизация пользователя
     *
     * @param login    имя пользователя (не пустая строка)
     * @param password пароль (не пустая строка)
     * @return профиль пользователя
     */
    void authentificate( String login, String password, AsyncCallback< Profile > async );

    /**
     * Выход пользователя
     */
    void logout( AsyncCallback< Void > async );
}
