package ru.protei.portal.app.portal.client.activity.app;

/**
 * Created by frost on 9/23/16.
 */
public interface AbstractAppActivity {

    void onUserClicked();

    void onLogoutClicked();

    void onLocaleChanged( String value );

    void onLogoClicked();
}
