package ru.protei.portal.ui.ipreservation.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.core.service.IpReservationService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.IpReservationController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса управления резервированием IP-адресов
 */
@Service( "IpReservationController" )
public class IpReservationControllerImpl implements IpReservationController {

    @Override
    public SearchResult< Subnet > getSubnetList( ReservedIpQuery query ) throws RequestFailedException {

        log.info("getSubnetList(): query={}", query);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(ipReservationService.getSubnets(token, query));
    }

    @Override
    public List<SubnetOption> getSubnetsOptionList(ReservedIpQuery query) throws RequestFailedException {

        log.info("getSubnetsOptionList(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<SubnetOption>> response = ipReservationService.getSubnetsOptionList(token, query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Subnet getSubnet( Long subnetId ) throws RequestFailedException {

        log.info( "getSubnet(): id={}", subnetId );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Subnet > response = ipReservationService.getSubnet( token, subnetId );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "getSubnet(): id={}", response.getData() );

        return response.getData();
    }

    @Override
    public Subnet saveSubnet( Subnet subnet ) throws RequestFailedException {

        log.info( "saveSubnet(): subnet={}", subnet );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Subnet> response = subnet.getId() == null
                ? ipReservationService.createSubnet( token, subnet )
                : ipReservationService.updateSubnet( token, subnet );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "saveSubnet(): response.getData()={}", response.getData() );

        return response.getData();
    }

    @Override
    public Long getFreeIpsCountBySubnets(List<Long> subnetIds) throws RequestFailedException {

        log.info( "getFreeIpCountBySubnets(): selected subnets size={}",
                CollectionUtils.isEmpty(subnetIds) ? 0 : subnetIds.size());

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(ipReservationService.getFreeIpsCountBySubnets(token, subnetIds));
    }

    @Override
    public SearchResult<ReservedIp> getReservedIpList( ReservedIpQuery reservedIpQuery ) throws RequestFailedException {

        log.info( "getReservedIpList(): search={} | sortField={} | order={}",
                reservedIpQuery.getSearchString(), reservedIpQuery.getSortField(), reservedIpQuery.getSortDir() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(ipReservationService.getReservedIps(token, reservedIpQuery));
    }

    @Override
    public ReservedIp getReservedIp( Long reservedId ) throws RequestFailedException {

        log.info( "getReservedIp(): id={}", reservedId );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< ReservedIp > response = ipReservationService.getReservedIp( token, reservedId );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "getReservedIp(): id={}", response.getData() );

        return response.getData();
    }

    @Override
    public List<ReservedIp> createReservedIp( ReservedIpRequest reservedIpRequest ) throws RequestFailedException {

        log.info( "createReservedIp(): reservedIpRequest={}", reservedIpRequest );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        if ( reservedIpRequest == null )
            throw new RequestFailedException (En_ResultStatus.INCORRECT_PARAMS);

        Result<List<ReservedIp>> response = ipReservationService.createReservedIp( token, reservedIpRequest );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "createReservedIp(): response.getData()={}", response.getData() );

        return response.getData();
    }

    @Override
    public ReservedIp updateReservedIp( ReservedIp reservedIp ) throws RequestFailedException {

        log.info( "updateReservedIp(): reservedIp={}", reservedIp );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        if ( reservedIp == null )
            throw new RequestFailedException (En_ResultStatus.INCORRECT_PARAMS);

        Result<ReservedIp> response = ipReservationService.updateReservedIp( token, reservedIp );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "updateReservedIp(): response.getData()={}", response.getData() );

        return response.getData();
    }

    @Override
    public Long removeSubnet(Subnet subnet, boolean removeWithIps) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Long> response = ipReservationService.removeSubnet(token, subnet, removeWithIps);

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long removeReservedIp(ReservedIp reservedIp) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Long> response = ipReservationService.removeReservedIp(token, reservedIp);

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Boolean isSubnetAddressExists( String address, Long excludeId ) throws RequestFailedException {

        log.info( "isSubnetAddressExists(): address={} | excludeId={}", address, excludeId );

        Result<Boolean> response = ipReservationService.isSubnetAddressExists( address, excludeId );

        log.info( "isSubnetAddressExists(): response.isOk()={} | response.getData() = {}", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public Boolean isReservedIpAddressExists(String address) throws RequestFailedException {

        log.info("isReservedIpAddressExists(): address={} | reserveDate={} | reserveDate={} | dateIntervalType={} | excludeId={}", address);

        Result<Boolean> response = ipReservationService.isReservedIpAddressExists( address );

        log.info( "isReservedIpAddressExists(): response.isOk()={} | response.getData() = {}", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public Boolean isSubnetAvailableToRemove( Long subnetId ) throws RequestFailedException {
        log.info( "isSubnetAvailableToRemove(): subnetId={}", subnetId );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = ipReservationService.isSubnetAvailableToRemove(token, subnetId);

        log.info( "isSubnetAvailableToRemove(): response.isOk()={} | response.getData() = {}", response.isOk(), response.getData() );

        if ( response.isError() ) throw new RequestFailedException(response.getStatus());

        return response.getData();
    }

    @Override
    public Boolean isIpOnline(ReservedIp reservedIp) throws RequestFailedException {
        log.info( "refreshIp(): reservedIp={}", reservedIp );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        if ( reservedIp == null )
            throw new RequestFailedException (En_ResultStatus.INCORRECT_PARAMS);

        Result<Boolean> response = ipReservationService.isIpOnline( token, reservedIp );

        log.info( "refreshIp(): response={}", response );

        return ServiceUtils.checkResultAndGetData(response);
    }

    @Autowired
    private IpReservationService ipReservationService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(IpReservationControllerImpl.class);
}
