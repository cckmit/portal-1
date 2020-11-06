package ru.protei.portal.app.portal.client.activity.auth;

/**
 * Created by turik on 23.09.16.
 */
public interface AbstractAuthActivity {
    void onLocaleChanged(String locale);
    void onLoginClicked();
    void onWindowsFocus();
}
