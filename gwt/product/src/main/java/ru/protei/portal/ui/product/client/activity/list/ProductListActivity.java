package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.animation.PlateListAnimation;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemActivity;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Активность списка продуктов
 */
public abstract class ProductListActivity extends ProductGridActivity implements AbstractProductItemActivity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
    }

    @Event
    public void onShow( ProductEvents.Show event ) {
        if(filterView.viewType().getValue() != ViewType.LIST)
            return;

        init(this::requestProducts, view.asWidget());
        view.getFilterContainer().add(filterView.asWidget());
        requestProducts();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.PRODUCT.equals( event.identity ) && filterView.viewType().getValue() == ViewType.LIST)) {
            return;
        }

        fireEvent(new ProductEvents.Edit(null));
    }

    @Override
    public void onFavoriteClicked(AbstractProductItemView itemView) {
        Window.alert( "On favorite clicked" );
    }

    @Override
    public void onPreviewClicked( AbstractProductItemView itemView ) {
        DevUnit value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent( new ProductEvents.ShowPreview( itemView.getPreviewContainer(), value, false ) );
        animation.showPreview(itemView, (IsWidget) itemView.getPreviewContainer());
    }

    @Override
    public void onEditClicked( AbstractProductItemView itemView ) {
        fireEvent( new ProductEvents.Edit( itemViewToModel.get( itemView ).getId()  ) );
    }

    public void onCreateClicked( ) { fireEvent(new ProductEvents.Edit()); }

    private AbstractProductItemView makeView ( DevUnit product )
    {
        AbstractProductItemView itemView = factory.get();
        itemView.setName(product.getName());
        itemView.setDeprecated(product.getStateId() > 1);
        itemView.setActivity(this);
        itemView.setEditEnabled( policyService.hasPrivilegeFor( En_Privilege.PRODUCT_EDIT ) );

        return itemView;
    }

    private void requestProducts() {
        if ( fillViewHandler != null ) {
            fillViewHandler.cancel();
        }

        view.getChildContainer().clear();
        view.setListCreateBtnVisible(policyService.hasPrivilegeFor( En_Privilege.PRODUCT_CREATE ));
        itemViewToModel.clear();

        productService.getProductList(query,
                new RequestCallback<List<DevUnit>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<DevUnit> result) {
                        fillViewHandler = taskService.startPeriodicTask( result, fillViewer, 50, 50 );
                    }
                });
    }

    Consumer<DevUnit> fillViewer = new Consumer<DevUnit> () {
        @Override
        public void accept( DevUnit product ) {
            AbstractProductItemView itemView = makeView(product);

            itemViewToModel.put( itemView, product );
            view.getChildContainer().add( itemView.asWidget() );
        }
    };

    @Inject
    PlateListAnimation animation;
    @Inject
    AbstractProductListView view;
    @Inject
    Provider<AbstractProductItemView> factory;
    @Inject
    PeriodicTaskService taskService;
    PeriodicTaskService.PeriodicTaskHandler fillViewHandler;

    private Map< AbstractProductItemView, DevUnit> itemViewToModel = new HashMap<AbstractProductItemView, DevUnit>();

}
