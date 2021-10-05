package ru.protei.portal.ui.common.client.widget.selector.module;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.ModuleStateLang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class ModuleStateFormSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init(ModuleStateModel model) {
        setSearchEnabled(false);
        setAsyncModel(model);
        setItemRenderer(value -> value == null ? defaultValue : getStateName(value));
        setValueRenderer(value -> value == null ? defaultValue :
                "<i class='fas fa-circle selector  m-r-5' " +
                        "style='color:" + value.getColor() + "'></i>" + getStateName(value));
    }

    @Override
    protected SelectorItem<CaseState> makeSelectorItem(CaseState element, String elementHtml) {
        PopupSelectorItem<CaseState> item = new PopupSelectorItem();
        item.setName(elementHtml);
        if (element != null) {
            item.setTitle(getStateName(element));
            item.setIcon("fas fa-circle selector m-r-5");
            item.setIconColor(element.getColor());
        }

        return item;
    }

    public String getStateName(CaseState state) {
        return moduleStateLang.getStateName(state);
    }

    @Inject
    ModuleStateLang moduleStateLang;

}