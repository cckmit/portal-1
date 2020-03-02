package ru.protei.portal.ui.ipreservation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import ru.protei.portal.ui.ipreservation.client.page.IpReservationPage;

/**
 * Описание классов фабрики
 */
public class IpReservationClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind( IpReservationPage.class ).asEagerSingleton();

/*        bind( ReservedIpTableActivity.class ).asEagerSingleton ();
        bind( AbstractReservedIpTableView.class ).to(ReservedIpTableView.class).in(Singleton.class);

        bind( SubnetCreateActivity.class ).asEagerSingleton();
        bind( AbstractSubnetCreateView.class ).to(SubnetCreateView.class).in(Singleton.class);

        bind( SubnetEditActivity.class ).asEagerSingleton();
        bind( AbstractSubnetEditView.class ).to(SubnetEditView.class).in(Singleton.class);

        bind( ReservedIpCreateActivity.class ).asEagerSingleton();
        bind( AbstractReseredIpCreateView.class ).to(ReseredIpCreateView.class).in(Singleton.class);

        bind( ReservedIpEditActivity.class ).asEagerSingleton();
        bind( AbstractReservedIpEditView.class ).to(ReservedIpEditView.class).in(Singleton.class);

        bind( AbstractIpReservationFilterView.class ).to( IpReservationFilterView.class ).in( Singleton.class );*/

/*        bind( ReservedIpPreviewActivity.class ).asEagerSingleton();
        bind( AbstractReservedIpPreviewView.class ).to( ReservedIpPreviewView.class ).in( Singleton.class );*/
    }
}

