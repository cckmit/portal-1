package ru.protei.portal.ui.roomreservation.client.widget.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;
import ru.protei.portal.ui.roomreservation.client.activity.table.filter.AbstractRoomReservationParamWidget;
import ru.protei.portal.ui.roomreservation.client.widget.selector.room.RoomReservationMultiSelector;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dict.En_SortField.room_reservation_date_from;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class RoomReservationParamWidget extends Composite implements AbstractRoomReservationParamWidget {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        fillDateRanges(dateRange);
        resetFilter();
    }

    public void setOnFilterChangeCallback(Runnable onFilterChangeCallback) {
        this.onFilterChangeCallback = onFilterChangeCallback;
    }

    @Override
    public void resetFilter() {
        dateRange.setValue(new DateIntervalWithType(null, En_DateIntervalType.THIS_WEEK));
        room.setValue(null);
        if (isAttached()) {
            onFilterChanged();
        }
    }

    @Override
    public RoomReservationQuery getQuery() {
        RoomReservationQuery query = new RoomReservationQuery();
        query.setRoomIds(emptyIfNull(room.getValue()).stream().map(RoomReservable::getId).collect(Collectors.toSet()));
        query.setDateRange(DateIntervalWithType.toDateRange(dateRange.getValue()));
        query.setSortField(room_reservation_date_from);
        return query;
    }

    @Override
    public void setValidateCallback(Consumer<Boolean> callback) {
        validateCallback = callback;
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        if (validate()) {
            onFilterChanged();
        }
    }

    @UiHandler("room")
    public void onRoomSelected(ValueChangeEvent<Set<RoomReservable>> event) {
        onFilterChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        dateRange.setEnsureDebugId(DebugIds.ROOM_RESERVATION.FILTER.DATE_RANGE_INPUT);
        room.setAddEnsureDebugId(DebugIds.ROOM_RESERVATION.FILTER.ROOM_SELECTOR_ADD_BUTTON);
        room.setClearEnsureDebugId(DebugIds.ROOM_RESERVATION.FILTER.ROOM_SELECTOR_CLEAR_BUTTON);
        room.setItemContainerEnsureDebugId(DebugIds.ROOM_RESERVATION.FILTER.ROOM_SELECTOR_ITEM_CONTAINER);
        room.setLabelEnsureDebugId(DebugIds.ROOM_RESERVATION.FILTER.ROOM_SELECTOR_LABEL);
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

    private void onFilterChanged() {
        if (onFilterChangeCallback != null) {
            onFilterChangeCallback.run();
        }
    }

    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker dateRange;
    @Inject
    @UiField(provided = true)
    RoomReservationMultiSelector room;
    @Inject
    @UiField
    Lang lang;

    private Consumer<Boolean> validateCallback;
    private Runnable onFilterChangeCallback;

    private static RoomReservationParamWidgetUiBinder ourUiBinder = GWT.create(RoomReservationParamWidgetUiBinder.class);
    interface RoomReservationParamWidgetUiBinder extends UiBinder<HTMLPanel, RoomReservationParamWidget> {}
}
