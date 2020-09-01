package ru.protei.portal.ui.absence.client.widget.datetime;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.widget.datetime.item.AbsenceDatesItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.gwt.user.datepicker.client.CalendarUtil.addDaysToDate;
import static com.google.gwt.user.datepicker.client.CalendarUtil.copyDate;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HAS_ERROR;
import static ru.protei.portal.ui.common.client.util.DateUtils.*;

public class AbsenceDates extends Composite implements HasValue<List<DateInterval>>, HasEnabled, HasVaryAbility {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public List<DateInterval> getValue() {
        return stream(items)
            .map(AbsenceDatesItem::getValue)
            .collect(Collectors.toList());
    }

    @Override
    public void setValue(List<DateInterval> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(List<DateInterval> value, boolean fireEvents) {
        clearView();
        fillView(ensureAtLeastOneExists(value));
        if (fireEvents) {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        render();
    }

    @Override
    public boolean isVaryAble() {
        return isVaryAble;
    }

    @Override
    public void setVaryAble(boolean isVaryAble) {
        this.isVaryAble = isVaryAble;
        controls.setVisible(isVaryAble);
        createAddWeek.setEnabled(isVaryAble);
        createAddDay.setEnabled(isVaryAble);
        render();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<DateInterval>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("createAddWeek")
    public void createAddWeekClick(ClickEvent event) {
        List<DateInterval> value = getValue();
        value.add(addDays(value.get(value.size() - 1), 7));
        setValue(value, true);
    }

    @UiHandler("createAddDay")
    public void createAddDayClick(ClickEvent event) {
        List<DateInterval> value = getValue();
        value.add(addDays(value.get(value.size() - 1), 1));
        setValue(value, true);
    }

    public void setValid(boolean isValid) {
        if (isValid) {
            root.removeStyleName(HAS_ERROR);
        } else {
            root.addStyleName(HAS_ERROR);
        }
    }

    private void fillView(List<DateInterval> intervals) {
        container.clear();
        stream(intervals)
            .map(this::createAbsenceDatesItem)
            .forEach(item -> {
                items.add(item);
                container.add(item.asWidget());
            });
    }

    private AbsenceDatesItem createAbsenceDatesItem(DateInterval interval) {
        AbsenceDatesItem item = itemProvider.get();
        item.setEnabled(isEnabled);
        item.setVaryAble(isVaryAble);
        item.setValue(interval);
        item.addRemoveHandler(removeEvent -> {
            items.remove(item);
            render();
        });
        item.addValueChangeHandler(this::fireChange);
        return item;
    }

    private void clearView() {
        items.clear();
    }

    private List<DateInterval> ensureAtLeastOneExists(List<DateInterval> value) {
        if (value.size() > 0) {
            return value;
        }
        Date now = new Date();
        value.add(new DateInterval(setBeginOfDay(now), setEndOfDay(now)));
        return value;
    }

    private void render() {
        setValue(getValue());
    }

    private DateInterval addDays(DateInterval interval, int days) {
        Date from = copyDate(interval.from);
        Date to = copyDate(interval.to);
        addDaysToDate(from, days);
        addDaysToDate(to, days);
        return new DateInterval(from, to);
    }

    private Date addHours(Date date, int hours) {
        Date d = copyDate(date);
        d.setHours(d.getHours() + hours);
        return d;
    }

    private void fireChange(ValueChangeEvent<DateInterval> ignore) {
        ValueChangeEvent.fire(AbsenceDates.this, null);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        container.ensureDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.DATE_RANGE_CONTAINER);
        createAddDay.ensureDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.DATE_RANGE_CONTAINER_ADD_DAY_BUTTON);
        createAddWeek.ensureDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.DATE_RANGE_CONTAINER_ADD_WEEK_BUTTON);
    }

    @Inject
    Provider<AbsenceDatesItem> itemProvider;
    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel container;
    @UiField
    HTMLPanel controls;
    @UiField
    Button createAddWeek;
    @UiField
    Button createAddDay;

    private List<AbsenceDatesItem> items = new ArrayList<>();
    private boolean isEnabled = true;
    private boolean isVaryAble = true;

    interface AbsenceDatesBinder extends UiBinder<HTMLPanel, AbsenceDates> {}
    private static AbsenceDatesBinder ourUiBinder = GWT.create(AbsenceDatesBinder.class);
}
