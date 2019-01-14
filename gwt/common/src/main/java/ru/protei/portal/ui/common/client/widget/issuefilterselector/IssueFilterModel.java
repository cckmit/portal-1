package ru.protei.portal.ui.common.client.widget.issuefilterselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class IssueFilterModel implements Activity {

    @Event
    public void onChangeUserFilterModel( IssueEvents.ChangeUserFilterModel event ) {
        requestFilters( null );
    }

    public void subscribe( SelectorWithModel< CaseFilterShortView > selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    public void requestFilters( SelectorWithModel< CaseFilterShortView > selector ) {

        filterService.getIssueFilterShortViewListByCurrentUser( new RequestCallback< List< CaseFilterShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< CaseFilterShortView > options ) {
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
        for ( SelectorWithModel< CaseFilterShortView > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    @Inject
    IssueFilterControllerAsync filterService;

    @Inject
    Lang lang;

    private List< CaseFilterShortView > list = new ArrayList<>();

    List<SelectorWithModel< CaseFilterShortView >> subscribers = new ArrayList<>();
}
