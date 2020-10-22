package ru.protei.portal.ui.dutylog.client.widget.filter.paramview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.DutyType;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.DutyTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;
import ru.protei.portal.ui.dutylog.client.activity.filter.AbstractDutyLogFilterParamView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public class DutyLogFilterParamWidget extends Composite implements AbstractDutyLogFilterParamView {

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
    public boolean isValidDateRange() {
        return validate();
    }

    @Override
    public void resetFilter() {
        sortField.setValue(En_SortField.duty_log_date_from);
        sortDir.setValue(true);
        employee.setValue(null);
        type.setValue(null);
        date.setValue(new DateIntervalWithType(null, En_DateIntervalType.THIS_WEEK_AND_BEYOND));
        if (isAttached()) {
            onFilterChanged();
        }
    }

    @Override
    public DutyLogQuery getQuery() {
        return new DutyLogQuery(
                toDateRange(date.getValue()),
                CollectionUtils.isEmpty(employee.getValue()) ? new HashSet<>() :
                        employee.getValue().stream().map(PersonShortView::getId).collect(Collectors.toSet()),
                CollectionUtils.isEmpty(type.getValue()) ? new HashSet<>() :  type.getValue(),
                sortField.getValue(),
                sortDir.getValue() ? En_SortDir.ASC : En_SortDir.DESC);
    }

    @Override
    public void fillFilterFields(DutyLogQuery query, SelectorsParams selectorsParams) {
        date.setValue(DateIntervalWithType.fromDateRange(query.getDateRange(), false));
        employee.setValue(applyPersons(selectorsParams, query.getPersonIds()));
        type.setValue(query.getTypes());
        sortField.setValue(query.getSortField());
        sortDir.setValue(query.getSortDir() == En_SortDir.ASC);
        if (validate()) {
            onFilterChanged();
        }
    }

    @Override
    public void setValidateCallback(Consumer<Boolean> callback) {
        validateCallback = callback;
    }

    @Override
    public void setOnFilterChangeCallback(Runnable onFilterChangeCallback) {
        this.onFilterChangeCallback = onFilterChangeCallback;
    }

    @UiHandler("type")
    public void onTypeSelected(ValueChangeEvent<Set<DutyType>> event){
        onFilterChanged();
    }

    @UiHandler("employee")
    public void onEmployeeSelected(ValueChangeEvent<Set<PersonShortView>> event){
        onFilterChanged();
    }

    @UiHandler("date")
    public void onDateSelected(ValueChangeEvent<DateIntervalWithType> event){
        if (validate()) {
            onFilterChanged();
        }
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        onFilterChanged();
    }

    @UiHandler( "sortDir" )
    public void onSortDirClicked( ClickEvent event ) {
        onFilterChanged();
    }

    private boolean validate() {
        boolean dataRangeTypeValid = isDataRangeTypeValid(date);
        boolean dataRangeValid = isDataRangeValid(date.getValue());
        date.setValid(dataRangeTypeValid, dataRangeValid);
        boolean isValid = dataRangeTypeValid && dataRangeValid;
        if (validateCallback != null) {
            validateCallback.accept(isValid);
        }
        return isValid;
    }

    private boolean isDataRangeTypeValid(TypedSelectorRangePicker rangePicker) {
        return !rangePicker.isTypeMandatory()
                || (rangePicker.getValue() != null
                && rangePicker.getValue().getIntervalType() != null);
    }

    private boolean isDataRangeValid(DateIntervalWithType dateRange) {
        if (dateRange == null || dateRange.getIntervalType() == null) {
            return true;
        }

        return !Objects.equals(dateRange.getIntervalType(), En_DateIntervalType.FIXED)
                || dateRange.getInterval().isValid();
    }

    private Set<PersonShortView> applyPersons(SelectorsParams filter, Set<Long> personIds) {
        return emptyIfNull(filter.getPersonShortViews()).stream()
                .filter(personShortView ->
                        emptyIfNull(personIds).stream().anyMatch(ids -> ids.equals(personShortView.getId())))
                .collect(Collectors.toSet());
    }

    private void onFilterChanged() {
        if (onFilterChangeCallback != null) {
            onFilterChangeCallback.run();
        }
    }

    private void ensureDebugIds() {
        if ( !DebugInfo.isDebugIdEnabled() ) {
            return;
        }

        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);
        employee.ensureDebugId(DebugIds.DUTY_LOG.FILTER.EMPLOYEE);
        date.ensureDebugId(DebugIds.DUTY_LOG.FILTER.DATE_RANGE);
        type.ensureDebugId(DebugIds.DUTY_LOG.FILTER.TYPE);
    }

    @UiField
    Lang lang;
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

    private Consumer<Boolean> validateCallback;
    private Runnable onFilterChangeCallback;

    private static DutyLogFilterParamWidgetUiBinder outUiBinder = GWT.create(DutyLogFilterParamWidgetUiBinder.class);
    interface DutyLogFilterParamWidgetUiBinder extends UiBinder<HTMLPanel, DutyLogFilterParamWidget> {}
}
