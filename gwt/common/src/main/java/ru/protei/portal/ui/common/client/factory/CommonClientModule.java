package ru.protei.portal.ui.common.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.common.client.activity.actionbar.ActionBarActivity;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemListView;
import ru.protei.portal.ui.common.client.activity.contactitem.AbstractContactItemView;
import ru.protei.portal.ui.common.client.activity.contactitem.ContactItemActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.notify.AbstractNotifyView;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.common.IssueStates;
import ru.protei.portal.ui.common.client.view.contactitem.item.ContactItemView;
import ru.protei.portal.ui.common.client.view.contactitem.list.ContactItemListView;
import ru.protei.portal.ui.common.client.view.dialogdetails.DialogDetailsView;
import ru.protei.portal.ui.common.client.view.notify.NotifyView;
import ru.protei.portal.ui.common.client.view.view.PagerView;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.activity.section.AbstractSectionItemView;
import ru.protei.winter.web.common.client.view.section.SectionItemView;

/**
 * Описание классов фабрики
 */
public class CommonClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind( FixedPositioner.class ).asEagerSingleton();
        bind( ActionBarActivity.class ).asEagerSingleton();
        bind( AbstractSectionItemView.class ).to( SectionItemView.class );

        bind( AbstractDialogDetailsView.class ).to( DialogDetailsView.class ).in( Singleton.class );

        bind( NotifyActivity.class ).asEagerSingleton();
        bind( AbstractNotifyView.class ).to( NotifyView.class );

        bind( ContactItemActivity.class ).asEagerSingleton();
        bind( AbstractContactItemListView.class ).to( ContactItemListView.class );
        bind( AbstractContactItemView.class ).to( ContactItemView.class );
        
        bind( CompanyModel.class ).asEagerSingleton();

        bind( DateFormatter.class ).in( Singleton.class );

        bind( AbstractPagerView.class ).to( PagerView.class );

        bind( IssueStates.class ).asEagerSingleton();

        requestStaticInjection(RequestCallback.class);



    }
}

