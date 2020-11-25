package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

/**
 * Селектор списка компаний
 */
public class CompanySelector extends ButtonPopupSingleSelector<EntityOption> {
    @Inject
    public void init(CompanyModel companyModel) {
        setAsyncModel(companyModel);
        setItemRenderer(value -> value == null ? defaultValue : value.getDisplayText());
    }

    @Override
    protected SelectorItem makeSelectorItem(EntityOption value, String elementHtml, String title) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        return item;
    }
}
