package ru.protei.portal.ui.roomreservation.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_RoomReservationReason;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.text.RawTextButtonSelector;
import ru.protei.portal.ui.roomreservation.client.activity.edit.AbstractRoomReservationEditActivity;
import ru.protei.portal.ui.roomreservation.client.activity.edit.AbstractRoomReservationEditView;
import ru.protei.portal.ui.roomreservation.client.widget.datetime.HasVaryAbility;
import ru.protei.portal.ui.roomreservation.client.widget.datetime.RoomReservationDates;
import ru.protei.portal.ui.roomreservation.client.widget.selector.reason.RoomReservationReasonButtonSelector;
import ru.protei.portal.ui.roomreservation.client.widget.selector.room.RoomReservableButtonSelector;

import java.util.List;
import java.util.Set;

public class RoomReservationEditView extends Composite implements AbstractRoomReservationEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        reason.addBtnStyleName("form-control");
        coffeeBreakCount.addBtnStyleName("form-control");
    }

    @Override
    public void setActivity(AbstractRoomReservationEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasVisibility loadingVisibility() {
        return loading;
    }

    @Override
    public HasVisibility contentVisibility() {
        return content;
    }

    @Override
    public void fillCoffeeBreakCountOptions(List<String> options) {
        coffeeBreakCount.fillOptions(options);
    }

    @Override
    public HasValue<PersonShortView> personResponsible() {
        return personResponsible;
    }

    @Override
    public HasValue<RoomReservable> room() {
        return room;
    }

    @Override
    public HasValue<En_RoomReservationReason> reason() {
        return reason;
    }

    @Override
    public HasValue<String> coffeeBreakCount() {
        return coffeeBreakCount;
    }

    @Override
    public HasValue<List<DateInterval>> dates() {
        return dates;
    }

    @Override
    public HasValue<Set<PersonShortView>> notifiers() {
        return notifiers;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasEnabled personResponsibleEnabled() {
        return personResponsible;
    }

    @Override
    public HasEnabled roomEnabled() {
        return room;
    }

    @Override
    public HasEnabled reasonEnabled() {
        return reason;
    }

    @Override
    public HasEnabled coffeeBreakCountEnabled() {
        return coffeeBreakCount;
    }

    @Override
    public HasEnabled datesEnabled() {
        return dates;
    }

    @Override
    public HasEnabled notifiersEnabled() {
        return notifiers;
    }

    @Override
    public HasEnabled commentEnabled() {
        return comment;
    }

    @Override
    public HasVaryAbility datesVaryAbility() {
        return dates;
    }

    @Override
    public HasVisibility roomAccessibilityVisibility() {
        return roomNotAccessiblePanel;
    }

    @UiHandler("room")
    public void roomChanged(ValueChangeEvent<RoomReservable> event) {
        if (activity != null) {
            activity.onRoomChanged(event.getValue());
        }
    }

    @UiField
    HTMLPanel root;
    @UiField
    IndeterminateCircleLoading loading;
    @UiField
    HTMLPanel content;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector personResponsible;
    @Inject
    @UiField(provided = true)
    RoomReservableButtonSelector room;
    @UiField
    HTMLPanel roomNotAccessiblePanel;
    @Inject
    @UiField(provided = true)
    RoomReservationReasonButtonSelector reason;
    @Inject
    @UiField(provided = true)
    RawTextButtonSelector coffeeBreakCount;
    @Inject
    @UiField(provided = true)
    RoomReservationDates dates;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector notifiers;
    @UiField
    AutoResizeTextArea comment;

    private AbstractRoomReservationEditActivity activity;

    interface RoomReservationEditViewUiBinder extends UiBinder<Widget, RoomReservationEditView> {}
    private static RoomReservationEditViewUiBinder ourUiBinder = GWT.create(RoomReservationEditViewUiBinder.class);
}
