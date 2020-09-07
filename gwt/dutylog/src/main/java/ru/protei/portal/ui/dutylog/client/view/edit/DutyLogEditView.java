package ru.protei.portal.ui.dutylog.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.DutyType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.DutyTypeLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;
import ru.protei.portal.ui.dutylog.client.activity.edit.AbstractDutyLogEditActivity;
import ru.protei.portal.ui.dutylog.client.activity.edit.AbstractDutyLogEditView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;


public class DutyLogEditView extends Composite implements AbstractDutyLogEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        type.setModel(elementIndex -> {
            DutyType[] list = DutyType.values();
            if (list.length <= elementIndex) return null;
            return list[elementIndex];
        });
        type.setItemRenderer(value -> typeLang.getName(value));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDutyLogEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<PersonShortView> employee() {
        return employee;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<DutyType> type() {
        return type;
    }

    @Override
    public HasVisibility contentVisibility() {
        return content;
    }

    @Override
    public HasVisibility loadingVisibility() {
        return loading;
    }

    @Override
    public HasValidable employeeValidator() {
        return employee;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        employee.setEnsureDebugId(DebugIds.DUTY_LOG.EDIT.EMPLOYEE_SELECTOR);
        dateRange.setEnsureDebugId(DebugIds.DUTY_LOG.EDIT.DATE_RANGE_INPUT);
        type.setEnsureDebugId(DebugIds.DUTY_LOG.EDIT.TYPE_SELECTOR);
    }

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector employee;
    @Inject
    @UiField(provided = true)
    RangePicker dateRange;
    @Inject
    @UiField(provided = true)
    FormPopupSingleSelector<DutyType> type;
    @UiField
    HTMLPanel content;
    @UiField
    IndeterminateCircleLoading loading;
    @UiField
    Lang lang;

    @Inject
    DutyTypeLang typeLang;

    private AbstractDutyLogEditActivity activity;

    private static DutyLogEditViewUiBinder ourUiBinder = GWT.create(DutyLogEditViewUiBinder.class);
    interface DutyLogEditViewUiBinder extends UiBinder<HTMLPanel, DutyLogEditView> {}
}