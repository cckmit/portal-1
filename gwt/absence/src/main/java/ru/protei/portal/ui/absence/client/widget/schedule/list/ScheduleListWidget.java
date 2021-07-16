package ru.protei.portal.ui.absence.client.widget.schedule.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dict.ScheduleValidationStatus;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.ScheduleValidator;
import ru.protei.portal.ui.absence.client.widget.schedule.item.ScheduleItemWidget;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.ScheduleValidationStatusLang;
import ru.protei.portal.ui.common.client.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleListWidget
        extends Composite
        implements HasValue<List<ScheduleItem>> {

    public ScheduleListWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public List<ScheduleItem> getValue() {
        return values;
    }

    @Override
    public void setValue(List<ScheduleItem> values) {
        setValue(values, false);
    }

    @Override
    public void setValue(List<ScheduleItem> items, boolean fireEvent) {
        hideError();
        values = values == null ? new ArrayList<ScheduleItem>() : values;
        scheduleContainer.clear();
        values.forEach(this::fillItem);
        setVisibilitySchedulePlaceholder();
        if (fireEvent) ValueChangeEvent.fire(this, items);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ScheduleItem>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void addItem(ScheduleItem value) {
        values.add(value);
        fillItem(value);
        setVisibilitySchedulePlaceholder();
        ScheduleValidationStatus validationStatus = ScheduleValidator.isValidSchedule(values);
        if (validationStatus == ScheduleValidationStatus.OK) {
            hideError();
            return;
        }
        showError(ScheduleValidationStatusLang.getValidationMessage(validationStatus, lang));
    }

    private void fillItem(ScheduleItem value) {
        ScheduleItemWidget itemWidget = new ScheduleItemWidget();
        String daysStr = CollectionUtils.emptyIfNull(value.getDaysOfWeek())
                .stream()
                .map(day -> DateUtils.getDayOfWeekNameShort(day, lang))
                .collect(Collectors.joining(", "));
        itemWidget.setDays(daysStr);
        String timeRangeStr = CollectionUtils.emptyIfNull(value.getTimes())
                .stream()
                .map(this::formatTimePeriod)
                .collect(Collectors.joining(", "));
        itemWidget.setTimes(timeRangeStr);
        itemWidget.addRemoveHandler(handler -> {
            scheduleContainer.remove(itemWidget);
            values.remove(value);
            setVisibilitySchedulePlaceholder();
            validate();
        });

        scheduleContainer.add(itemWidget);
        setVisibilitySchedulePlaceholder();
    }

    private void validate() {
        ScheduleValidationStatus status = ScheduleValidator.isValidSchedule(values);
        if (status == ScheduleValidationStatus.OK) {
            hideError();
            return;
        }
        showError(ScheduleValidationStatusLang.getValidationMessage(status, lang));
    }

    private String formatTimePeriod(Interval interval) {
        if (interval == null || interval.isEmpty()) {
            return "";
        }

        return lang.absenceTimeRange(DateFormatter.formatTimeOnly(interval.from), DateFormatter.formatTimeOnly(interval.to));
    }

    private void setVisibilitySchedulePlaceholder() {
        noScheduleDataPlaceholder.setVisible(CollectionUtils.isEmpty(values));
    }

    private void showError(String error) {
        errorMessage.setText(error);
        errorMessage.setVisible(true);
    }

    private void hideError() {
        errorMessage.setText(null);
        errorMessage.setVisible(false);
    }

    @UiField
    Label errorMessage;
    @UiField
    HTMLPanel scheduleContainer;
    @UiField
    HTMLPanel noScheduleDataPlaceholder;
    @UiField
    Lang lang;

    private List<ScheduleItem> values = new ArrayList<>();

    interface ScheduleListWidgetBinder extends UiBinder<HTMLPanel, ScheduleListWidget> {}
    private static ScheduleListWidgetBinder ourUiBinder = GWT.create(ScheduleListWidgetBinder.class);
}
