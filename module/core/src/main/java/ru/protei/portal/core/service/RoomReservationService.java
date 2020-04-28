package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;

import java.util.List;

public interface RoomReservationService {

    @Privileged({ En_Privilege.ROOM_RESERVATION_VIEW })
    Result<List<RoomReservation>> getReservations(AuthToken token, RoomReservationQuery query);

    @Privileged({ En_Privilege.ROOM_RESERVATION_VIEW })
    Result<RoomReservation> getReservation(AuthToken token, Long reservationId);

    @Privileged({ En_Privilege.ROOM_RESERVATION_CREATE })
    @Auditable( En_AuditType.ROOM_RESERVATION_CREATE )
    Result<List<RoomReservation>> createReservations(AuthToken token, List<RoomReservation> reservations);

    @Privileged({ En_Privilege.ROOM_RESERVATION_EDIT })
    @Auditable( En_AuditType.ROOM_RESERVATION_MODIFY )
    Result<RoomReservation> updateReservation(AuthToken token, RoomReservation reservation);

    @Privileged({ En_Privilege.ROOM_RESERVATION_REMOVE })
    @Auditable( En_AuditType.ROOM_RESERVATION_REMOVE )
    Result<RoomReservation> removeReservation(AuthToken token, Long reservationId);

    Result<List<RoomReservable>> getRooms(AuthToken token);
}
