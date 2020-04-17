package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.RoomReservableDAO;
import ru.protei.portal.core.model.dao.RoomReservationDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class RoomReservationServiceImpl implements RoomReservationService {

    @Override
    public Result<List<RoomReservation>> getReservations(AuthToken authToken, RoomReservationQuery query) {
        List<RoomReservation> result = roomReservationDAO.listByQuery(query);
        return ok(result);
    }

    @Override
    public Result<RoomReservation> getReservation(AuthToken token, Long reservationId) {
        RoomReservation result = getReservation(reservationId);
        return ok(result);
    }

    @Override
    public Result<RoomReservation> createReservation(AuthToken token, RoomReservation reservation) {

        if (!validate(reservation)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean outdated = isReservationFinished(reservation);
        if (outdated) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean hasAccessToRoom = hasAccessToRoom(token.getPersonId(), getActiveRooms(), reservation.getRoom());
        if (!hasAccessToRoom) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        reservation.setDateRequested(new Date());
        reservation.setPersonRequester(new Person(token.getPersonId()));

        Long id = roomReservationDAO.persist(reservation);
        if (id == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }
        jdbcManyRelationsHelper.persist(reservation, "personsToBeNotified");

        RoomReservation result = getReservation(id);
        return ok(result);
    }

    @Override
    public Result<RoomReservation> updateReservation(AuthToken token, RoomReservation reservation) {

        if (!validate(reservation) || token == null || reservation.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RoomReservation stored = getReservation(reservation.getId());
        if (stored == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (stored.getPersonRequester() != null && (
                reservation.getPersonRequester() == null
                || !Objects.equals(stored.getPersonRequester().getId(), reservation.getPersonRequester().getId())
        )) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean outdated = isReservationStarted(stored);
        if (outdated) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        boolean hasAccess = hasModificationAccessToReservation(token.getPersonId(), stored);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        boolean hasAccessToRoom = hasAccessToRoom(token.getPersonId(), getActiveRooms(), reservation.getRoom());
        if (!hasAccessToRoom) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        reservation.setDateRequested(stored.getDateRequested());
        reservation.setPersonRequester(stored.getPersonRequester());

        boolean merged = roomReservationDAO.merge(reservation);
        if (!merged) {
            return error(En_ResultStatus.NOT_UPDATED);
        }
        jdbcManyRelationsHelper.persist(reservation, "personsToBeNotified");

        RoomReservation result = getReservation(reservation.getId());
        return ok(result);
    }

    @Override
    public Result<RoomReservation> removeReservation(AuthToken token, Long reservationId) {

        if (token == null || reservationId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RoomReservation stored = getReservation(reservationId);
        if (stored == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        boolean outdated = isReservationStarted(stored);
        if (outdated) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        boolean hasAccess = hasModificationAccessToReservation(token.getPersonId(), stored);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        boolean removed = roomReservationDAO.removeByKey(reservationId);
        if (!removed) {
            return error(En_ResultStatus.NOT_REMOVED);
        }

        return ok(stored);
    }

    @Override
    public Result<List<RoomReservable>> getRooms(AuthToken token) {
        List<RoomReservable> rooms = getActiveRooms();
        return ok(rooms);
    }

    private RoomReservation getReservation(Long reservationId) {
        RoomReservation reservation = roomReservationDAO.get(reservationId);
        jdbcManyRelationsHelper.fill(reservation, "personsToBeNotified");
        return reservation;
    }

    private List<RoomReservable> getActiveRooms() {
        List<RoomReservable> rooms = roomReservableDAO.listActiveRooms();
        jdbcManyRelationsHelper.fill(rooms, "personsAllowedToReserve");
        return rooms;
    }

    private boolean validate(RoomReservation reservation) {
        if (reservation == null) {
            return false;
        }
        if (reservation.getPersonResponsible() == null) {
            return false;
        }
        if (reservation.getRoom() == null) {
            return false;
        }
        if (reservation.getDateFrom() == null) {
            return false;
        }
        if (reservation.getDateUntil() == null) {
            return false;
        }
        if (reservation.getDateFrom().after(reservation.getDateUntil())) {
            return false;
        }
        if (reservation.getDateFrom().equals(reservation.getDateUntil())) {
            return false;
        }
        return true;
    }

    private boolean isReservationStarted(RoomReservation reservation) {
        Date now = new Date();
        return now.after(reservation.getDateFrom());
    }

    private boolean isReservationFinished(RoomReservation reservation) {
        Date now = new Date();
        return now.after(reservation.getDateUntil());
    }

    private boolean hasModificationAccessToReservation(Long personId, RoomReservation reservation) {
        // TODO check for superuser
        boolean isRequester = reservation.getPersonRequester() != null && Objects.equals(reservation.getPersonRequester().getId(), personId);
        boolean isResponsible = reservation.getPersonResponsible() != null && Objects.equals(reservation.getPersonResponsible().getId(), personId);
        return isRequester || isResponsible;
    }

    private boolean hasAccessToRoom(Long personId, List<RoomReservable> availableRooms, RoomReservable room) {
        for (RoomReservable availableRoom : availableRooms) {
            if (!Objects.equals(availableRoom.getId(), room.getId())) {
                continue;
            }
            if (!availableRoom.isActive()) {
                return false;
            }
            if (isNotEmpty(availableRoom.getPersonsAllowedToReserve())) {
                return stream(availableRoom.getPersonsAllowedToReserve())
                        .map(Person::getId)
                        .collect(Collectors.toList())
                        .contains(personId);
            }
            return true;
        }
        return false;
    }

    @Autowired
    RoomReservationDAO roomReservationDAO;
    @Autowired
    RoomReservableDAO roomReservableDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
}
