package ru.protei.portal.ui.ipreservation.client.view.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class SubnetMultiSelector extends InputPopupMultiSelector<SubnetOption>{

    @Inject
    public void init(SubnetModel model, Lang lang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName( lang.buttonClear() );
        setFilter(subnetOption -> subnetOption.isAllowForReserve());
        setItemRenderer( value -> value == null ? "" : value.getDisplayText() );
    }

    @Override
    public boolean isValid() {
        return CollectionUtils.isNotEmpty(getValue());
    }

    public void setShowAll(boolean value) {
        if (value) {
            setFilter(null);
        }
    }
}
