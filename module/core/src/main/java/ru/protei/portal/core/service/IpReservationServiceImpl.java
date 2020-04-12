package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

/**
 * Реализация сервиса управления подсистемой резервирования IP
 */
public class IpReservationServiceImpl implements IpReservationService {

    @Autowired
    SubnetDAO subnetDAO;

    @Autowired
    ReservedIpDAO reservedIpDAO;

    @Autowired
    JdbcManyRelationsHelper helper;

    @Override
    public Result<Boolean> isSubnetAddressExists( String address, Long excludeId) {

        if( address == null || address.isEmpty() )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkSubnetExists(address, excludeId));
    }

    @Override
    public Result<Boolean> isReservedIpAddressExists( String address, Long excludeId) {

        if( address == null || address.isEmpty() )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkReservedIpExists(address, excludeId));
    }

    @Override
    public Result<SearchResult<ReservedIp>> getReservedIps(AuthToken token, ReservedIpQuery query) {
        SearchResult<ReservedIp> sr = reservedIpDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    public Result<SearchResult<Subnet>> getSubnets(AuthToken token, ReservedIpQuery query) {
        SearchResult<Subnet> sr = subnetDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    public Result<List<SubnetOption>> getSubnetsOptionList(AuthToken token, ReservedIpQuery query) {

        List<Subnet> result = subnetDAO.listByQuery(query);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        List<SubnetOption> options = result.stream()
                .map(s -> new SubnetOption(s.getAddress() + "." +  s.getMask() + " " + s.getComment(),
                        s.getId()))
                .collect(Collectors.toList());

        return ok(options);
    }

    @Override
    public Result< Map< Subnet, List<ReservedIp>>> getReservedIpsBySubnets(AuthToken token, ReservedIpQuery query ) {
        Map< Subnet, List<ReservedIp> > subnetToReservedIpsMap = new HashMap<>();
        /*
         *  @todo
         *    по списку зарезервированных IP построить мапу с группировкой по подсетям
         */
        return ok( subnetToReservedIpsMap );
    }

    @Override
    public Result<Subnet> getSubnet( AuthToken token, Long id ) {
        Subnet subnet = subnetDAO.get(id);

        return subnet != null ? Result.ok( subnet)
                : error( En_ResultStatus.NOT_FOUND);
    }

    @Override
    public Result<ReservedIp> getReservedIp( AuthToken authToken, Long id ) {
        ReservedIp reservedIp = reservedIpDAO.get(id);

        return reservedIp != null ? Result.ok( reservedIp)
                : error( En_ResultStatus.NOT_FOUND);
    }

    @Override
    public Result<Subnet> createSubnet( AuthToken token, Subnet subnet) {

        if (!isValidSubnet(subnet)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (checkSubnetExists(subnet.getAddress(), subnet.getId()))
            return error(En_ResultStatus.ALREADY_EXIST);

        subnet.setCreated(new Date());
        subnet.setCreatorId(token.getPersonId());
        Long subnetId = subnetDAO.persist(subnet);

        if (subnetId == null)
            return error(En_ResultStatus.NOT_CREATED);

        subnet.setId(subnetId);
        return ok(subnet);
    }

    @Override
    public Result<Subnet> updateSubnet( AuthToken token, Subnet subnet ) {

        if (subnet.getId() == null || !isValidSubnet(subnet)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Subnet oldSubnet = subnetDAO.get(subnet.getId());

        if (!Objects.equals(oldSubnet.getAddress(), subnet.getAddress()) ||
            !Objects.equals(oldSubnet.getMask(), subnet.getMask())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Boolean result = subnetDAO.merge(subnet);
        if ( !result ) {
            return error(En_ResultStatus.NOT_UPDATED);
        }
        return ok(subnet);
    }

    @Override
    public Result<Boolean> isSubnetAvailableToRemove(AuthToken token, Long subnetId) {
        if( subnetId == null )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        ReservedIpQuery query = new ReservedIpQuery();
        query.setSubnetId(subnetId);
        Long result = reservedIpDAO.count(query);
        if (result == null || result == 0)
            return ok(true);

        return ok(false);
    }

    @Override
    public Result<Long> removeSubnet( AuthToken token, Subnet subnet) {
        if (subnet == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        if (!subnetDAO.removeByKey(subnet.getId()))
            return error(En_ResultStatus.INTERNAL_ERROR);

        return ok(subnet.getId());
    }

    @Override
    /* @todo задача резервирования долна выполняться в фоне? */
    public Result<List<ReservedIp>> createReservedIp( AuthToken token, ReservedIpRequest reservedIpRequest) {

        if (!isValidRequest(reservedIpRequest)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        ArrayList<ReservedIp> reservedIps = new ArrayList<>();

        ReservedIp reservedIp = new ReservedIp();
        reservedIp.setCreated(new Date());
        reservedIp.setCreatorId(token.getPersonId());
        reservedIp.setOwnerId(reservedIpRequest.getOwnerId());

        En_DateIntervalType intervalType = reservedIpRequest.getDateIntervalType();

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date throughMonth = calendar.getTime();

        switch (intervalType) {
            case UNLIMITED:
                reservedIp.setReserveDate(today);
                reservedIp.setReleaseDate(null);
                break;
            case MONTH:
                reservedIp.setReserveDate(today);
                reservedIp.setReleaseDate(throughMonth);
                break;
            case FIXED:
                reservedIp.setReserveDate(reservedIpRequest.getReserveDate());
                reservedIp.setReleaseDate(reservedIpRequest.getReserveDate());
        }

        reservedIp.setComment(reservedIpRequest.getComment());

        if (reservedIpRequest.isExact()) {
            if (checkReservedIpExists(reservedIpRequest.getIpAddress(), null)) {
                return error(En_ResultStatus.ALREADY_EXIST);
            }

            reservedIp.setIpAddress(reservedIpRequest.getIpAddress());
            reservedIp.setMacAddress(reservedIpRequest.getMacAddress());
            String subnetAddress = reservedIpRequest.getIpAddress().substring(0, reservedIpRequest.getIpAddress().lastIndexOf("."));
            Subnet subnet = subnetDAO.getSubnetByAddress(subnetAddress);
            if (subnet == null) {
                return error(En_ResultStatus.UNDEFINED_OBJECT);
            }
            reservedIp.setSubnetId(subnet.getId());

            Long reservedIpId = reservedIpDAO.persist(reservedIp);

            if (reservedIpId == null)
                return error(En_ResultStatus.NOT_CREATED);

            reservedIp.setId(reservedIpId);
            reservedIps.add(reservedIp);

        } else {
         /*
           @todo если задано "любая свободная подсеть"
                - проверка подсетей на наличие требуемого кол-ва свободных IP
                - пройти по всем подходящим подсетям, резервируя IP, попутно проверяя их через NRPE
         */
            //@todo number?
            //int reserved = 0;
            Set<SubnetOption> subnets;
            if (CollectionUtils.isNotEmpty(reservedIpRequest.getSubnets())) {
                subnets = reservedIpRequest.getSubnets();
            } else {
                List<SubnetOption> result = getSubnetsOptionList(token,
                        new ReservedIpQuery("", En_SortField.address, En_SortDir.ASC))
                        .getData();

                if (CollectionUtils.isEmpty(result)) {
                    return error(En_ResultStatus.INTERNAL_ERROR);
                }

                subnets = result.stream().collect(Collectors.toSet());
            }

            /*
               @todo
                 что делать, если в выбранных подсетях не хватает свободных IPшников
             */

            while (reservedIps.size() < reservedIpRequest.getNumber()) {
                ReservedIp ip = reservedIp;
                subnets.forEach( s -> {
                            if (hasSubnetFreeIps(s.getId())) {
                                String freeIp = getAnyFreeIpInSubnet(s.getId());
                                if (freeIp != null) {
                                    ip.setSubnetId(s.getId());
                                    ip.setIpAddress(freeIp);
                                    return;
                                }
                            }
                        }
                );
                if (ip.getIpAddress() == null || ip.getSubnetId() == null) {
                    break;
                }

                Long reservedIpId = reservedIpDAO.persist(ip);

                if (reservedIpId != null) {
                    reservedIps.add(ip);
                }
            }
        }

        /*
           @todo
             - проверка IP через NRPE
             - update IP с ответом от NRPE
             -  ответ внести в reservedIp и merge в БД
        */

        return ok(reservedIps);
    }

    @Override
    public Result<ReservedIp> updateReservedIp( AuthToken token, ReservedIp reservedIp ) {

        if (reservedIp.getId() == null || !isValidReservedIp(reservedIp)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        ReservedIp oldReservedIp = reservedIpDAO.get(reservedIp.getId());

        if (!Objects.equals(oldReservedIp.getIpAddress(), reservedIp.getIpAddress())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Boolean result = reservedIpDAO.merge(reservedIp);
        if ( !result ) {
            return error(En_ResultStatus.NOT_UPDATED);
        }
        return ok(reservedIp);
    }

    @Override
    public Result<Long> removeReservedIp( AuthToken token, ReservedIp reservedIp ) {
        if (reservedIp == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        if (!reservedIpDAO.removeByKey(reservedIp.getId())) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        /* @todo
            уведомление об освобождении IP
            надо ли его отправлять, если owner сам освобождает?
         */
        return ok(reservedIp.getId());
    }

    private boolean isValidSubnet(Subnet subnet) {
        return subnet != null
                && StringUtils.isNotBlank(subnet.getAddress())
                && subnet.getAddress().matches(CrmConstants.IpReservation.SUBNET_ADDRESS)
                && StringUtils.isNotBlank(subnet.getMask());
    }

    private boolean isValidRequest(ReservedIpRequest reservedIpRequest) {
        if (reservedIpRequest == null || reservedIpRequest.getOwnerId() == null) {
            return false;
        }

        if (reservedIpRequest.isExact()) {
            if (!isValidIpAddress(reservedIpRequest.getIpAddress()) ||
                !isValidMacAddress(reservedIpRequest.getMacAddress())) {
                return false;
            }
        } else {
            if (reservedIpRequest.getNumber() < CrmConstants.IpReservation.MIN_IPS_COUNT ||
                reservedIpRequest.getNumber() > CrmConstants.IpReservation.MAX_IPS_COUNT) {
                return false;
            }
        }

        if(reservedIpRequest.getDateIntervalType() == null
          || ( En_DateIntervalType.FIXED.equals(reservedIpRequest.getDateIntervalType())
                && !isValidUseRange( reservedIpRequest.getReserveDate(),
                                     reservedIpRequest.getReleaseDate())
              ))
        {
            return false;
        }

        return true;
    }

    private boolean isValidReservedIp(ReservedIp reservedIp) {
        return reservedIp != null
               && reservedIp.getOwnerId() != null
               && isValidIpAddress(reservedIp.getIpAddress())
               && isValidMacAddress(reservedIp.getMacAddress())
               && isValidUseRange(reservedIp.getReserveDate(),
                                  reservedIp.getReleaseDate());
    }

    @Override
    public Result<Boolean> notifyOwnerAboutReleaseIp() {
        /*
        @todo
            - найти все IP с подходящими под критерий SEND_RELEASE_DATE_EXPIRES_TO_EXPIRE_DATE_IN_DAYS датами освобождения
            - сгруппировать по owner_id
            - сформировать по одному уведомлению каждому owner'у со списком IP-адресов,
              которые он должен освободить/продлить в течение SEND_RELEASE_DATE_EXPIRES_TO_EXPIRE_DATE_IN_DAYS дней
         */
        return ok(true );
    }

    @Override
    public Result<Boolean> notifyAdminsAboutExpiredReleaseDates() {
        /*
         @todo
            - найти все IP с наступившей датой освобождения
            - сгруппировать по owner_id
            - сформировать одно уведомление со списокм owner'ов и принадлежащих им IP
            возможно группировать следует по подсетям, а owner'а указывать как доп.инфо
         */
        return ok(true );
    }

    private boolean isValidIpAddress(String ipAddress) {
        return ipAddress.matches(CrmConstants.IpReservation.IP_ADDRESS);
    }

    private boolean isValidMacAddress(String macAddress) {
        return StringUtils.isEmpty(macAddress)
                || macAddress.matches(CrmConstants.IpReservation.MAC_ADDRESS);
    }

    private boolean isValidUseRange(Date from, Date to) {
        return from != null
                && to != null
                && from.before(to);
    }

    private boolean hasSubnetFreeIps(Long subnetId) {
        Long count = reservedIpDAO.countByExpression("subnet_id=?", subnetId);
        return count < CrmConstants.IpReservation.MAX_IPS_COUNT;
    }

    private String getAnyFreeIpInSubnet(Long subnetId) {
        Subnet subnet = subnetDAO.get(subnetId);

        ReservedIpQuery query = new ReservedIpQuery(null, En_SortField.ip_address, En_SortDir.ASC);
        query.setSubnetId(subnetId);
        SearchResult<ReservedIp> sr = reservedIpDAO.getSearchResultByQuery(query);

        if (CollectionUtils.isNotEmpty(sr.getResults()) && sr.getResults().size() < CrmConstants.IpReservation.MAX_IPS_COUNT) {
            for (int i=CrmConstants.IpReservation.MIN_IPS_COUNT; i < CrmConstants.IpReservation.MAX_IPS_COUNT; i++) {
                String checkedIp = subnet.getAddress()+"."+i;
                if (!sr.getResults()
                        .stream()
                        .filter(r -> r.getIpAddress().equals(checkedIp))
                        .findFirst().isPresent()) {
                    return checkedIp;
                }
            }
        } else {
            return subnet.getAddress()+".1";
        }

        return null;
    }

    private boolean checkSubnetExists (String address, Long excludeId) {
        Subnet subnet = subnetDAO.getSubnetByAddress(address);

        if (subnet == null)
            return false;

        if (excludeId != null && subnet.getId().equals(excludeId))
            return false;

        return true;
    }

    private boolean checkReservedIpExists (String address, Long excludeId) {
        ReservedIp reservedIp = reservedIpDAO.getReservedIpByAddress(address);

        if (reservedIp == null)
            return false;

        if (excludeId != null && reservedIp.getId().equals(excludeId))
            return false;

        return true;
    }

    public static final int SEND_RELEASE_DATE_EXPIRES_TO_EXPIRE_DATE_IN_DAYS = 3;

    private final static Logger log = LoggerFactory.getLogger( IpReservationService.class );
}