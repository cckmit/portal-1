package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Модель селектора компаний
 */
public abstract class CompanyParentModel implements Activity, SelectorModel<EntityOption> {
    @Event
    public void onInit( AuthEvents.Success event ) {
        for (SelectorWithModel<EntityOption> subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }

    @Event
    public void onCompanyListChanged( CompanyEvents.ChangeModel event ) {
        for (SelectorWithModel<EntityOption> subscriber : subscribers) {
            subscriber.clearOptions();
        }
    }

    @Override
    public void onSelectorLoad( SelectorWithModel<EntityOption> selector ) {
        if (selector == null) {
            return;
        }
        subscribers.add( selector );
        if (selector.getValues() == null || selector.getValues().isEmpty()) {
            requestOptions( selector );
        }
    }

    @Override
    public void onSelectorUnload( SelectorWithModel<EntityOption> selector ) {
        if (selector == null) {
            return;
        }
        selector.clearOptions();
    }

    private void requestOptions( SelectorWithModel<EntityOption> selector ) {
        companyService.getCompanyOptionList( query, new RequestCallback<List<EntityOption>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( List<EntityOption> options ) {
                selector.fillOptions( options );
                selector.refreshValue();
            }
        } );
    }

    @Inject
    CompanyControllerAsync companyService;

    @Inject
    Lang lang;

    private CompanyQuery query = new CompanyQuery();

    {
        query.setCategoryIds( Arrays.asList(
                En_CompanyCategory.CUSTOMER.getId(),
                En_CompanyCategory.PARTNER.getId(),
                En_CompanyCategory.SUBCONTRACTOR.getId(),
                En_CompanyCategory.HOME.getId() ) );
        query.setParentIdIsNull( true );
        query.setSortHomeCompaniesAtBegin( true );
    }

    private List<SelectorWithModel<EntityOption>> subscribers = new ArrayList<>();
}
