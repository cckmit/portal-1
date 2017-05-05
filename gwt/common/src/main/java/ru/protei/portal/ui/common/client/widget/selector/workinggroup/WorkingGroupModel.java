package ru.protei.portal.ui.common.client.widget.selector.workinggroup;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.WorkingGroup;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductServiceAsync;
import ru.protei.portal.ui.common.client.service.WorkingGroupServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель селектора рабочих групп
 */
public abstract class WorkingGroupModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }


    public void subscribe( ModelSelector<WorkingGroup> selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector< WorkingGroup > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        service.getAllGroups(
                new RequestCallback<List<WorkingGroup>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<WorkingGroup> options ) {
                list.clear();
                list.addAll( options );
                notifySubscribers();
            }
        } );
    }

    @Inject
    WorkingGroupServiceAsync service;

    @Inject
    Lang lang;

    private List< WorkingGroup > list = new ArrayList<>();

    List< ModelSelector< WorkingGroup > > subscribers = new ArrayList<>();
}
