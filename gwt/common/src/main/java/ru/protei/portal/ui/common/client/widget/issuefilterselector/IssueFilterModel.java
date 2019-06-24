package ru.protei.portal.ui.common.client.widget.issuefilterselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class IssueFilterModel implements Activity {

    public void updateFilterType( SelectorWithModel< CaseFilterShortView > selector, En_CaseFilterType filterType ) {
        filterService.getIssueFilterShortViewList( filterType, new RequestCallback< List< CaseFilterShortView > >() {
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
}
