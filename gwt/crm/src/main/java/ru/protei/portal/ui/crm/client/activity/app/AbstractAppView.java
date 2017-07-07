package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.crm.client.widget.localeselector.LocaleImagesHelper;

import java.util.List;

/**
 * Created by frost on 9/23/16.
 */
public interface AbstractAppView extends IsWidget {
    void setActivity( AbstractAppActivity activity );

    void setUsername( String username, String role );

    HasWidgets getDetailsContainer();

    HasWidgets getMenuContainer();

    HasWidgets getNotifyContainer();

    HasWidgets getActionBarContainer();

    void setLocaleList( List< LocaleImagesHelper.ImageModel > langModelList );

    void setCurrentLocaleLabel( String currentLang );
}
