package ru.protei.portal.ui.common.client.widget.selector.plan.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.plan.model.PlanModel;

public class PlanMultiSelector extends InputPopupMultiSelector<PlanOption> {
    @Inject
    public void init(PlanModel model, Lang lang) {
        this.model = model;

        setSearchEnabled(true);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setAsyncSearchModel(model);
        setItemRenderer(PlanOption::getDisplayText);
    }

    public void setCreatorId(Long creatorId) {
        model.setCreatorId(creatorId);
    }

    private PlanModel model;
}
