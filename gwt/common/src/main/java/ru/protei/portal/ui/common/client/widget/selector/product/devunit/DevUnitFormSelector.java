package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

/**
 * Button селектор с продуктами
 */
public class DevUnitFormSelector extends FormSelector<ProductShortView> implements SelectorWithModel<ProductShortView> {

    @Inject
    public void init(ProductModel model) {
        this.model = model;
        model.subscribe(this, En_DevUnitState.ACTIVE, null);
        setSelectorModel(model);
        setSearchEnabled(true);
        setSearchAutoFocus(true);

        setDisplayOptionCreator( value -> {
            if (value == null) {
                return new DisplayOption(defaultValue);
            }
            return new DisplayOption(
                    value.getName(),
                    En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "not-active" : "" ,
                    En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "fa fa-ban ban" : "");
        } );
    }

    public void updateQuery(En_DevUnitState enDevUnitState, En_DevUnitType... enDevUnitTypes) {
        if (model != null) {
            model.updateQuery(this, enDevUnitState, enDevUnitTypes);
        }
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }
    private String defaultValue = null;
    protected ProductModel model;
}
