package ru.protei.portal.ui.common.client.activity.loading;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.LoadingEvents;

import java.util.HashMap;
import java.util.Map;

/**
 * Активность в виджете загрузки
 */
public abstract class LoadingActivity
        implements AbstractLoadingActivity, Activity {

    @Event
    public void onShowLoading( LoadingEvents.Show event ) {
        AbstractLoadingView view = provider.get();
        view.setActivity(this);

        parentToView.put( event.parent, view );
        event.parent.add( view.asWidget() );
    }

    @Event
    public void onHideLoading( LoadingEvents.Hide event ) {
        AbstractLoadingView view = parentToView.remove( event.parent );
        if ( view == null ) {
            return;
        }

        view.asWidget().removeFromParent();
    }

    @Inject
    Provider<AbstractLoadingView> provider;

    private Map<HasWidgets, AbstractLoadingView> parentToView = new HashMap<>();
}
