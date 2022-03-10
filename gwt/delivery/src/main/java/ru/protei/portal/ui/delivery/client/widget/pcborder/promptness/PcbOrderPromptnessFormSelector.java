package ru.protei.portal.ui.delivery.client.widget.pcborder.promptness;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderPromptnessLang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class PcbOrderPromptnessFormSelector extends FormPopupSingleSelector<En_PcbOrderPromptness> {

    @Inject
    public void init() {
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
        setValueRenderer(value -> value == null ? defaultValue :
                "<i class='fas fa-circle selector  m-r-5' " +
                        "style='color:" + value.getColor() + "'></i>" + lang.getName(value));
        setModel(elementIndex -> {
            if (size(values) <= elementIndex) return null;
            return values.get(elementIndex);
        });
    }

    @Override
    protected SelectorItem<En_PcbOrderPromptness> makeSelectorItem(En_PcbOrderPromptness element, String elementHtml) {
        PopupSelectorItem<En_PcbOrderPromptness> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        if (element != null) {
            item.setTitle(lang.getName(element));
            item.setIcon("fas fa-circle selector m-r-5");
            item.setIconColor(element.getColor());
        }

        return item;
    }

    @Inject
    En_PcbOrderPromptnessLang lang;

    private List<En_PcbOrderPromptness> values = Arrays.asList(En_PcbOrderPromptness.values());
}
