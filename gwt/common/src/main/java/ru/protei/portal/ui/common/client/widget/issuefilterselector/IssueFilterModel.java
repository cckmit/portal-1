package ru.protei.portal.ui.common.client.widget.issuefilterselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IssueFilterModel implements Activity {

    @Event
    public void onChangeUserFilterModel( IssueEvents.ChangeUserFilterModel event ) {
        for ( SelectorWithModel< CaseFilterShortView > subscriber : subscribers ) {
            subscriber.clearOptions();
        }
    }

    public void subscribe( SelectorWithModel< CaseFilterShortView > selector, En_CaseFilterType filterType ) {
        subscribers.add( selector );
        selectorToType.put( selector, filterType );
    }

    public void updateFilterType( SelectorWithModel< CaseFilterShortView > selector, En_CaseFilterType filterType ) {
        selectorToType.put( selector, filterType );
    }

    public void requestFilters( SelectorWithModel< CaseFilterShortView > selector ) {

        filterService.getIssueFilterShortViewList( selectorToType.get( selector ), new RequestCallback< List< CaseFilterShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< CaseFilterShortView > options ) {
                selector.fillOptions( options );
                selector.refreshValue();
            }
        } );
    }

    @Inject
    IssueFilterControllerAsync filterService;

    @Inject
    Lang lang;

    private Map< SelectorWithModel< CaseFilterShortView >, En_CaseFilterType > selectorToType = new HashMap<>();
    List< SelectorWithModel< CaseFilterShortView > > subscribers = new ArrayList<>();
}
