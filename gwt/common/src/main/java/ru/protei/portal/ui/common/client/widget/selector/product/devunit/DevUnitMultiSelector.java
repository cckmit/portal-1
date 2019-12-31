package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

/**
 * Мультиселектор продуктов
 */
public class DevUnitMultiSelector extends InputPopupMultiSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model, Lang lang) {
        this.model = model;
        setAsyncSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setSelectorItemRenderer( option -> option == null ? lang.productWithout() :
                (option.getName() + (HelperFunc.isEmpty( option.getAliases() ) ? "" : " (" + option.getAliases() + ")")) );
    }

    public void setTypes(En_DevUnitType... enDevUnitTypes) {
        if (model != null) {
            model.setUnitState( En_DevUnitState.ACTIVE );
            model.setUnitTypes( enDevUnitTypes);
        }
    }

    @Override
    protected SelectorItem makeSelectorItem( ProductShortView element, String elementHtml ) {
        PopupSelectableItem item = new PopupSelectableItem();
        item.setElementHtml( elementHtml );
        if (hasNullValue() && element == null) {
            element = new ProductShortView( CrmConstants.Product.UNDEFINED, lang.productWithout(), 0 );
        }
        item.setSelected( isSelected( element ) );

        return item;
    }

    @Inject
    private Lang lang;

    protected ProductModel model;
}
