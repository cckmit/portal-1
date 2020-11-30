package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/RoomReservationController")
public interface RoomReservationController extends RemoteService {

    SearchResult<RoomReservation> getReservations(RoomReservationQuery query) throws RequestFailedException;

    RoomReservation getReservation(Long reservationId) throws RequestFailedException;

    List<RoomReservation> createReservations(List<RoomReservation> reservations) throws RequestFailedException;

    RoomReservation updateReservation(RoomReservation reservation) throws RequestFailedException;

    Long removeReservation(Long reservationId) throws RequestFailedException;

    List<RoomReservable> getRooms() throws RequestFailedException;
}
