package ru.protei.portal.ui.delivery.client.widget.pcborder.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderStateLang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class PcbOrderStateFormSelector extends FormPopupSingleSelector<En_PcbOrderState> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getStateName(value));
        setValueRenderer(value -> value == null ? defaultValue :
                "<i class='fas fa-circle selector  m-r-5' " +
                        "style='color:" + value.getColor() + "'></i>" + lang.getStateName(value));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
    }

    @Override
    protected SelectorItem<En_PcbOrderState> makeSelectorItem(En_PcbOrderState element, String elementHtml) {
        PopupSelectorItem<En_PcbOrderState> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        if (element != null) {
            item.setTitle(lang.getStateName(element));
            item.setIcon("fas fa-circle selector m-r-5");
            item.setIconColor(element.getColor());
        }

        return item;
    }

    @Inject
    En_PcbOrderStateLang lang;

    private List<En_PcbOrderState> values = Arrays.asList(En_PcbOrderState.values());
}
