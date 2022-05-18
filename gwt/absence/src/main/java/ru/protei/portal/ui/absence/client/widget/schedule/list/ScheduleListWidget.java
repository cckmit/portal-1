package ru.protei.portal.ui.absence.client.widget.schedule.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.util.ScheduleValidator;
import ru.protei.portal.ui.absence.client.util.ScheduleFormatterClient;
import ru.protei.portal.ui.absence.client.widget.schedule.item.ScheduleItemWidget;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.List;

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
        values = items == null ? new ArrayList<>() : items;
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
        En_ResultStatus validationStatus = ScheduleValidator.isValidSchedule(values);
        if (validationStatus == En_ResultStatus.OK) {
            hideError();
            return;
        }
        showError(statusLang.getMessage(validationStatus));
    }

    private void fillItem(ScheduleItem value) {
        ScheduleItemWidget itemWidget = new ScheduleItemWidget();
        itemWidget.setDays(ScheduleFormatterClient.getDays(value));
        itemWidget.setTimes(ScheduleFormatterClient.getTimeRanges(value));
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
        En_ResultStatus status = ScheduleValidator.isValidSchedule(values);
        if (status == En_ResultStatus.OK) {
            hideError();
            return;
        }
        showError(statusLang.getMessage(status));
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

    @Inject
    static En_ResultStatusLang statusLang;

    private List<ScheduleItem> values = new ArrayList<>();

    interface ScheduleListWidgetBinder extends UiBinder<HTMLPanel, ScheduleListWidget> {}
    private static ScheduleListWidgetBinder ourUiBinder = GWT.create(ScheduleListWidgetBinder.class);
}
