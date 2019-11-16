package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;

public class HomeCompanyMultiSelector
        extends InputPopupMultiSelector<EntityOption>
{

    @Inject
    public void init(HomeCompanyModel model, Lang lang) {
        this.model = model;
        setAsyncSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setSelectorItemRenderer( compay -> compay==null?"":compay.getDisplayText()  );
    }

    public void setReverseOrder(boolean reverseOrder) {
        model.setReverseOrder(reverseOrder);
    }

    private HomeCompanyModel model;
}
