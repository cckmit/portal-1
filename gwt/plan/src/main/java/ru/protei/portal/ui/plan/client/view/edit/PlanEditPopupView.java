package ru.protei.portal.ui.plan.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.plan.client.activity.edit.AbstractPlanEditPopupView;

public class PlanEditPopupView extends Composite implements AbstractPlanEditPopupView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<DateInterval> planPeriod() { return planPeriod; }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @UiField
    Lang lang;

    @UiField
    ValidableTextBox name;

    @Inject
    @UiField(provided = true)
    RangePicker planPeriod;

    interface PlanEditPopupViewUiBinder extends UiBinder<Widget, PlanEditPopupView> {}
    private static PlanEditPopupViewUiBinder ourUiBinder = GWT.create(PlanEditPopupViewUiBinder.class);
}