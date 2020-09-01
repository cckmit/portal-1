package ru.protei.portal.ui.roomreservation.client.util;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;

import java.util.Date;
import java.util.Objects;

public class AccessUtil {

    public static boolean canView(PolicyService policyService) {
        return policyService.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW);
    }

    public static boolean canCreate(PolicyService policyService) {
        return policyService.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_CREATE);
    }

    public static boolean canEdit(PolicyService policyService) {
        return policyService.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_EDIT);
    }

    public static boolean canEdit(PolicyService policyService, RoomReservation reservation) {
        boolean isReservationFinished = isReservationFinished(reservation);
        boolean hasAccess = hasAccessToReservation(policyService, En_Privilege.ROOM_RESERVATION_EDIT, reservation);
        return !isReservationFinished && hasAccess;
    }

    public static boolean canRemove(PolicyService policyService, RoomReservation reservation) {
        boolean isReservationStarted = isReservationStarted(reservation);
        boolean hasAccess = hasAccessToReservation(policyService, En_Privilege.ROOM_RESERVATION_REMOVE, reservation);
        return !isReservationStarted && hasAccess;
    }

    public static boolean hasAccessToReservation(PolicyService policyService, En_Privilege privilege, RoomReservation reservation) {
        Long personId = policyService.getProfile().getId();
        boolean isRequester = reservation.getPersonRequester() != null && Objects.equals(reservation.getPersonRequester().getId(), personId);
        boolean isResponsible = reservation.getPersonResponsible() != null && Objects.equals(reservation.getPersonResponsible().getId(), personId);
        boolean isPrivileged = policyService.hasPrivilegeFor(privilege);
        boolean isAdmin = policyService.hasSystemScopeForPrivilege(privilege);
        boolean isUserWithAccess = isPrivileged && (isRequester || isResponsible);
        return isAdmin || isUserWithAccess;
    }

    public static boolean hasAccessToRoom(PolicyService policyService, En_Privilege privilege, RoomReservable room) {
        if (room == null) {
            return true;
        }
        if (!room.isActive()) {
            return false;
        }
        if (room.isRestricted()) {
            boolean isAdmin = policyService.hasSystemScopeForPrivilege(privilege);
            return isAdmin;
        }
        return true;
    }

    private static boolean isReservationStarted(RoomReservation reservation) {
        Date now = new Date();
        return now.after(reservation.getDateFrom());
    }

    private static boolean isReservationFinished(RoomReservation reservation) {
        Date now = new Date();
        return now.after(reservation.getDateUntil());
    }

    public static boolean hasAccessToRoomView(PolicyService policyService) {
        return policyService.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW);
    }
}
