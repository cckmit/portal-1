package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

/**
 * Button селектор с продуктами
 */
public class DevUnitButtonSelector extends ButtonPopupSingleSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model) {
        this.model = model;
        model.setUnitState(En_DevUnitState.ACTIVE);
        setAsyncModel(model);

        setItemRenderer( value -> value == null ? defaultValue :
                value.getName() + ( HelperFunc.isEmpty(value.getAliases()) ? "" : " (" + value.getAliases() + ")") );
    }

    @Override
    protected SelectorItem makeSelectorItem( ProductShortView value, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        if(value!=null){
            item.setIcon( En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "not-active" : "" );
            item.setIcon( En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "fa fa-ban ban" : "" );
        }
        return item;
    }

    public void updateQuery(En_DevUnitState enDevUnitState, En_DevUnitType... enDevUnitTypes) {
        if (model != null) {
            model.setUnitState(enDevUnitState);
            model.setUnitTypes(enDevUnitTypes);
        }
    }

    public void updateQuery(ProductQuery productQuery) {
        model.setQuery(productQuery);
    }

    protected ProductModel model;
}
