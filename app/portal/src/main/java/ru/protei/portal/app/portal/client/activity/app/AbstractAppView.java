package ru.protei.portal.app.portal.client.activity.app;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.app.portal.client.widget.locale.LocaleImage;

/**
 * Created by frost on 9/23/16.
 */
public interface AbstractAppView extends IsWidget {
    void setActivity( AbstractAppActivity activity );

    void setUser( String username, String company, String photo );

    void setAppVersion(String appVersion);

    HasWidgets getDetailsContainer();

    HasWidgets getMenuContainer();

    HasWidgets getNotifyContainer();

    HasWidgets getActionBarContainer();

    HasValue<LocaleImage> locale();

    void setLogoByLocale(String locale);

    void setExternalLinks(String html);

    void clearExternalLinks();

    void showHelp(boolean isShow);
}
