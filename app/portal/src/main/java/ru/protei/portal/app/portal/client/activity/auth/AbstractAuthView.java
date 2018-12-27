package ru.protei.portal.app.portal.client.activity.auth;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by turik on 23.09.16.
 */
public interface AbstractAuthView extends IsWidget {

    void setActivity( AbstractAuthActivity activity );

    String getUserName();
    void setUserName(String userName);

    String getPassword();
    void setPassword(String password);

    void setFocus();

    void showError(String msg);

    void hideError();


    void reset();
}