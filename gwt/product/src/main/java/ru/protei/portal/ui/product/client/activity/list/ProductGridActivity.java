package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.ui.Widget;
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
import ru.protei.portal.ui.common.client.service.ProductServiceAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterActivity;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterView;

/**
 * Активность множества продуктов
 */
public abstract class ProductGridActivity implements AbstractProductGridActivity, AbstractProductFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        filterView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void init( Runnable onFilterChangedAction, Widget... widgets ) {
        this.fireEvent(new AppEvents.InitPanelName(lang.products()));
        init.parent.clear();

        this.onFilterChangedAction = onFilterChangedAction;
        for(Widget widget: widgets){
            init.parent.add(widget);
        }

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.PRODUCT_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.PRODUCT ) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();
        currentViewType = filterView.viewType().getValue();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Override
    public void onFilterChanged() {
        if(filterView.viewType().getValue() != currentViewType){
            fireEvent(new ProductEvents.Show());
            return;
        }

        query = makeQuery();
        if(onFilterChangedAction != null)
            onFilterChangedAction.run();
    }

    private ProductQuery makeQuery() {
        ProductQuery pq = new ProductQuery();
        pq.setSearchString(filterView.searchPattern().getValue());
        pq.setState(filterView.showDeprecated().getValue() ? null : En_DevUnitState.ACTIVE);
        pq.setSortField(filterView.sortField().getValue());
        pq.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);

        return pq;
    }


    @Inject
    AbstractProductFilterView filterView;
    @Inject
    Lang lang;
    @Inject
    ProductServiceAsync productService;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails init;
    private static Runnable onFilterChangedAction;
    private static String CREATE_ACTION;
    private static ViewType currentViewType;

    protected static ProductQuery query;
}