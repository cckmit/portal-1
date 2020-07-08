package ru.protei.portal.ui.common.client.widget.selector.plan.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.plan.model.PlanModel;

public class PlanButtonSelector extends ButtonPopupSingleSelector<PlanOption> {
    @Inject
    public void init(PlanModel model) {
        setAsyncSearchModel(model);
        setItemRenderer(value -> value == null ? defaultValue : value.getDisplayText());
    }
}
