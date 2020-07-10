package ru.protei.portal.ui.ipreservation.client.view.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;

public class SubnetMultiSelector extends InputPopupMultiSelector<SubnetOption>{

    @Inject
    public void init(SubnetModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName( lang.buttonClear() );
        setFilter(subnetOption -> subnetOption.isLocal());
        setItemRenderer( value -> value == null ? "" : value.getDisplayText() );
    }

    @Override
    public boolean isValid() {
        return CollectionUtils.isNotEmpty(getValue());
    }

    @Override
    protected SelectorItem<SubnetOption> makeSelectorItem(SubnetOption value, String elementHtml) {
        PopupSelectableItem<SubnetOption> item = new PopupSelectableItem<>();

        if (value != null && !value.isLocal()) {
            item.setIcon("fa fa-ban ban m-r-5");
        }

        item.setElementHtml(elementHtml);
        item.setSelected(isSelected(value));
        return item;
    }

    public void setOnlyLocalVisible(boolean onlyLocal) {
        if (onlyLocal) {
            setFilter(subnetOption -> true);
        }
    }

    private SubnetModel model;
}
