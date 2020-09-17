package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.RoomReservationNotificationEvent;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.RoomReservableDAO;
import ru.protei.portal.core.model.dao.RoomReservationDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class RoomReservationServiceImpl implements RoomReservationService {

    @Override
    public Result<SearchResult<RoomReservation>> getReservations(AuthToken authToken, RoomReservationQuery query) {
        SearchResult<RoomReservation> result = roomReservationDAO.getSearchResultByQuery(query);
        return ok(result);
    }

    @Override
    public Result<RoomReservation> getReservation(AuthToken token, Long reservationId) {
        RoomReservation result = getReservation(reservationId);
        if (result == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        return ok(result);
    }

    @Override
    @Transactional
    public Result<List<RoomReservation>> createReservations(AuthToken token, List<RoomReservation> reservations) {

        for (RoomReservation reservation : reservations) {

            boolean valid = isValid(reservation);
            if (!valid) {
                return error(En_ResultStatus.INCORRECT_PARAMS);
            }

            boolean finished = isReservationFinished(reservation);
            if (finished) {
                return error(En_ResultStatus.ROOM_RESERVATION_FINISHED);
            }

            boolean hasAccessToRoom = hasAccessToRoom(token, En_Privilege.ROOM_RESERVATION_CREATE, findRoom(getActiveRooms(), reservation.getRoom()));
            if (!hasAccessToRoom) {
                return error(En_ResultStatus.ROOM_RESERVATION_ROOM_NOT_ACCESSIBLE);
            }

            boolean hasIntersections = hasReservationIntersections(reservation, emptyList());
            if (hasIntersections) {
                return error(En_ResultStatus.ROOM_RESERVATION_HAS_INTERSECTIONS);
            }
        }

        List<RoomReservation> result = stream(reservations)
            .map(reservation -> {
                Long id = persistReservation(reservation, new PersonShortView(token.getPersonId()));
                if (id == null) {
                    throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
                }
                return id;
            })
            .map(this::getReservation)
            .collect(Collectors.toList());

        List<ApplicationEvent> events = stream(result)
            .map(reservation -> new RoomReservationNotificationEvent(
                this,
                reservation,
                RoomReservationNotificationEvent.Action.CREATED,
                makeNotificationList(reservation)
            ))
            .collect(Collectors.toList());

        return ok(result)
            .publishEvents(events);
    }

    @Override
    @Transactional
    public Result<RoomReservation> updateReservation(AuthToken token, RoomReservation reservation) {

        boolean valid = isValid(reservation) && token != null && reservation.getId() != null;
        if (!valid) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        RoomReservation stored = getReservation(reservation.getId());
        if (stored == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        boolean requesterValid = idRequesterValid(reservation, stored);
        if (!requesterValid) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean finished = isReservationFinished(reservation);
        if (finished) {
            return error(En_ResultStatus.ROOM_RESERVATION_FINISHED);
        }

        boolean hasAccess = hasAccessToReservation(token, En_Privilege.ROOM_RESERVATION_EDIT, stored);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        boolean hasAccessToRoom = hasAccessToRoom(token, En_Privilege.ROOM_RESERVATION_EDIT, findRoom(getActiveRooms(), reservation.getRoom()));
        if (!hasAccessToRoom) {
            return error(En_ResultStatus.ROOM_RESERVATION_ROOM_NOT_ACCESSIBLE);
        }

        boolean hasIntersections = hasReservationIntersections(reservation, listOf(reservation.getId()));
        if (hasIntersections) {
            return error(En_ResultStatus.ROOM_RESERVATION_HAS_INTERSECTIONS);
        }

        boolean merged = mergeReservation(reservation, stored);
        if (!merged) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        RoomReservation result = getReservation(reservation.getId());
        return ok(result)
            .publishEvent(new RoomReservationNotificationEvent(
                this,
                result,
                RoomReservationNotificationEvent.Action.UPDATED,
                makeNotificationList(result)
            ));
    }

    @Override
    @Transactional
    public Result<RoomReservation> removeReservation(AuthToken token, Long reservationId) {

        boolean valid = token != null && reservationId != null;
        if (!valid) {
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

        boolean hasAccess = hasAccessToReservation(token, En_Privilege.ROOM_RESERVATION_REMOVE, stored);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        boolean removed = roomReservationDAO.removeByKey(reservationId);
        if (!removed) {
            return error(En_ResultStatus.NOT_REMOVED);
        }

        return ok(stored)
            .publishEvent(new RoomReservationNotificationEvent(
                this,
                stored,
                RoomReservationNotificationEvent.Action.REMOVED,
                makeNotificationList(stored)
            ));
    }

    @Override
    public Result<List<RoomReservable>> getRooms(AuthToken token) {
        List<RoomReservable> rooms = getActiveRooms();
        return ok(rooms);
    }

    private RoomReservation getReservation(Long reservationId) {
        RoomReservation reservation = roomReservationDAO.get(reservationId);
        if (reservation != null) {
            jdbcManyRelationsHelper.fill(reservation, "personsToBeNotified");
        }
        return reservation;
    }

    private Long persistReservation(RoomReservation reservation, PersonShortView requester) {
        reservation.setId(null);
        reservation.setDateRequested(new Date());
        reservation.setPersonRequester(requester);
        Long id = roomReservationDAO.persist(reservation);
        boolean persisted = id != null;
        if (persisted) {
            jdbcManyRelationsHelper.persist(reservation, "personsToBeNotified");
        }
        return id;
    }

    private boolean mergeReservation(RoomReservation reservation, RoomReservation previous) {
        reservation.setDateRequested(previous.getDateRequested());
        reservation.setPersonRequester(previous.getPersonRequester());
        boolean merged = roomReservationDAO.merge(reservation);
        if (merged) {
            jdbcManyRelationsHelper.persist(reservation, "personsToBeNotified");
        }
        return merged;
    }

    private List<RoomReservable> getActiveRooms() {
        List<RoomReservable> rooms = roomReservableDAO.listActiveRooms();
        return rooms;
    }

    private boolean isValid(RoomReservation reservation) {
        if (reservation == null) {
            return false;
        }
        if (reservation.getPersonResponsible() == null) {
            return false;
        }
        if (reservation.getRoom() == null) {
            return false;
        }
        if (reservation.getReason() == null) {
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

    private boolean idRequesterValid(RoomReservation reservation, RoomReservation previous) {
        boolean requesterWasSet = previous.getPersonRequester() != null;
        if (!requesterWasSet) {
            return true;
        }
        boolean requesterSet = reservation.getPersonRequester() != null;
        if (!requesterSet) {
            return false;
        }
        boolean requesterMatchedWithPreviousState = Objects.equals(previous.getPersonRequester().getId(), reservation.getPersonRequester().getId());
        return requesterMatchedWithPreviousState;
    }

    private boolean isReservationStarted(RoomReservation reservation) {
        Date now = new Date();
        return now.after(reservation.getDateFrom());
    }

    private boolean isReservationFinished(RoomReservation reservation) {
        Date now = new Date();
        return now.after(reservation.getDateUntil());
    }

    private boolean hasAccessToReservation(AuthToken token, En_Privilege privilege, RoomReservation reservation) {
        Long personId = token.getPersonId();
        Set<UserRole> roles = token.getRoles();
        boolean isRequester = reservation.getPersonRequester() != null && Objects.equals(reservation.getPersonRequester().getId(), personId);
        boolean isResponsible = reservation.getPersonResponsible() != null && Objects.equals(reservation.getPersonResponsible().getId(), personId);
        boolean isPrivileged = policyService.hasPrivilegeFor(privilege, roles);
        boolean isAdmin = policyService.hasScopeForPrivilege(roles, privilege, En_Scope.SYSTEM);
        boolean isUserWithAccess = isPrivileged && (isRequester || isResponsible);
        return isAdmin || isUserWithAccess;
    }

    private boolean hasAccessToRoom(AuthToken token, En_Privilege privilege, RoomReservable room) {
        if (room == null) {
            return false;
        }
        if (!room.isActive()) {
            return false;
        }
        if (room.isRestricted()) {
            Set<UserRole> roles = token.getRoles();
            boolean isAdmin = policyService.hasScopeForPrivilege(roles, privilege, En_Scope.SYSTEM);
            return isAdmin;
        }
        return true;
    }

    private boolean hasReservationIntersections(RoomReservation reservation, List<Long> excludeIds) {
        return stream(roomReservationDAO.listByRoomAndDateBounds(
            reservation.getRoom().getId(),
            reservation.getDateFrom(),
            reservation.getDateUntil()
        )).anyMatch(r -> !excludeIds.contains(r.getId()));
    }

    private RoomReservable findRoom(List<RoomReservable> availableRooms, RoomReservable room) {
        for (RoomReservable availableRoom : availableRooms) {
            if (Objects.equals(availableRoom.getId(), room.getId())) {
                return availableRoom;
            }
        }
        return null;
    }

    private List<NotificationEntry> makeNotificationList(RoomReservation reservation) {
        return Stream.of(
            makeNotificationListFromReservation(reservation),
            makeNotificationListFromConfiguration()
        )
            .flatMap(Collection::stream) // flatten
            .distinct()
            .collect(Collectors.toList());
    }

    private List<NotificationEntry> makeNotificationListFromReservation(RoomReservation reservation) {
        List<Long> ids = fetchAllPersonIds(reservation);
        List<Person> people = personDAO.partialGetListByKeys(ids, "locale");
        jdbcManyRelationsHelper.fill(people, Person.Fields.CONTACT_ITEMS);
        return stream(people)
            .filter(Objects::nonNull)
            .map(person -> {
                PlainContactInfoFacade contact = new PlainContactInfoFacade(person.getContactInfo());
                return NotificationEntry.email(contact.getEmail(), person.getLocale());
            })
            .filter(entry -> StringUtils.isNotEmpty(entry.getAddress()))
            .collect(Collectors.toList());
    }

    private List<NotificationEntry> makeNotificationListFromConfiguration() {
        return stream(Arrays.asList(config.data().getMailNotificationConfig().getCrmRoomReservationNotificationsRecipients()))
            .filter(Objects::nonNull)
            .filter(not(String::isEmpty))
            .map(address -> NotificationEntry.email(address, "ru"))
            .collect(Collectors.toList());
    }

    private List<Long> fetchAllPersonIds(RoomReservation reservation) {
        return stream(new ArrayList<PersonShortView>() {{
            addAll(reservation.getPersonsToBeNotified());
            add(reservation.getPersonRequester());
            add(reservation.getPersonResponsible());
        }})
                .filter(Objects::nonNull)
                .map(PersonShortView::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Autowired
    PortalConfig config;
    @Autowired
    RoomReservationDAO roomReservationDAO;
    @Autowired
    RoomReservableDAO roomReservableDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    PolicyService policyService;
}
