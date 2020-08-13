package ru.protei.portal.ui.employee.client.activity.birthday;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.struct.EmployeeBirthday;
import ru.protei.portal.core.model.struct.EmployeesBirthdays;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.YearMonthDay;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;

import static ru.protei.portal.ui.common.client.util.DateUtils.*;

public abstract class EmployeeBirthdayActivity implements AbstractEmployeeBirthdayActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(EmployeeEvents.ShowBirthdays event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }
        showView();
        cursor = new Date();
        show(cursor);
    }

    @Override
    public void onBirthdayClicked(EmployeeBirthday birthday) {
        if (birthday != null) {
            fireEvent(new EmployeeEvents.ShowFullScreen(birthday.getId()));
        }
    }

    @Override
    public void onOneMonthBackClicked() {
        YearMonthDay ymd = makeYearMonthDay(cursor);
        cursor = makeDate(ymd.getYear(), ymd.getMonth() - 1, 1);
        show(cursor);
    }

    @Override
    public void onShowTodayButtonClicked() {
        cursor = new Date();
        show(cursor);
    }

    @Override
    public void onOneMonthForwardClicked() {
        YearMonthDay ymd = makeYearMonthDay(cursor);
        cursor = makeDate(ymd.getYear(), ymd.getMonth() + 1, 1);
        show(cursor);
    }

    @Override
    public void onReloadClicked() {
        reload();
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void selectDate(Date date) {
        YearMonthDay day = makeYearMonthDay(date);
        view.yearAndMonth().setValue(day.getYear() + " " + getMonthName(day.getMonth(), lang));
    }

    private void reload() {
        show(cursor);
    }

    private void show(Date cursor) {
        Date dateFrom = makeDateFrom(resetTime(setBeginOfMonth(cursor)));
        Date dateUntil = makeDateUntil(resetTime(setEndOfMonth(cursor)));
        selectDate(cursor);
        show(dateFrom, dateUntil);
    }

    private void show(Date dateFrom, Date dateUntil) {
        hideLoading();
        hideCalendar();
        if (dateFrom == null || dateUntil == null) {
            showLoading();
            return;
        }
        showLoading();
        employeeService.getEmployeesBirthdays(dateFrom, dateUntil, new FluentCallback<EmployeesBirthdays>()
                .withError(throwable -> {
                    hideLoading();
                    hideCalendar();
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(birthdays -> {
                    hideLoading();
                    showCalendar(birthdays);
                }));
    }

    private void showCalendar(EmployeesBirthdays birthdays) {
        view.birthdays().setValue(birthdays);
        view.calendarContainerVisibility().setVisible(true);
    }

    private void hideCalendar() {
        view.calendarContainerVisibility().setVisible(false);
    }

    private void showLoading() {
        view.loadingVisibility().setVisible(true);
    }

    private void hideLoading() {
        view.loadingVisibility().setVisible(false);
    }

    private Date makeDateFrom(Date date) {
        YearMonthDay ymd = makeYearMonthDay(date);
        return makeDate(
            ymd.getYear(),
            ymd.getMonth(),
            ymd.getDayOfMonth() - getDayOfWeekNormalized(date) + 1
        );
    }

    private Date makeDateUntil(Date date) {
        YearMonthDay ymd = makeYearMonthDay(date);
        return makeDate(
            ymd.getYear(),
            ymd.getMonth(),
            ymd.getDayOfMonth() + (7 - getDayOfWeekNormalized(date))
        );
    }

    @Inject
    Lang lang;
    @Inject
    AbstractEmployeeBirthdayView view;
    @Inject
    PolicyService policyService;
    @Inject
    EmployeeControllerAsync employeeService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Date cursor;
    private AppEvents.InitDetails initDetails;
}
