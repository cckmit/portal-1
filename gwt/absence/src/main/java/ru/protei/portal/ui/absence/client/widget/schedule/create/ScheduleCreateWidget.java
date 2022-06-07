package ru.protei.portal.ui.absence.client.widget.schedule.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.util.ScheduleValidator;
import ru.protei.portal.ui.absence.client.widget.timerange.TimeRange;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.togglebtn.item.ToggleButton;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dto.ScheduleItem;

import java.util.*;
import java.util.stream.Collectors;

public class ScheduleCreateWidget
        extends Composite
        implements HasRejectHandlers, HasApplyHandlers<ScheduleItem> {

    public ScheduleCreateWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        generateDaysOfWeek();
    }

    @Override
    public HandlerRegistration addApplyHandler(ApplyHandler<ScheduleItem> handler) {
        return addHandler(handler, ApplyEvent.getType());
    }

    @Override
    public HandlerRegistration addRejectHandler(RejectHandler handler) {
        return addHandler(handler, RejectEvent.getType());
    }

    @UiHandler("addTimeRangeButton")
    public void onAddTimeRangeButton(ClickEvent event) {
        addDefaultTimeRange();
    }

    public void resetView() {
        resetDays();
        timeRangeContainer.clear();
        timeRangeItems.clear();
        addDefaultTimeRange();
        hideError();
    }

    @UiHandler("selectEverydayButton")
    public void onSelectEverydayButtonClicked(ClickEvent event) {
        event.preventDefault();
        for (Map.Entry<Integer, ToggleButton> buttonEntry : dayToBtnMap.entrySet()) {
            buttonEntry.getValue().setValue(true);
        }
    }

    @UiHandler("selectWeekdaysButton")
    public void onSelectWeekdaysButtonClicked(ClickEvent event) {
        event.preventDefault();
        int weekendStart = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekendStart();
        int weekendEnd = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekendEnd();

        for (Map.Entry<Integer, ToggleButton> buttonEntry : dayToBtnMap.entrySet()) {
            boolean isWeekdays;
            if (weekendStart > weekendEnd) {
                isWeekdays = buttonEntry.getKey() > weekendEnd && buttonEntry.getKey() < weekendStart;
            } else {
                isWeekdays = buttonEntry.getKey() > weekendStart && buttonEntry.getKey() < weekendEnd;
            }
            buttonEntry.getValue().setValue(isWeekdays);
        }
    }

    @UiHandler("applyButton")
    public void onApplyButtonClicked(ClickEvent event) {
        En_ResultStatus status = ScheduleValidator.isValidScheduleItem(getValue());
        if (status == En_ResultStatus.OK) {
            hideError();
            ApplyEvent.fire(this, getValue());
            return;
        }
        showError(statusLang.getMessage(status));
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        resetView();
        RejectEvent.fire(this);
    }

    @UiHandler("resetButton")
    public void onResetButtonClicked(ClickEvent event) {
        resetView();
    }

    private void addDefaultTimeRange() {
        TimeRange timeRangeWidget = timeRangeProvider.get();
        timeRangeWidget.addValueChangeHandler(value -> {
            En_ResultStatus status = ScheduleValidator.isValidIntervals(getTimeRanges());
            if (status == En_ResultStatus.OK) {
                hideError();
                return;
            }
            showError(statusLang.getMessage(status));
        });
        timeRangeWidget.setValue(new TimeInterval(new Time(0, 0), new Time(23, 59)));
        timeRangeContainer.add(timeRangeWidget);
        timeRangeItems.add(timeRangeWidget);
    }

    private ScheduleItem getValue() {
        return new ScheduleItem(getSelectedDays(), getTimeRanges());
    }

    private void showError(String error) {
        errorMessage.setText(error);
        errorMessage.setVisible(true);
    }

    private void hideError() {
        errorMessage.setText(null);
        errorMessage.setVisible(false);
    }

    private void generateDaysOfWeek() {
        String[] weekdays = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekdaysShortStandalone();

        int currentDayIndex = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().firstDayOfTheWeek();
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            String day = weekdays[currentDayIndex];
            ToggleButton dayButton = new ToggleButton();
            dayButton.setText(day);
            dayButton.setStyleName("btn btn-circle absence-day-btn m-r-5");
            dayToBtnMap.put(currentDayIndex, dayButton);
            daysOfWeekContainer.add(dayButton);
            currentDayIndex++;
            currentDayIndex = currentDayIndex >= DAYS_IN_WEEK ? currentDayIndex - DAYS_IN_WEEK : currentDayIndex;
        }
    }

    private void resetDays() {
        dayToBtnMap.forEach((key, value) -> value.setValue(false));
    }

    private List<Integer> getSelectedDays() {
        return dayToBtnMap.entrySet().stream()
                .filter(entry -> entry.getValue().getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<TimeInterval> getTimeRanges() {
        return timeRangeItems.stream()
                .map(TimeRange::getValue)
                .collect(Collectors.toList());
    }

    @UiField
    HTMLPanel daysOfWeekContainer;
    @UiField
    Button addTimeRangeButton;
    @UiField
    HTMLPanel timeRangeContainer;
    @UiField
    Label errorMessage;

    @UiField
    Lang lang;

    @Inject
    static Provider<TimeRange> timeRangeProvider;
    @Inject
    static En_ResultStatusLang statusLang;

    private Map<Integer, ToggleButton> dayToBtnMap = new HashMap<>();
    private List<TimeRange> timeRangeItems = new ArrayList<>();
    private static final int DAYS_IN_WEEK = 7;

    interface AbsenceCreateWidgetBinder extends UiBinder<HTMLPanel, ScheduleCreateWidget> {}
    private static AbsenceCreateWidgetBinder ourUiBinder = GWT.create(AbsenceCreateWidgetBinder.class);

}
