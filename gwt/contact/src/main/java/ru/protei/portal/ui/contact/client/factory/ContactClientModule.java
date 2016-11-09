package ru.protei.portal.ui.contact.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.contact.client.activity.edit.AbstractContactEditView;
import ru.protei.portal.ui.contact.client.activity.edit.ContactEditActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;
import ru.protei.portal.ui.contact.client.activity.table.ContactTableActivity;
import ru.protei.portal.ui.contact.client.view.edit.ContactEditView;
import ru.protei.portal.ui.contact.client.view.table.ContactTableView;


/**
 * Описание классов фабрики
 */
public class ContactClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind ( ContactTableActivity.class ).asEagerSingleton();
        bind ( AbstractContactTableView.class ).to( ContactTableView.class ).in( Singleton.class );

        bind( ContactEditActivity.class ).asEagerSingleton();
        bind ( AbstractContactEditView.class ).to(ContactEditView.class).in(Singleton.class);

    }
}

