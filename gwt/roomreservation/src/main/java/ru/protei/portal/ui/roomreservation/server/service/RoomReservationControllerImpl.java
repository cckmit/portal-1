package ru.protei.portal.ui.roomreservation.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.core.service.RoomReservationService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.RoomReservationController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("RoomReservationController")
public class RoomReservationControllerImpl implements RoomReservationController {

    @Override
    public SearchResult<RoomReservation> getReservations(RoomReservationQuery query) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(roomReservationService.getReservations(token, query));
    }

    @Override
    public RoomReservation getReservation(Long reservationId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(roomReservationService.getReservation(token, reservationId));
    }

    @Override
    public List<RoomReservation> createReservations(List<RoomReservation> reservations) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(roomReservationService.createReservations(token, reservations));
    }

    @Override
    public RoomReservation updateReservation(RoomReservation reservation) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(roomReservationService.updateReservation(token, reservation));
    }

    @Override
    public RoomReservation removeReservation(Long reservationId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(roomReservationService.removeReservation(token, reservationId));
    }

    @Override
    public List<RoomReservable> getRooms() throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(roomReservationService.getRooms(token));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    RoomReservationService roomReservationService;
}
