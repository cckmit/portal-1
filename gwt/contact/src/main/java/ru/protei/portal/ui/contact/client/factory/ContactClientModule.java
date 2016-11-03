package ru.protei.portal.ui.contact.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.contact.client.activity.preview.AbstractContactPreviewView;
import ru.protei.portal.ui.contact.client.activity.preview.ContactPreviewActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;
import ru.protei.portal.ui.contact.client.activity.table.ContactTableActivity;
import ru.protei.portal.ui.contact.client.view.preview.ContactPreviewView;
import ru.protei.portal.ui.contact.client.view.table.ContactTableView;


/**
 * Описание классов фабрики
 */
public class ContactClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind ( ContactTableActivity.class ).asEagerSingleton();
        bind ( AbstractContactTableView.class ).to( ContactTableView.class ).in( Singleton.class );

        bind( ContactPreviewActivity.class ).asEagerSingleton();
        bind( AbstractContactPreviewView.class ).to( ContactPreviewView.class ).in( Singleton.class );
    }
}

