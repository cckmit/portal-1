package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

/**
 * Мультиселектор продуктов
 */
public class DevUnitMultiSelector extends InputPopupMultiSelector<ProductShortView> {

    @Inject
    public void init( ProductModel model, Lang lang ) {
        this.model = model;
        setAsyncModel( model );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
        model.setUnitState( En_DevUnitState.ACTIVE );
        setItemRenderer( option -> makeOptionName( option ) );
        setNullItem( () -> new ProductShortView( CrmConstants.Product.UNDEFINED, lang.productWithout(), 0 ) );
    }

    public void setTypes( En_DevUnitType... enDevUnitTypes ) {
        model.setUnitTypes( enDevUnitTypes );
    }

    public void setState( En_DevUnitState enDevUnitState ) {
        model.setUnitState( enDevUnitState );
    }

    protected String makeOptionName( ProductShortView productShortView ) {
        return sanitizeHtml(productShortView.getName()
                + (HelperFunc.isEmpty( productShortView.getAliases() ) ? "" : " (" + productShortView.getAliases() + ")"));
    }

    private ProductModel model;
}
