package ru.protei.portal.ui.crm.client.activity.app;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by frost on 9/23/16.
 */
public interface AbstractAppView extends IsWidget {
    void setActivity( AbstractAppActivity activity );

    void setUsername( String username, String role );

    void setPanelName( String panelName);

    HasWidgets getDetailsContainer();

    HasWidgets getNotifyContainer();

    void setFocus();
}
