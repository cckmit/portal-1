package ru.protei.portal.ui.ipreservation.client.view.widget.selector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.IpReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class SubnetModel extends BaseSelectorModel<SubnetOption> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
    }

    @Event
    public void onSubnetListChanged(IpReservationEvents.ChangeModel event) {
        clean();
    }

    @Override
    protected void requestData( LoadingHandler selector, String searchText ) {
        ipReservationController.getSubnetsOptionList(new ReservedIpQuery(searchText, En_SortField.address, En_SortDir.ASC),
                new FluentCallback<List<SubnetOption>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess( result -> updateElements( result, selector ) ));
    }

    @Inject
    IpReservationControllerAsync ipReservationController;
    @Inject
    Lang lang;
}
