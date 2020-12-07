package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class HomeCompanyButtonSelector extends ButtonPopupSingleSelector<EntityOption> {

    @Inject
    public void init(HomeCompanyModel homeCompanyModel, Lang lang) {
        this.model = homeCompanyModel;
        setAsyncModel(homeCompanyModel);
        setSearchEnabled(false);
        setItemRenderer( value ->value == null ? lang.selectValue() : value.getDisplayText());
    }

    @Override
    protected SelectorItem makeSelectorItem(EntityOption value, String elementHtml, String name) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        return item;
    }

    public void setReverseOrder(boolean reverseOrder) {
        model.setReverseOrder(reverseOrder);
    }

    public void setSynchronizeWith1C(Boolean synchronizeWith1C) {
        model.setSynchronizeWith1C(synchronizeWith1C);
        model.refreshOptions();

    }

    private HomeCompanyModel model;
}
