package ru.protei.portal.ui.common.client.widget.issuefilterselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IssueFilterModel implements Activity {

    @Event
    public void onChangeUserFilterModel( IssueEvents.ChangeUserFilterModel event ) {
        for ( SelectorWithModel<FilterShortView> subscriber : selectorToType.keySet() ) {
            requestFilters( subscriber, selectorToType.get( subscriber ) );
        }
    }

    public void updateFilterType( SelectorWithModel< FilterShortView > selector, En_CaseFilterType filterType ) {
        selectorToType.put( selector, filterType );
        requestFilters( selector, filterType );
    }

    private void requestFilters( SelectorWithModel< FilterShortView > selector, En_CaseFilterType filterType ) {
        filterService.getCaseFilterShortViewList( filterType, new RequestCallback< List< FilterShortView > >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List< FilterShortView > options ) {
                selector.fillOptions( options );
                selector.refreshValue();
            }
        } );
    }

    @Inject
    CaseFilterControllerAsync filterService;

    @Inject
    Lang lang;

    private Map< SelectorWithModel< FilterShortView >, En_CaseFilterType > selectorToType = new HashMap<>();
}
