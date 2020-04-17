package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface RoomReservationDAO extends PortalBaseDAO<RoomReservation> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition(RoomReservationQuery query);
}
