package ru.protei.portal.ui.employee.client.widget.calendar.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.struct.EmployeeBirthday;
import ru.protei.portal.core.model.struct.EmployeesBirthdays;
import ru.protei.portal.ui.common.client.common.YearMonthDay;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.common.client.util.AvatarUtils.getAvatarUrl;
import static ru.protei.portal.ui.common.client.util.DateUtils.*;
import static ru.protei.portal.ui.common.client.util.WidgetUtils.*;

public class BirthdaysCalendarContainer extends Composite implements HasValue<EmployeesBirthdays>, HasVisibility {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public EmployeesBirthdays getValue() {
        return value;
    }

    @Override
    public void setValue(EmployeesBirthdays value) {
        setValue(value, false);
    }

    @Override
    public void setValue(EmployeesBirthdays value, boolean fireEvents) {
        this.value = value;
        clearView();
        fillView(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EmployeesBirthdays> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void clearView() {
        container.clear();
    }

    private void fillView(EmployeesBirthdays value) {
        eventBackgroundState = 0;
        YearMonthDay today = makeYearMonthDay(new Date());
        List<List<YearMonthDay>> monthByWeeks = makeMonthByWeeks(value.getDateFrom(), value.getDateUntil());
        container.add(makeHeader());
        container.add(makeGrid(today, monthByWeeks, value.getBirthdays()));
    }

    private Widget makeHeader() {
        HTMLPanel table = makeStyledDiv("tble");
        HTMLPanel thead = makeStyledDiv("thead");
        for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {
            thead.add(makeHeaderCell(getDayOfWeekName(dayOfWeek, lang)));
        }
        table.add(thead);
        return table;
    }

    private Widget makeHeaderCell(String name) {
        HTMLPanel cell = makeStyledDiv("tcell");
        cell.add(makeStyledDiv("weekday", name));
        return cell;
    }

    private Widget makeGrid(YearMonthDay today, List<List<YearMonthDay>> monthByWeeks, List<EmployeeBirthday> birthdays) {
        HTMLPanel grid = makeStyledDiv("grid");
        grid.add(makeTable(today, monthByWeeks, birthdays));
        return grid;
    }

    private Widget makeTable(YearMonthDay today, List<List<YearMonthDay>> monthByWeeks, List<EmployeeBirthday> birthdays) {
        HTMLPanel table = makeStyledDiv("tble");
        for (List<YearMonthDay> week : monthByWeeks) {
            HTMLPanel row = makeStyledDiv("trow");
            for (YearMonthDay day : week) {
                row.add(makeTableDayCell(today, day, birthdays));
            }
            table.add(row);
        }
        return table;
    }

    private Widget makeTableDayCell(YearMonthDay today, YearMonthDay day, List<EmployeeBirthday> birthdays) {
        boolean isPastDay = isBefore(today, day);
        boolean isToday = Objects.equals(day, today);
        HTMLPanel cell = makeStyledDiv("tcell" + (isToday ? " current-date active" : "") + (isPastDay ? " not" : ""));
        cell.add(makeStyledDiv("month-date" + (isToday ? " current-date active" : ""), String.valueOf(day.getDayOfMonth())));
        HTMLPanel bdayCell = makeStyledDiv("bday-cell");
        stream(birthdays)
            .filter(birthday -> {
                boolean monthMatched = Objects.equals(birthday.getBirthdayMonth(), day.getMonth());
                boolean dayMatched = Objects.equals(birthday.getBirthdayDayOfMonth(), day.getDayOfMonth());
                return monthMatched && dayMatched;
            })
            .map(this::makeBirthday)
            .forEach(bdayCell::add);
        cell.add(bdayCell);
        return cell;
    }

    private Widget makeBirthday(EmployeeBirthday birthday) {
        FocusPanel bday = makeStyledFocusDiv("bday-wrapper " + getNextBirthdayBackground());
        HTMLPanel container = makeStyledDiv("bday-container d-flex flex-row");
        HTMLPanel containerImage = makeStyledDiv("bday-container-image thumbnail-wrapper d24 circular");
        containerImage.add(makeStyledImg("thumbnail-photo", getAvatarUrl(birthday.getId(), En_CompanyCategory.HOME, birthday.getGender())));
        container.add(containerImage);
        HTMLPanel containerText = makeStyledDiv("bday-container-text flex-grow-1");
        containerText.add(makeStyledDiv("bday-container-text-1 no-margin text-truncate", birthday.getName()));
        containerText.add(makeStyledDiv("bday-container-text-2 no-margin text-truncate", getMonthName(birthday.getBirthdayMonth(), lang) + ", " + birthday.getBirthdayDayOfMonth()));
        container.add(containerText);
        bday.add(container);
        bday.addClickHandler(event -> {
            event.preventDefault();
            event.stopPropagation();
            if (handler != null) {
                handler.onBirthdayClicked(birthday);
            }
        });
        return bday;
    }

    private String getNextBirthdayBackground() {
        int index = eventBackgroundState++;
        String background = BACKGROUNDS[index];
        if (eventBackgroundState >= BACKGROUNDS.length) {
            eventBackgroundState = 0;
        }
        return background;
    }

    private List<List<YearMonthDay>> makeMonthByWeeks(Date start, Date finish) {
        YearMonthDay from = makeYearMonthDay(start);
        YearMonthDay until = makeYearMonthDay(finish);
        List<YearMonthDay> days = rangeYearMonthDay(from, until);
        return compensateMissingDaysOfFirstAndLastWeeks(splitDaysToListOfWeeks(days));
    }

    private List<List<YearMonthDay>> splitDaysToListOfWeeks(List<YearMonthDay> days) {
        List<List<YearMonthDay>> monthByWeeks = new ArrayList<>();
        List<YearMonthDay> week = new ArrayList<>();
        for (YearMonthDay ymd : days) {
            week.add(ymd);
            boolean isEndOfWeek = ymd.getDayOfWeek() == 7;
            if (isEndOfWeek) {
                monthByWeeks.add(week);
                week = new ArrayList<>();
            }
        }
        if (isNotEmpty(week)) {
            monthByWeeks.add(week);
        }
        return monthByWeeks;
    }

    private List<List<YearMonthDay>> compensateMissingDaysOfFirstAndLastWeeks(List<List<YearMonthDay>> monthByWeeks) {
        if (isNotEmpty(monthByWeeks)) {
            monthByWeeks.set(0, compensateMissingDaysFirstWeek(monthByWeeks.get(0)));
            monthByWeeks.set(monthByWeeks.size() - 1, compensateMissingDaysLastWeek(monthByWeeks.get(monthByWeeks.size() - 1)));
        }
        return monthByWeeks;
    }

    private List<YearMonthDay> compensateMissingDaysFirstWeek(List<YearMonthDay> week) {
        int size = week.size();
        YearMonthDay ymd = week.get(0);
        for (int i = 0; i < (7 - size); i++) {
            ymd = makeYearMonthDay(makeDate(
                ymd.getYear(),
                ymd.getMonth(),
                ymd.getDayOfMonth() - 1
            ));
            week.add(0, ymd);
        }
        return week;
    }

    private List<YearMonthDay> compensateMissingDaysLastWeek(List<YearMonthDay> week) {
        int size = week.size();
        YearMonthDay ymd = week.get(week.size() - 1);
        for (int i = 0; i < (7 - size); i++) {
            ymd = makeYearMonthDay(makeDate(
                ymd.getYear(),
                ymd.getMonth(),
                ymd.getDayOfMonth() + 1
            ));
            week.add(ymd);
        }
        return week;
    }

    @Inject
    Lang lang;

    @UiField
    HTMLPanel container;

    private EmployeesBirthdays value;
    private Handler handler;
    private int eventBackgroundState = 0;
    private static final String[] BACKGROUNDS = new String[] {
            "bg-primary-lighter",
            "bg-complete-lighter",
            "bg-success-lighter",
            "bg-danger-lighter",
            "bg-master-lighter",
    };

    public interface Handler {
        void onBirthdayClicked(EmployeeBirthday birthday);
    }

    interface BirthdaysCalendarContainerBinder extends UiBinder<HTMLPanel, BirthdaysCalendarContainer> {}
    private static BirthdaysCalendarContainerBinder ourUiBinder = GWT.create(BirthdaysCalendarContainerBinder.class);
}
