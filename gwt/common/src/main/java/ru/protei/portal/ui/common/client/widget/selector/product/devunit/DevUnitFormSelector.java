package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.components.client.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

/**
 * Button селектор с продуктами
 */
public class DevUnitFormSelector
        extends FormSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model) {
        model.setUnitState(En_DevUnitState.ACTIVE);
        setAsyncSelectorModel(model);
        setSearchEnabled(true);
        setSearchAutoFocus(true);
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );

        setSelectorItemRenderer( value -> value == null ? defaultValue :
                value.getName() + (StringUtils.isEmpty(value.getAliases()) ? "" : " (" + value.getAliases() + ")") );

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

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }
    private String defaultValue = null;
}
