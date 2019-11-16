package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.ButtonPopupSingleSelector;

public class HomeCompanyButtonSelector
         extends ButtonPopupSingleSelector<EntityOption>
{

    @Inject
    public void init(HomeCompanyModel homeCompanyModel, Lang lang) {
        this.model = homeCompanyModel;
        setAsyncSelectorModel(homeCompanyModel);
        setSearchEnabled(false);
        setSelectorItemRenderer(value ->value == null ? lang.selectValue() : value.getDisplayText());
    }

    public void setReverseOrder(boolean reverseOrder) {
        model.setReverseOrder(reverseOrder);
    }

    private HomeCompanyModel model;
}
