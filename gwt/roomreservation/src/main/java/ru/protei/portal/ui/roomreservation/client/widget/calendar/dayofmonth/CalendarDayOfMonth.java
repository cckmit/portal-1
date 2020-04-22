package ru.protei.portal.ui.roomreservation.client.widget.calendar.dayofmonth;

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
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.gwt.user.datepicker.client.CalendarUtil.resetTime;
import static ru.protei.portal.ui.roomreservation.client.util.DateUtils.*;

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
        HTMLPanel wrapper = new HTMLPanel("div", "");
        wrapper.setStyleName("weeks-wrapper");
        for (List<Day> week : makeWeeks(value)) {
            wrapper.add(makeWeek(week));
        }
        container.add(wrapper);
    }

    private Widget makeWeek(List<Day> week) {
        HTMLPanel container = new HTMLPanel("div", "");
        boolean hasSelected = false;
        for (Day day : week) {
            YearMonthDay current = new YearMonthDay(value.getYear(), value.getMonth(), day.getDayOfMonth());
            boolean isSelected = isSame(value, current);
            boolean isToday = isSame(makeYearMonthDay(new Date()), current);
            hasSelected |= isSelected;
            container.add(makeDay(day, isSelected, isToday));
        }
        container.setStyleName("week" + (hasSelected ? " active" : ""));
        return container;
    }

    private Widget makeDay(Day day, boolean active, boolean today) {
        HTMLPanel wrapper = new HTMLPanel("div", "");
        wrapper.setStyleName("day-wrapper date-selector");

        HTMLPanel container = new HTMLPanel("div", "");

        HTMLPanel weekDay = new HTMLPanel("div", "");
        weekDay.setStyleName("week-day");
        HTMLPanel header = new HTMLPanel("div", getDayOfWeekNameShort(day.getDayOfWeek(), lang));
        header.setStyleName("day week-header");
        weekDay.add(header);
        container.add(weekDay);

        HTMLPanel weekDate = new HTMLPanel("div", "");
        weekDate.setStyleName("week-date" + (active ? " active" : "") + (today ? " current-date" : ""));
        HTMLPanel content = new HTMLPanel("div", "");
        content.setStyleName("day");
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
        int daysInMonth = getDaysInMonth(value.getMonth(), value.getYear());
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

    private Date makeDate(int year, int month, int dayOfMonth) {
        Date date = new Date();
        resetTime(date);
        date.setYear(getYearDeNormalized(year));
        date.setMonth(getMonthDeNormalized(month));
        date.setDate(dayOfMonth);
        return date;
    }

    private boolean isSame(YearMonthDay d1, YearMonthDay d2) {
        return Objects.equals(d1.getYear(), d2.getYear())
            && Objects.equals(d1.getMonth(), d2.getMonth())
            && Objects.equals(d1.getDayOfMonth(), d2.getDayOfMonth());
    }

    @Inject
    Lang lang;

    @UiField
    HTMLPanel container;

    private YearMonthDay value;

    interface CalendarDayOfMonthBinder extends UiBinder<HTMLPanel, CalendarDayOfMonth> {}
    private static CalendarDayOfMonthBinder ourUiBinder = GWT.create(CalendarDayOfMonthBinder.class);
}
