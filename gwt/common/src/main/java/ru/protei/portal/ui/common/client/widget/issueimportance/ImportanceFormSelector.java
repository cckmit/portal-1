package ru.protei.portal.ui.common.client.widget.issueimportance;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;

/**
 * Селектор критичности кейсов
 */
public class ImportanceFormSelector extends FormPopupSingleSelector<ImportanceLevel> {

    @Inject
    public void init() {
        setSearchEnabled(false);
        setItemRenderer(value -> value == null ? defaultValue : value.getCode());
        setValueRenderer(value -> value == null ? defaultValue :
                "<i class='case-importance m-r-5' " +
                        "style='background-color:" + value.getColor() + ";color:" + makeContrastColor(value.getColor()) + "'>" +
                        value.getCode().substring(0, 1).toUpperCase() +
                        "</i>" + value.getCode());
    }

    @Override
    protected SelectorItem<ImportanceLevel> makeSelectorItem(ImportanceLevel element, String elementHtml) {
        PopupSelectorItem<ImportanceLevel> item = new PopupSelectorItem();
        item.setName(element.getCode());
        item.setTitle(element.getCode());
        item.setIcon("case-importance m-r-5", element.getCode().substring(0, 1).toUpperCase());
        item.setIconColor(makeContrastColor(element.getColor()), element.getColor());
        return item;
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    private String defaultValue;
}
