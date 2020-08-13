package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.RoomReservationDAO;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.winter.jdbc.annotations.EnumType;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.winter.jdbc.JdbcHelper.makeSqlStringCollection;

public class RoomReservationDAO_Impl extends PortalBaseJdbcDAO<RoomReservation> implements RoomReservationDAO {

    @Override
    public List<RoomReservation> listByRoomAndDateBounds(Long roomId, Date from, Date until) {
        return getListByCondition("room_reservation.room_id = ? AND (" +
                    "(? < room_reservation.date_from AND room_reservation.date_from < ?) OR " +
                    "(? < room_reservation.date_until AND room_reservation.date_until < ?) OR " +
                    "(room_reservation.date_from < ? AND ? < room_reservation.date_until) OR " +
                    "(room_reservation.date_from < ? AND ? < room_reservation.date_until) OR " +
                    "(room_reservation.date_from = ? AND room_reservation.date_until = ?)" +
                ")", roomId, from, until, from, until, from, from, until, until, from, until);
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(RoomReservationQuery query) {
        return new SqlCondition().build((condition, args) -> {

            condition.append("1=1");

            if (query == null) {
                return;
            }

            if (isNotEmpty(query.getRoomIds())) {
                condition.append(" and room_reservation.room_id in ")
                        .append(makeSqlStringCollection(query.getRoomIds(), args, null));
            }

            if (query.getPersonRequesterIds() != null) {
                condition.append(" and room_reservation.person_requester_id in ")
                        .append(makeSqlStringCollection(query.getPersonRequesterIds(), args, null));
            }

            if (query.getPersonResponsibleIds() != null) {
                condition.append(" and room_reservation.person_responsible_id in ")
                        .append(makeSqlStringCollection(query.getPersonResponsibleIds(), args, null));
            }

            if (query.getReasons() != null) {
                condition.append(" and room_reservation.reason_id in ")
                        .append(makeSqlStringCollection(query.getReasons(), args, EnumType.ID));
            }

            Interval date = makeInterval(query.getDate());

            if ( date != null ) {
                if (date.from != null) {
                    condition.append( " and room_reservation.date_from >= ?" );
                    args.add( date.from );
                }
                if (date.to != null) {
                    condition.append( " and room_reservation.date_until <= ?" );
                    args.add( date.to );
                }
            }
        });
    }
}
