package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import java.util.ArrayList;
import java.util.List;

public class HomeCompanyButtonSelector extends ButtonPopupSingleSelector<EntityOption> {

    @Inject
    public void init(HomeCompanyModel homeCompanyModel, Lang lang) {
        this.model = homeCompanyModel;
        setAsyncModel(homeCompanyModel);
        setSearchEnabled(false);
        setItemRenderer( value ->value == null ? lang.selectValue() : value.getDisplayText());
        setFilter(value -> value == null || !idsToHide.contains(value.getId()));
    }

    @Override
    protected SelectorItem makeSelectorItem(EntityOption value, String elementHtml) {
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

    public void setIdsToHide(List<Long> idsToHide) {
        this.idsToHide.clear();
        this.idsToHide.addAll(idsToHide);
        clearSelector();
    }

    private List<Long> idsToHide = new ArrayList<>();
    private HomeCompanyModel model;
}
