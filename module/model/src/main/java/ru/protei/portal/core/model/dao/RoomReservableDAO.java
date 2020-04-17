package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RoomReservable;

import java.util.List;

public interface RoomReservableDAO extends PortalBaseDAO<RoomReservable> {
    List<RoomReservable> listActiveRooms();
}
