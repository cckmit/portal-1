package ru.protei.portal.ui.product.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterActivity;
import ru.protei.portal.ui.product.client.activity.filter.AbstractProductFilterView;
import ru.protei.portal.ui.product.client.widget.type.ProductTypeBtnGroupMulti;

import java.util.Set;

/**
 * Представление фильтра продуктов
 */
public class ProductFilterView extends Composite implements AbstractProductFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractProductFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<Boolean> showDeprecated() { return showDeprecated; }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public HasValue<Set<En_DevUnitType>> types() {
        return types;
    }

    @Override
    public HasValue<ProductDirectionInfo> direction() {
        return direction;
    }

    @Override
    public void resetFilter() {
        showDeprecated.setValue( false );
        sortField.setValue( En_SortField.prod_name );
        sortDir.setValue( true );
        types.setValue( null );
        search.setValue( "" );
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "showDeprecated" )
    public void onShowDeprecatedClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "types" )
    public void onTypeSelected( ValueChangeEvent<Set<En_DevUnitType>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("direction")
    public void onDirectionChanged(ValueChangeEvent<ProductDirectionInfo> event) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    private void ensureDebugIds() {
        search.setEnsureDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setEnsureDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);

        showDeprecated.ensureDebugId(DebugIds.PRODUCT_TABLE.FILTER.SHOW_DEPRECATED);
        types.ensureDebugId(DebugIds.PRODUCT_TABLE.FILTER.TYPES);

        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    @UiField
    CheckBox showDeprecated;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    CleanableSearchBox search;

    @Inject
    @UiField(provided = true)
    ProductDirectionButtonSelector direction;

    @UiField
    Button resetBtn;

    @Inject
    @UiField(provided = true)
    ProductTypeBtnGroupMulti types;

    @Inject
    @UiField
    Lang lang;


    AbstractProductFilterActivity activity;

    private static ProductFilterView.ProductFilterViewUiBinder ourUiBinder = GWT.create( ProductFilterView.ProductFilterViewUiBinder.class );
    interface ProductFilterViewUiBinder extends UiBinder<HTMLPanel, ProductFilterView > {}

}