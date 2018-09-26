package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.ui.crm.client.widget.localeselector.LocaleImage;

/**
 * Created by frost on 9/23/16.
 */
public interface AbstractAppView extends IsWidget {
    void setActivity( AbstractAppActivity activity );

    void setUser( String username, String company, String iconSrc );

    void setAppVersion(String appVersion);

    HasWidgets getDetailsContainer();

    HasWidgets getMenuContainer();

    HasWidgets getNotifyContainer();

    HasWidgets getActionBarContainer();

    HasValue<LocaleImage> locale();
}
