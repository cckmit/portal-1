package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.Set;

/**
 * Button селектор с продуктами
 */
public class DevUnitFormSelector extends FormPopupSingleSelector<ProductShortView> {

    @Inject
    public void init(ProductModel model) {
        this.model = model;
        model.setUnitState(En_DevUnitState.ACTIVE);
        setAsyncModel(model);

        setItemRenderer( value -> value == null ? defaultValue :
                value.getName() + (StringUtils.isEmpty(value.getAliases()) ? "" : " (" + value.getAliases() + ")") );

    }

    @Override
    public void setFilter(Selector.SelectorFilter<ProductShortView> selectorFilter) {
        super.setFilter(selectorFilter);
        this.filter = selectorFilter;
    }

    public void setTypes(En_DevUnitType... enDevUnitTypes) {
        if (model != null) {
            model.setUnitTypes( enDevUnitTypes);
        }
    }

    public void setState(En_DevUnitState enDevUnitState) {
        model.setUnitState(enDevUnitState);
    }

    public void setDirectionId(Long directionId) {
        model.setDirectionId(directionId);
    }

    public void setPlatformIds(Set<Long> platformIds) {
        if (platformIds == null) {
            super.setFilter(product -> false);
            return;
        }

        super.setFilter(filter);
        model.setPlatformIds(platformIds);
    }

    public void setAsyncProductModel(ProductModel productModel) {
        this.model = productModel;
        setAsyncModel(productModel);
    }

    @Override
    protected SelectorItem<ProductShortView> makeSelectorItem(ProductShortView value, String elementHtml, String name) {
        PopupSelectorItem<ProductShortView> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        if (value != null) {
            item.setIcon(En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "not-active" : "");
            item.setIcon(En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "fa fa-ban ban" : "");
        }
        return item;
    }

    private ProductModel model;
    private Selector.SelectorFilter<ProductShortView> filter;
}
