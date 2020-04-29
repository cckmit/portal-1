package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RoomReservableDAO;
import ru.protei.portal.core.model.ent.RoomReservable;

import java.util.List;

public class RoomReservableDAO_Impl extends PortalBaseJdbcDAO<RoomReservable> implements RoomReservableDAO {
    @Override
    public List<RoomReservable> listActiveRooms() {
        return getListByCondition("active IS TRUE");
    }
}
