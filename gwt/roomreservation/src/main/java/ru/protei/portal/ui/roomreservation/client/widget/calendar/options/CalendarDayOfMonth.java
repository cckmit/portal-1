package ru.protei.portal.ui.roomreservation.client.widget.calendar.options;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.roomreservation.client.struct.Day;
import ru.protei.portal.ui.common.client.common.YearMonthDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.ui.common.client.util.DateUtils.*;
import static ru.protei.portal.ui.common.client.util.WidgetUtils.makeDiv;
import static ru.protei.portal.ui.common.client.util.WidgetUtils.makeStyledDiv;

public class CalendarDayOfMonth extends Composite implements HasValue<YearMonthDay> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public YearMonthDay getValue() {
        return value;
    }

    @Override
    public void setValue(YearMonthDay value) {
        setValue(value, false);
    }

    @Override
    public void setValue(YearMonthDay value, boolean fireEvents) {
        this.value = value;
        clearView();
        fillView();
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<YearMonthDay> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void onDayOfMonthChanged(Integer dayOfMonth) {
        value.setDayOfMonth(dayOfMonth);
        ValueChangeEvent.fire(this, value);
    }

    private void clearView() {
        container.clear();
    }

    private void fillView() {
        HTMLPanel wrapper = makeStyledDiv("weeks-wrapper");
        for (List<Day> week : makeWeeks(value)) {
            wrapper.add(makeWeek(week));
        }
        container.add(wrapper);
    }

    private Widget makeWeek(List<Day> week) {
        HTMLPanel container = makeDiv();
        boolean hasSelected = false;
        for (Day day : week) {
            YearMonthDay current = new YearMonthDay(
                value.getYear(),
                value.getMonth(),
                day.getDayOfMonth(),
                day.getDayOfWeek()
            );
            boolean isToday = isSame(
                makeYearMonthDay(new Date()),
                current
            );
            boolean isSelected = isSame(value, current);
            hasSelected |= isSelected;
            container.add(makeDay(day, isSelected, isToday));
        }
        container.setStyleName("week" + (hasSelected ? " active" : ""));
        return container;
    }

    private Widget makeDay(Day day, boolean active, boolean today) {
        HTMLPanel wrapper = makeStyledDiv("day-wrapper date-selector");

        HTMLPanel container = makeDiv();

        HTMLPanel weekDay = makeStyledDiv("week-day");
        weekDay.add(makeStyledDiv("day week-header", getDayOfWeekNameShort(day.getDayOfWeek(), lang)));
        container.add(weekDay);

        HTMLPanel weekDate = makeStyledDiv("week-date" + (active ? " active" : "") + (today ? " current-date" : ""));
        HTMLPanel content = makeStyledDiv("day");
        Anchor date = new Anchor();
        date.setText(String.valueOf(day.getDayOfMonth()));
        date.addClickHandler(event -> {
            event.preventDefault();
            event.stopPropagation();
            onDayOfMonthChanged(day.getDayOfMonth());
        });
        content.add(date);
        weekDate.add(content);
        container.add(weekDate);

        FocusPanel focus = new FocusPanel(container);
        focus.addClickHandler(event -> {
            event.preventDefault();
            onDayOfMonthChanged(day.getDayOfMonth());
        });
        wrapper.add(focus);
        return wrapper;
    }

    private List<List<Day>> makeWeeks(YearMonthDay value) {
        int year = value.getYear();
        int month = value.getMonth();
        int daysInMonth = getDaysInMonth(month, year);
        List<List<Day>> weeks = new ArrayList<>();
        weeks.add(new ArrayList<>());
        for (int dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
            Date date = makeDate(year, month, dayOfMonth);
            int dayOfWeek = getDayOfWeekNormalized(date);
            weeks.get(weeks.size() - 1).add(new Day(dayOfMonth, dayOfWeek));
            if (dayOfWeek == 7 && dayOfMonth != daysInMonth) {
                weeks.add(new ArrayList<>());
            }
        }
        return weeks;
    }

    @Inject
    Lang lang;

    @UiField
    HTMLPanel container;

    private YearMonthDay value;

    interface CalendarDayOfMonthBinder extends UiBinder<HTMLPanel, CalendarDayOfMonth> {}
    private static CalendarDayOfMonthBinder ourUiBinder = GWT.create(CalendarDayOfMonthBinder.class);
}
