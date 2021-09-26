package ru.protei.portal.ui.common.client.widget.selector.cardbatch.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.CardBatchStateLang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class CardBatchStateFormSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init(CardBatchStateModel model) {
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
        return stateLang.getStateName(state);
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    @Inject
    CardBatchStateLang stateLang;

    private String defaultValue;
}