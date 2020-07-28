package ru.protei.portal.ui.absence.client.widget.paramview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.activity.report.paramview.AbstractAbsenceFilterParamWidget;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public class AbsenceFilterParamWidget extends Composite implements AbstractAbsenceFilterParamWidget {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        fillDateRanges(dateRange);
    }

    @Override
    public boolean isValidDateRange() {
        return validate();
    }

    private boolean validate() {
        boolean dataRangeTypeValid = isDataRangeTypeValid(dateRange);
        boolean dataRangeValid = isDataRangeValid(dateRange.getValue());
        dateRange.setValid(dataRangeTypeValid, dataRangeValid);
        boolean isValid = dataRangeTypeValid && dataRangeValid;
        if (validateCallback != null) {
            validateCallback.accept(isValid);
        }
        return isValid;
    }

    @Override
    public void watchForScrollOf(Widget widget) {
        sortField.watchForScrollOf(widget);
    }

    @Override
    public void stopWatchForScrollOf(Widget widget) {
        sortField.stopWatchForScrollOf(widget);
    }

    @Override
    public void resetFilter() {
        employees.setValue(null);
        dateRange.setValue(null);
        reasons.setValue(null);
        sortField.setValue(En_SortField.absence_person);
        sortDir.setValue(true);
        validate();
    }

    @Override
    public AbsenceQuery getQuery() {
        return new AbsenceQuery(
                toDateRange(dateRange.getValue()),
                CollectionUtils.isEmpty(employees.getValue()) ? new HashSet<>() :
                        employees.getValue().stream().map(PersonShortView::getId).collect(Collectors.toSet()),
                CollectionUtils.isEmpty(reasons.getValue()) ? new HashSet<>() :
                        reasons.getValue().stream().map(En_AbsenceReason::getId).collect(Collectors.toSet()),
                sortField.getValue(),
                sortDir.getValue() ? En_SortDir.ASC : En_SortDir.DESC);
    }

    @Override
    public void fillFilterFields(AbsenceQuery query, SelectorsParams selectorsParams) {
        dateRange.setValue(DateIntervalWithType.fromDateRange(query.getDateRange(), false));
        employees.setValue(applyPersons(selectorsParams, query.getEmployeeIds()));
        reasons.setValue(applyReason(query.getReasonIds()));
        sortField.setValue(query.getSortField());
        sortDir.setValue(query.getSortDir() == En_SortDir.ASC);
        validate();
    }

    @Override
    public void setValidateCallback(Consumer<Boolean> callback) {
        validateCallback = callback;
    }

    private Set<PersonShortView> applyPersons(SelectorsParams filter, Set<Long> personIds) {
        return emptyIfNull(filter.getPersonShortViews()).stream()
                .filter(personShortView ->
                        emptyIfNull(personIds).stream().anyMatch(ids -> ids.equals(personShortView.getId())))
                .collect(Collectors.toSet());
    }

    private Set<En_AbsenceReason> applyReason(Set<Integer> reasonIds) {
        return emptyIfNull(reasonIds).stream()
                .map(id -> Arrays.stream(En_AbsenceReason.values()).filter(en_absenceReason -> en_absenceReason.getId() == id).findAny().orElse(null))
                .collect(Collectors.toSet());
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        validate();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        dateRange.setEnsureDebugId(DebugIds.ABSENCE_REPORT.DATE_RANGE_INPUT);
        employees.setAddEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_ADD_BUTTON);
        employees.setClearEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_CLEAR_BUTTON);
        employees.setItemContainerEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_ITEM_CONTAINER);
        employees.setLabelEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_LABEL);
        reasons.setAddEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_ADD_BUTTON);
        reasons.setClearEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_CLEAR_BUTTON);
        reasons.setItemContainerEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_ITEM_CONTAINER);
        reasons.setLabelEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_LABEL);
        absenceReportSortByLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE_REPORT.SORT_FIELD_LABEL);
        sortField.setEnsureDebugId(DebugIds.ABSENCE_REPORT.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.ABSENCE_REPORT.SORT_DIR_BUTTON);
    }

    private void fillDateRanges (TypedSelectorRangePicker rangePicker) {
        rangePicker.fillSelector(En_DateIntervalType.reportTypes());
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

    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker dateRange;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector employees;
    @Inject
    @UiField(provided = true)
    AbsenceReasonMultiSelector reasons;
    @UiField
    LabelElement absenceReportSortByLabel;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField
    Lang lang;

    private Consumer<Boolean> validateCallback;

    private static AbsenceFilterParamViewUiBinder ourUiBinder = GWT.create(AbsenceFilterParamViewUiBinder.class);
    interface AbsenceFilterParamViewUiBinder extends UiBinder<HTMLPanel, AbsenceFilterParamWidget> {}
}