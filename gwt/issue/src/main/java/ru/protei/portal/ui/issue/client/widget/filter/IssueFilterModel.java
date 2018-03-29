package ru.protei.portal.ui.issue.client.widget.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.IssueFilterShortView;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class IssueFilterModel implements Activity {

    @Event
    public void onChangeUserFilterModel( IssueEvents.ChangeUserFilterModel event ) {
        requestFilters( null );
    }

    public void subscribe( ModelSelector< IssueFilterShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    public void requestFilters( ModelSelector< IssueFilterShortView > selector ) {

        filterService.getIssueFilterShortViewListByCurrentUser( new RequestCallback< List< IssueFilterShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< IssueFilterShortView > options ) {
                list.clear();
                list.addAll( options );

                if (selector == null){
                    notifySubscribers();
                } else {
                    selector.fillOptions( list );
                }
            }
        } );
    }

    private void notifySubscribers() {
        for ( ModelSelector< IssueFilterShortView > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    @Inject
    IssueFilterServiceAsync filterService;

    @Inject
    Lang lang;

    private List< IssueFilterShortView > list = new ArrayList<>();

    List< ModelSelector< IssueFilterShortView > > subscribers = new ArrayList<>();
}
