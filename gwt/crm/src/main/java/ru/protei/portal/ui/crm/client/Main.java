package ru.protei.portal.ui.crm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import ru.protei.portal.ui.crm.client.events.AuthEvents;
import ru.protei.portal.ui.crm.client.factory.ClientFactory;

import java.util.logging.Logger;

/**
 * Точка входа
 */
public class Main implements EntryPoint {

    public void onModuleLoad() {
        ClientFactory factory = GWT.create( ClientFactory.class);
        factory.getAuthActivity ().fireEvent ( new AuthEvents.Show (RootPanel.get ()));
    }

    private static final Logger log = Logger.getLogger( Main.class.getName() );
}
