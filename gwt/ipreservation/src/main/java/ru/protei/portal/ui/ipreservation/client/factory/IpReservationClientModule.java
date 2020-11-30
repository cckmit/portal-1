package ru.protei.portal.ui.ipreservation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.create.AbstractReservedIpCreateView;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.create.ReservedIpCreateActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.edit.AbstractReservedIpEditView;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.edit.AbstractSubnetEditView;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.edit.ReservedIpEditActivity;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.edit.SubnetEditActivity;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.filter.AbstractReservedIpFilterView;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.table.AbstractReservedIpTableView;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.filter.AbstractSubnetFilterView;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.table.AbstractSubnetTableView;
import ru.protei.portal.ui.ipreservation.client.activity.reservedip.table.ReservedIpTableActivity;
import ru.protei.portal.ui.ipreservation.client.activity.subnet.table.SubnetTableActivity;
import ru.protei.portal.ui.ipreservation.client.page.IpReservationPage;
import ru.protei.portal.ui.ipreservation.client.page.ReservedIpPage;
import ru.protei.portal.ui.ipreservation.client.page.SubnetPage;
import ru.protei.portal.ui.ipreservation.client.view.reservedip.create.ReservedIpCreateView;
import ru.protei.portal.ui.ipreservation.client.view.reservedip.edit.ReservedIpEditView;
import ru.protei.portal.ui.ipreservation.client.view.subnet.edit.SubnetEditView;
import ru.protei.portal.ui.ipreservation.client.view.reservedip.filter.ReservedIpFilterView;
import ru.protei.portal.ui.ipreservation.client.view.reservedip.table.ReservedIpTableView;
import ru.protei.portal.ui.ipreservation.client.view.subnet.filter.SubnetFilterView;
import ru.protei.portal.ui.ipreservation.client.view.subnet.table.SubnetTableView;

/**
 * Описание классов фабрики
 */
public class IpReservationClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind( IpReservationPage.class ).asEagerSingleton();
        bind( ReservedIpPage.class ).asEagerSingleton();
        bind( SubnetPage.class ).asEagerSingleton();

        bind( ReservedIpTableActivity.class ).asEagerSingleton ();
        bind( AbstractReservedIpTableView.class ).to(ReservedIpTableView.class).in(Singleton.class);

        bind( SubnetTableActivity.class ).asEagerSingleton ();
        bind( AbstractSubnetTableView.class ).to(SubnetTableView.class).in(Singleton.class);

        bind(AbstractReservedIpFilterView.class).to(ReservedIpFilterView.class).in(Singleton.class);
        bind(AbstractSubnetFilterView.class).to(SubnetFilterView.class).in(Singleton.class);

        bind( SubnetEditActivity.class ).asEagerSingleton();
        bind( AbstractSubnetEditView.class ).to(SubnetEditView.class).in(Singleton.class);

        bind( ReservedIpCreateActivity.class ).asEagerSingleton();
        bind( AbstractReservedIpCreateView.class ).to(ReservedIpCreateView.class).in(Singleton.class);

        bind( ReservedIpEditActivity.class ).asEagerSingleton();
        bind( AbstractReservedIpEditView.class ).to(ReservedIpEditView.class).in(Singleton.class);
    }
}

