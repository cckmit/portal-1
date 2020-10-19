package ru.protei.portal.ui.dutylog.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.DutyTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;
import ru.protei.portal.ui.dutylog.client.activity.filter.AbstractDutyLogFilterActivity;
import ru.protei.portal.ui.dutylog.client.activity.filter.AbstractDutyLogFilterView;

import java.util.Set;

public class DutyLogFilterView extends Composite implements AbstractDutyLogFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        date.fillSelector(En_DateIntervalType.dutyTypes());
        type.setModel(elementIndex -> {
            DutyType[] list = DutyType.values();
            if (list.length <= elementIndex) return null;
            return list[elementIndex];
        });
        type.setItemRenderer(value -> typeLang.getName(value));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDutyLogFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        sortField.setValue(En_SortField.duty_log_date_from);
        sortDir.setValue(false);
        employee.setValue(null);
        date.setValue(null);
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<Set<PersonShortView>> employees() {
        return employee;
    }

    @Override
    public HasValue<DateIntervalWithType> date() {
        return date;
    }

    @Override
    public HasValue<Set<DutyType>> type() {
        return type;
    }

    @UiHandler("type")
    public void onTypeSelected(ValueChangeEvent<Set<DutyType>> event){
        fireFilterChanged();
    }

    @UiHandler("employee")
    public void onEmployeeSelected(ValueChangeEvent<Set<PersonShortView>> event){
        fireFilterChanged();
    }

    @UiHandler("date")
    public void onDateSelected(ValueChangeEvent<DateIntervalWithType> event){
        fireFilterChanged();
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        fireFilterChanged();
    }

    @UiHandler( "sortDir" )
    public void onSortDirClicked( ClickEvent event ) {
        fireFilterChanged();
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        resetFilter();
        fireFilterChanged();
    }
    private void fireFilterChanged() {
        activity.onFilterChanged();
    }

    private void ensureDebugIds() {
        if ( !DebugInfo.isDebugIdEnabled() ) {
            return;
        }

        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
        employee.ensureDebugId(DebugIds.DUTY_LOG.FILTER.EMPLOYEE);
        date.ensureDebugId(DebugIds.DUTY_LOG.FILTER.DATE_RANGE);
        type.ensureDebugId(DebugIds.DUTY_LOG.FILTER.TYPE);
    }

    @UiField
    Lang lang;
    @UiField
    Button resetBtn;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker date;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector employee;
    @Inject
    @UiField(provided = true)
    InputPopupMultiSelector<DutyType> type;

    @Inject
    DutyTypeLang typeLang;

    private AbstractDutyLogFilterActivity activity;

    private static DutyLogFilterViewUiBinder outUiBinder = GWT.create(DutyLogFilterViewUiBinder.class);
    interface DutyLogFilterViewUiBinder extends UiBinder<HTMLPanel, DutyLogFilterView> {}
}
