package ru.protei.portal.ui.product.client.activity.list;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterActivity;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterView;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность множества продуктов
 */
public abstract class ProductGridActivity implements AbstractProductGridActivity, AbstractProductFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        filterView.setActivity( this );
        query = makeQuery();
        currentViewType = ViewType.TABLE;
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( ProductEvents.Show event ) {
        fireEvent(new AppEvents.InitPanelName(lang.products()));

        fireEvent(new ActionBarEvents.Clear());
        if(policyService.hasPrivilegeFor( En_Privilege.PRODUCT_CREATE )){
            fireEvent(new ActionBarEvents.Add( lang.buttonCreate(), null, UiConstants.ActionBarIdentity.PRODUCT ));
        }

        boolean isListCurrent = currentViewType == ViewType.LIST;
        fireEvent(new ActionBarEvents.Add(
                isListCurrent? lang.table(): lang.list(),
                isListCurrent? UiConstants.ActionBarIcons.TABLE: UiConstants.ActionBarIcons.LIST,
                UiConstants.ActionBarIdentity.PRODUCT_TYPE_VIEW
        ));

        query = makeQuery();
        fireEvent(new ProductEvents.ShowDefinite(currentViewType, filterView.asWidget(), query));
    }

    @Event
    public void onChangeViewClicked( ActionBarEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.PRODUCT_TYPE_VIEW.equals( event.identity )))
            return;

        currentViewType = currentViewType == ViewType.TABLE? ViewType.LIST: ViewType.TABLE;
        onShow(new ProductEvents.Show());
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.PRODUCT.equals( event.identity )) ) {
            return;
        }

        fireEvent(new ProductEvents.Edit(null));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        fireEvent(new ProductEvents.ShowDefinite(currentViewType, filterView.asWidget(), query));
    }

    private ProductQuery makeQuery() {
        ProductQuery pq = new ProductQuery();
        pq.setSearchString(filterView.searchPattern().getValue());
        pq.setState(filterView.showDeprecated().getValue() ? null : En_DevUnitState.ACTIVE);
        pq.setSortField(filterView.sortField().getValue());
        pq.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        pq.setTypes(filterView.types().getValue());

        return pq;
    }


    @Inject
    AbstractProductFilterView filterView;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private ViewType currentViewType;
    private ProductQuery query;
}