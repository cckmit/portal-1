package ru.protei.portal.ui.ipreservation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.ipreservation.client.activity.edit.AbstractReservedIpEditView;
import ru.protei.portal.ui.ipreservation.client.activity.edit.AbstractSubnetEditView;
import ru.protei.portal.ui.ipreservation.client.activity.edit.ReservedIpEditActivity;
import ru.protei.portal.ui.ipreservation.client.activity.edit.SubnetEditActivity;
import ru.protei.portal.ui.ipreservation.client.activity.table.AbstractReservedIpTableView;
import ru.protei.portal.ui.ipreservation.client.activity.table.ReservedIpTableActivity;
import ru.protei.portal.ui.ipreservation.client.page.IpReservationPage;
import ru.protei.portal.ui.ipreservation.client.view.edit.ReservedIpEditView;
import ru.protei.portal.ui.ipreservation.client.view.edit.SubnetEditView;
import ru.protei.portal.ui.ipreservation.client.view.table.ReservedIpTableView;

/**
 * Описание классов фабрики
 */
public class IpReservationClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind( IpReservationPage.class ).asEagerSingleton();

        bind( ReservedIpTableActivity.class ).asEagerSingleton ();
        bind( AbstractReservedIpTableView.class ).to(ReservedIpTableView.class).in(Singleton.class);

        bind( SubnetEditActivity.class ).asEagerSingleton();
        bind( AbstractSubnetEditView.class ).to(SubnetEditView.class).in(Singleton.class);

        bind( ReservedIpEditActivity.class ).asEagerSingleton();
        bind( AbstractReservedIpEditView.class ).to(ReservedIpEditView.class).in(Singleton.class);

/*
        bind( ReservedIpCreateActivity.class ).asEagerSingleton();
        bind( AbstractReseredIpCreateView.class ).to(ReseredIpCreateView.class).in(Singleton.class);

        bind( ReservedIpPreviewActivity.class ).asEagerSingleton();
        bind( AbstractReservedIpPreviewView.class ).to( ReservedIpPreviewView.class ).in( Singleton.class );*/
    }
}

