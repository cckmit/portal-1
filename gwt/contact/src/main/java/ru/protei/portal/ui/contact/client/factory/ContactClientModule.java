package ru.protei.portal.ui.contact.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.contact.client.activity.edit.AbstractContactEditView;
import ru.protei.portal.ui.contact.client.activity.edit.ContactEditActivity;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterView;
import ru.protei.portal.ui.contact.client.activity.page.ContactPage;
import ru.protei.portal.ui.contact.client.activity.preview.AbstractContactPreviewView;
import ru.protei.portal.ui.contact.client.activity.preview.ContactPreviewActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;
import ru.protei.portal.ui.contact.client.activity.table.ContactTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.concise.AbstractContactConciseTableView;
import ru.protei.portal.ui.contact.client.activity.table.concise.ContactConciseTableActivity;
import ru.protei.portal.ui.contact.client.view.edit.ContactEditView;
import ru.protei.portal.ui.contact.client.view.filter.ContactFilterView;
import ru.protei.portal.ui.contact.client.view.preview.ContactPreviewView;
import ru.protei.portal.ui.contact.client.view.table.ContactTableView;
import ru.protei.portal.ui.contact.client.view.table.concise.ContactConciseTableView;


/**
 * Описание классов фабрики
 */
public class ContactClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( ContactPage.class ).asEagerSingleton();

        bind ( ContactTableActivity.class ).asEagerSingleton();
        bind ( AbstractContactTableView.class ).to( ContactTableView.class ).in( Singleton.class );

        bind ( ContactConciseTableActivity.class ).asEagerSingleton();
        bind ( AbstractContactConciseTableView.class ).to( ContactConciseTableView.class ).in( Singleton.class );

        bind( ContactPreviewActivity.class ).asEagerSingleton();
        bind( AbstractContactPreviewView.class ).to( ContactPreviewView.class ).in( Singleton.class );

        bind( ContactEditActivity.class ).asEagerSingleton();
        bind ( AbstractContactEditView.class ).to(ContactEditView.class).in(Singleton.class);

        bind( AbstractContactFilterView.class ).to( ContactFilterView.class ).in( Singleton.class );
    }
}

