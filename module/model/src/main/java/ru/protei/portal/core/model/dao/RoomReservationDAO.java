package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.Date;
import java.util.List;

public interface RoomReservationDAO extends PortalBaseDAO<RoomReservation> {

    List<RoomReservation> listByRoomAndDateBounds(Long roomId, Date from, Date until);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(RoomReservationQuery query);
}
