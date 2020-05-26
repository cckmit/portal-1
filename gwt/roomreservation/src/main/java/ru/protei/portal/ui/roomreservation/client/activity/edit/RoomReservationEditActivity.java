package ru.protei.portal.ui.roomreservation.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.RoomReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.service.RoomReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.gwt.user.datepicker.client.CalendarUtil.copyDate;
import static java.lang.Integer.parseInt;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.roomreservation.client.util.AccessUtil.*;

public abstract class RoomReservationEditActivity implements Activity, AbstractRoomReservationEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.setSaveOnEnterClick(false);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onAuth(AuthEvents.Success event) {
        currentPerson = null;
        getPerson(event.profile.getId(), personShortView -> {
            currentPerson = personShortView;
        });
    }

    @Event
    public void onCreate(RoomReservationEvents.Create event) {
        boolean hasAccess = canCreate(policyService);
        if (!hasAccess) {
            return;
        }
        hideForm();
        hideLoading();
        showPopup(true);
        showForm(makeBlankReservation(
            event.room,
            event.date != null
                ? event.date
                : addOneHour(new Date())
        ));
    }

    @Event
    public void onEdit(RoomReservationEvents.Edit event) {
        boolean hasAccess = canView(policyService) || canEdit(policyService);
        if (!hasAccess) {
            return;
        }
        hideForm();
        hideLoading();
        showPopup(false);
        loadReservation(event.roomReservationId, this::showForm);
    }

    @Override
    public void onSaveClicked() {
        if (reservation == null) {
            return;
        }
        boolean isNew = reservation.getId() == null;
        List<RoomReservation> reservations = fillDtos(reservation);
        if (isNew) {
            roomReservationController.createReservations(reservations, new FluentCallback<List<RoomReservation>>()
                .withSuccess(result -> {
                    onCancelClicked();
                    fireEvent(new NotifyEvents.Show(lang.roomReservationSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new RoomReservationEvents.Reload());
                }));
        } else {
            if (reservations.size() != 1) {
                return;
            }
            RoomReservation reservation = reservations.get(0);
            roomReservationController.updateReservation(reservation, new FluentCallback<RoomReservation>()
                .withSuccess(result -> {
                    onCancelClicked();
                    fireEvent(new NotifyEvents.Show(lang.roomReservationUpdated(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new RoomReservationEvents.Reload());
                }));
        }
    }

    @Override
    public void onRemoveClicked() {
        if (reservation == null) {
            return;
        }
        Long id = reservation.getId();
        if (id == null) {
            return;
        }
        roomReservationController.removeReservation(id, new FluentCallback<RoomReservation>()
            .withSuccess(result -> {
                onCancelClicked();
                fireEvent(new NotifyEvents.Show(lang.roomReservationRemoved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new RoomReservationEvents.Reload());
            }));
    }

    @Override
    public void onCancelClicked() {
        reservation = null;
        dialogView.hidePopup();
    }

    @Override
    public void onRoomChanged(RoomReservable room) {
        boolean isNew = reservation == null || reservation.getId() == null;
        boolean canCreate = isNew && canCreate(policyService);
        boolean canEdit = !isNew && canEdit(policyService, reservation);
        boolean hasAccessToRoom = hasAccessToRoom(
                policyService,
                isNew ? En_Privilege.ROOM_RESERVATION_CREATE : En_Privilege.ROOM_RESERVATION_EDIT,
                room);
        boolean canModify = hasAccessToRoom && (canCreate || canEdit);
        view.roomAccessibilityVisibility().setVisible(!hasAccessToRoom);
        dialogView.saveButtonVisibility().setVisible(canModify);
    }

    private void loadReservation(Long reservationId, Consumer<RoomReservation> onSuccess) {
        showLoading();
        roomReservationController.getReservation(reservationId, new FluentCallback<RoomReservation>()
            .withError(throwable -> {
                hideLoading();
                hideForm();
                defaultErrorHandler.accept(throwable);
            })
            .withSuccess(reservation -> {
                hideLoading();
                onSuccess.accept(reservation);
            }));
    }

    private void showPopup(boolean isNew) {
        dialogView.setHeader(isNew ? lang.roomReservationCreation() : lang.roomReservationEditing());
        dialogView.showPopup();
    }

    private void showForm(RoomReservation reservation) {
        view.contentVisibility().setVisible(true);
        if (!view.asWidget().isAttached()) {
            return;
        }
        fillView(reservation);
    }

    private void hideForm() {
        view.contentVisibility().setVisible(false);
    }

    private void showLoading() {
        view.loadingVisibility().setVisible(true);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(false);
    }

    private void hideLoading() {
        view.loadingVisibility().setVisible(false);
    }

    private void fillView(RoomReservation reservation) {
        this.reservation = reservation;

        boolean isNew = reservation.getId() == null;
        boolean canCreate = isNew && canCreate(policyService);
        boolean canEdit = !isNew && canEdit(policyService, reservation);
        boolean canRemove = !isNew && canRemove(policyService, reservation);
        boolean hasAccessToRoom = hasAccessToRoom(
                policyService,
                isNew ? En_Privilege.ROOM_RESERVATION_CREATE : En_Privilege.ROOM_RESERVATION_EDIT,
                reservation.getRoom());
        boolean canModify = hasAccessToRoom && (canCreate || canEdit);

        view.fillCoffeeBreakCountOptions(stream(COFFEE_BREAK_OPTIONS)
                .map(String::valueOf)
                .collect(Collectors.toList()));
        view.personResponsible().setValue(reservation.getPersonResponsible() != null
                ? reservation.getPersonResponsible().toFullNameShortView()
                : currentPerson);
        view.room().setValue(reservation.getRoom(), true);
        view.reason().setValue(reservation.getReason());
        view.coffeeBreakCount().setValue(reservation.getCoffeeBreakCount() != null
                ? String.valueOf(reservation.getCoffeeBreakCount())
                : String.valueOf(COFFEE_BREAK_OPTIONS.get(0)));
        view.dates().setValue(toDateIntervals(reservation));
        view.notifiers().setValue(stream(reservation.getPersonsToBeNotified())
                .map(Person::toFullNameShortView)
                .collect(Collectors.toSet()));
        view.comment().setValue(reservation.getComment());

        view.personResponsibleEnabled().setEnabled(canModify);
        view.roomEnabled().setEnabled(canModify);
        view.reasonEnabled().setEnabled(canModify);
        view.coffeeBreakCountEnabled().setEnabled(canModify);
        view.datesEnabled().setEnabled(canModify);
        view.notifiersEnabled().setEnabled(canModify);
        view.commentEnabled().setEnabled(canModify);

        view.datesVaryAbility().setVaryAble(canCreate);
        view.roomAccessibilityVisibility().setVisible(!hasAccessToRoom);

        dialogView.saveButtonVisibility().setVisible(canModify);
        dialogView.removeButtonVisibility().setVisible(canRemove);
    }

    private List<RoomReservation> fillDtos(RoomReservation base) {
        return stream(view.dates().getValue())
            .map(date -> {
                RoomReservation reservation = new RoomReservation();
                reservation.setId(base.getId());
                reservation.setPersonRequester(base.getPersonRequester());
                reservation.setDateRequested(base.getDateRequested());
                reservation.setPersonResponsible(view.personResponsible().getValue() != null
                        ? Person.fromPersonFullNameShortView(view.personResponsible().getValue())
                        : null);
                reservation.setRoom(view.room().getValue());
                reservation.setReason(view.reason().getValue());
                reservation.setCoffeeBreakCount(view.coffeeBreakCount().getValue() != null
                        ? parseInt(view.coffeeBreakCount().getValue())
                        : null);
                reservation.setDateFrom(date.from);
                reservation.setDateUntil(date.to);
                reservation.setPersonsToBeNotified(stream(view.notifiers().getValue())
                        .map(Person::fromPersonFullNameShortView)
                        .collect(Collectors.toList()));
                reservation.setComment(view.comment().getValue());
                return reservation;
            })
            .collect(Collectors.toList());
    }

    private RoomReservation makeBlankReservation(RoomReservable room, Date dateFrom) {
        RoomReservation reservation = new RoomReservation();
        reservation.setRoom(room);
        reservation.setDateFrom(copyDate(dateFrom));
        reservation.setDateUntil(addOneHour(dateFrom));
        return reservation;
    }

    private Date addOneHour(Date date) {
        Date d = copyDate(date);
        d.setHours(d.getHours() + 1);
        return d;
    }

    private List<DateInterval> toDateIntervals(RoomReservation reservation) {
        List<DateInterval> intervals = new ArrayList<>();
        if (reservation.getDateFrom() != null && reservation.getDateUntil() != null) {
            intervals.add(new DateInterval(
                copyDate(reservation.getDateFrom()),
                copyDate(reservation.getDateUntil())
            ));
        }
        return intervals;
    }

    private void getPerson(Long personId, Consumer<PersonShortView> onSuccess) {
        employeeController.getEmployee(personId, new FluentCallback<PersonShortView>().withSuccess(onSuccess));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractRoomReservationEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    PolicyService policyService;
    @Inject
    RoomReservationControllerAsync roomReservationController;
    @Inject
    EmployeeControllerAsync employeeController;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private PersonShortView currentPerson;
    private RoomReservation reservation;
    private static final List<Integer> COFFEE_BREAK_OPTIONS = Arrays.asList(0, 1, 2, 3, 4, 5);
}
