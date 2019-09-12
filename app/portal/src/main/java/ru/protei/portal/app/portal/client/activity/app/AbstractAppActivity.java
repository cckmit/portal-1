package ru.protei.portal.app.portal.client.activity.app;

/**
 * Created by frost on 9/23/16.
 */
public interface AbstractAppActivity {

    void onLogoutClicked();

    void onLogoClicked();

    void onLocaleChanged(String locale);

    void onSettingsClicked();
}
