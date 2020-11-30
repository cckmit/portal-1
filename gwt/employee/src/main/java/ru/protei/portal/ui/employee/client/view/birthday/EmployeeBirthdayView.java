package ru.protei.portal.ui.employee.client.view.birthday;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.EmployeeBirthday;
import ru.protei.portal.core.model.struct.EmployeesBirthdays;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.employee.client.activity.birthday.AbstractEmployeeBirthdayActivity;
import ru.protei.portal.ui.employee.client.activity.birthday.AbstractEmployeeBirthdayView;
import ru.protei.portal.ui.employee.client.widget.calendar.container.BirthdaysCalendarContainer;

public class EmployeeBirthdayView extends Composite implements AbstractEmployeeBirthdayView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        calendarContainer.setHandler(new BirthdaysCalendarContainer.Handler() {
            @Override
            public void onBirthdayClicked(EmployeeBirthday birthday) {
                if (activity != null) {
                    activity.onBirthdayClicked(birthday);
                }
            }
        });
    }

    @Override
    public void setActivity(AbstractEmployeeBirthdayActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<EmployeesBirthdays> birthdays() {
        return calendarContainer;
    }

    @Override
    public TakesValue<String> yearAndMonth() {
        return new TakesValue<String>() {
            public void setValue(String value) { yearAndMonth.setInnerText(value); }
            public String getValue() { return yearAndMonth.getInnerText(); }
        };
    }

    @Override
    public HasVisibility loadingVisibility() {
        return loading;
    }

    @Override
    public HasVisibility calendarContainerVisibility() {
        return calendarContainer;
    }

    @UiHandler("oneMonthBackButton")
    public void oneMonthBackButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOneMonthBackClicked();
        }
    }

    @UiHandler("showTodayButton")
    public void showTodayButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onShowTodayButtonClicked();
        }
    }

    @UiHandler("oneMonthForwardButton")
    public void oneMonthForwardButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOneMonthForwardClicked();
        }
    }

    @UiHandler("reloadButton")
    public void reloadButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onReloadClicked();
        }
    }

    @UiField
    HeadingElement yearAndMonth;
    @UiField
    Button oneMonthBackButton;
    @UiField
    Button showTodayButton;
    @UiField
    Button oneMonthForwardButton;
    @UiField
    Button reloadButton;
    @UiField
    IndeterminateCircleLoading loading;
    @Inject
    @UiField(provided = true)
    BirthdaysCalendarContainer calendarContainer;

    private AbstractEmployeeBirthdayActivity activity;

    interface EmployeeBirthdayViewBinder extends UiBinder<HTMLPanel, EmployeeBirthdayView> {}
    private static EmployeeBirthdayViewBinder ourUiBinder = GWT.create(EmployeeBirthdayViewBinder.class);
}
