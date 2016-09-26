package ru.protei.portal.ui.crm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import ru.protei.portal.ui.crm.client.event.AppEvents;
import ru.protei.portal.ui.crm.client.event.AuthEvents;
import ru.protei.portal.ui.crm.client.factory.ClientFactory;

import java.util.logging.Logger;

/**
 * Точка входа
 */
public class Main implements EntryPoint {

    public void onModuleLoad() {
        ClientFactory factory = GWT.create( ClientFactory.class);

        factory.getAppActivity().fireEvent( new AppEvents.Init( RootPanel.get() ));
        factory.getAppActivity().fireEvent( new AuthEvents.Success( "userName" ) );
        factory.getAppActivity().fireEvent( new AppEvents.Show( ));
    }

    private static final Logger log = Logger.getLogger( Main.class.getName() );
}
