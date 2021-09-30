package ru.protei.portal.ui.delivery.client.widget.cardbatch.priority;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import static ru.protei.portal.core.model.helper.StringUtils.firstUppercaseChar;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;

/**
 * Селектор приоритетов партии плат
 */
public class PrioritySelector extends FormPopupSingleSelector<ImportanceLevel> {

    @Inject
    public void init(PriorityModel priorityModel) {
        setSearchEnabled(false);
        setAsyncModel(priorityModel);
        setItemRenderer(value -> value == null ? defaultValue : value.getCode());
        setValueRenderer(value -> value == null ? defaultValue :
                "<i class='case-importance m-r-5' " +
                        "style='background-color:" + value.getColor() + ";color:" + makeContrastColor(value.getColor()) + "'>" +
                        firstUppercaseChar(value.getCode()) +
                        "</i>" + value.getCode());
    }

    @Override
    protected SelectorItem<ImportanceLevel> makeSelectorItem(ImportanceLevel element, String elementHtml) {
        PopupSelectorItem<ImportanceLevel> item = new PopupSelectorItem();
        item.setName(element.getCode());
        item.setTitle(element.getCode());
        item.setIcon("case-importance m-r-5", firstUppercaseChar(element.getCode()));
        item.setIconColor(makeContrastColor(element.getColor()), element.getColor());
        return item;
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    private String defaultValue;
}
