package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;

import java.util.List;

public interface RoomReservationControllerAsync {

    void getReservations(RoomReservationQuery query, AsyncCallback<List<RoomReservation>> async);

    void getReservation(Long reservationId, AsyncCallback<RoomReservation> async);

    void createReservations(List<RoomReservation> reservations, AsyncCallback<List<RoomReservation>> async);

    void updateReservation(RoomReservation reservation, AsyncCallback<RoomReservation> async);

    void removeReservation(Long reservationId, AsyncCallback<RoomReservation> async);

    void getRooms(AsyncCallback<List<RoomReservable>> async);
}
