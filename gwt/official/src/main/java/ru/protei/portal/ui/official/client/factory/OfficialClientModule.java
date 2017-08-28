package ru.protei.portal.ui.official.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.official.client.activity.filter.AbstractOfficialFilterView;
import ru.protei.portal.ui.official.client.activity.page.OfficialPage;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialItemView;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialListView;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialPreviewView;
import ru.protei.portal.ui.official.client.activity.preview.OfficialPreviewActivity;
import ru.protei.portal.ui.official.client.activity.table.AbstractOfficialTableView;
import ru.protei.portal.ui.official.client.activity.table.OfficialTableActivity;
import ru.protei.portal.ui.official.client.view.filter.OfficialFilterView;
import ru.protei.portal.ui.official.client.view.preview.OfficialItemView;
import ru.protei.portal.ui.official.client.view.preview.OfficialListView;
import ru.protei.portal.ui.official.client.view.preview.OfficialPreviewView;
import ru.protei.portal.ui.official.client.view.table.OfficialTableView;

/**
 * Описание классов фабрики
 */
public class OfficialClientModule extends AbstractGinModule{
    @Override
    protected void configure() {
        bind(OfficialPage.class).asEagerSingleton();

        bind ( OfficialTableActivity.class ).asEagerSingleton();
        bind ( AbstractOfficialTableView.class ).to( OfficialTableView.class ).in( Singleton.class );

        bind(OfficialPreviewActivity.class).asEagerSingleton();
        bind (AbstractOfficialPreviewView.class ).to(OfficialPreviewView.class ).in( Singleton.class );
        bind (AbstractOfficialListView.class).to(OfficialListView.class);
        bind (AbstractOfficialItemView.class).to(OfficialItemView.class);
        bind ( AbstractOfficialFilterView.class ).to(OfficialFilterView.class ).in( Singleton.class );

    }
}
