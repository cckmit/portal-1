package ru.protei.portal.ui.crm.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import ru.protei.portal.ui.crm.client.factory.ClientFactory;

import java.util.logging.Logger;

/**
 * Точка входа
 */
public class Main implements EntryPoint {

    public void onModuleLoad() {
        ClientFactory factory = GWT.create( ClientFactory.class);

        Window.alert( "Duck!" );
    }

    private static final Logger log = Logger.getLogger( Main.class.getName() );
}
