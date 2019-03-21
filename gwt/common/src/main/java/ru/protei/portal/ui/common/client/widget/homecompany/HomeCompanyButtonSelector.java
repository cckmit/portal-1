package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeCompanyButtonSelector extends ButtonSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init(HomeCompanyModel homeCompanyModel, Lang lang) {
        homeCompanyModel.subscribe(this);
        setSelectorModel(homeCompanyModel);
        setSearchEnabled(false);
        setDisplayOptionCreator(value -> new DisplayOption(value == null ? lang.selectValue() : value.getDisplayText()));
    }

    @Override
    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        if (hasNullValue) {
            addOption(null);
        }
        if (reverseOrder) {
            List<EntityOption> reversed = new ArrayList<>(options);
            Collections.reverse(reversed);
            reversed.forEach(this::addOption);
        } else {
            options.forEach(this::addOption);
        }
    }

    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
    }

    private boolean reverseOrder = false;
}
