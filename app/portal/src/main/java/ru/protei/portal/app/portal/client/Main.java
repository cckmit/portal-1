package ru.protei.portal.app.portal.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.user.client.ui.RootPanel;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.app.portal.client.factory.ClientFactory;
import ru.protei.portal.ui.webts.client.TypescriptWebEntryPoint;

import java.util.logging.Logger;

/**
 * Точка входа
 */
public class Main implements EntryPoint {

    public void onModuleLoad() {
        DebugInfo.setDebugIdPrefix(DebugIds.DEBUG_ID_PREFIX);

        ClientFactory factory = GWT.create(ClientFactory.class);
        TypescriptWebEntryPoint.setFactoryReference(factory.getAppActivity());
        factory.getAppActivity().fireEvent( new AuthEvents.Init( RootPanel.get() ));
        factory.getAppActivity().fireEvent( new AppEvents.Init( RootPanel.get() ));
    }

    private static final Logger log = Logger.getLogger( Main.class.getName() );
}
