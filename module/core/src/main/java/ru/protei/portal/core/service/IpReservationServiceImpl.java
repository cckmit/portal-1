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
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collector;
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
    public Result<Boolean> checkUniqueSubnet( AuthToken token, String address, Long excludeId) {

        if( address == null || address.isEmpty() )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkUniqueSubnet( address, excludeId));
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

        if (!validateFields(subnet)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!checkUniqueSubnet(subnet.getAddress(), subnet.getId()))
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

        if (!validateFields(subnet) || subnet.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Subnet oldSubnet = subnetDAO.get(subnet.getId());

        /*
           @todo временно открыть редактирование адреса и маски
         */
/*        if (!Objects.equals(oldSubnet.getAddress(), subnet.getAddress()) ||
            !Objects.equals(oldSubnet.getMask(), subnet.getMask())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }*/

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
        /*
           @todo если задан "конкретный IP"
                - проверка IP через NRPE
                - update IP с ответом от NRPE
         */
        if (!validateFields(reservedIpRequest)) {
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
            if (!checkUniqueReservedIp(reservedIpRequest.getIpAddress(), null)) {
                return error(En_ResultStatus.ALREADY_EXIST);
            }

            reservedIp.setIpAddress(reservedIpRequest.getIpAddress());
            reservedIp.setMacAddress(reservedIpRequest.getMacAddress());
            String subnetAddress = reservedIpRequest.getIpAddress().substring(0, reservedIpRequest.getIpAddress().lastIndexOf("."));
            Subnet subnet = subnetDAO.checkExistsByAddress(subnetAddress);
            if (subnet == null) {
                return error(En_ResultStatus.UNDEFINED_OBJECT);
            }
            reservedIp.setSubnetId(subnet.getId());

            Long reservedIpId = reservedIpDAO.persist(reservedIp);

            if (reservedIpId == null)
                return error(En_ResultStatus.NOT_CREATED);

            reservedIp.setId(reservedIpId);

            /*
               @todo запрос на NRPE,
               ответ внести в reservedIp и merge в БД
             */

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

            int created = 0;
            while (created < reservedIpRequest.getNumber()) {
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

                Long reservedIpId = reservedIpDAO.persist(ip);

                if (reservedIpId != null) {
                    created ++;
                }
                /*
                  @todo запрос на NRPE,
                    ответ внести в reservedIp и merge в БД
                */
            }
        }

        return ok(reservedIps);
    }

    @Override
    public Result<ReservedIp> updateReservedIp( AuthToken token, ReservedIp reservedIp ) {

        if (!validateFields(reservedIp) || reservedIp.getId() == null) {
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

    private boolean checkUniqueSubnet (String address, Long excludeId) {
        Subnet subnet = subnetDAO.checkExistsByAddress(address);
        return subnet == null || subnet.getId().equals(excludeId);
    }

    private boolean checkUniqueReservedIp (String address, Long excludeId) {
        ReservedIp reservedIp = reservedIpDAO.checkExistsByAddress(address);
        return reservedIp == null || reservedIp.getId().equals(excludeId);
    }

    private boolean validateFields(Subnet subnet) {
        if (subnet == null) {
            return false;
        }

        if (StringUtils.isBlank(subnet.getAddress()) || StringUtils.isBlank(subnet.getMask())) {
            return false;
        }

        return true;
    }

    private boolean validateFields(ReservedIpRequest reservedIpRequest) {
        if (reservedIpRequest == null) {
            return false;
        }

        /* @todo
             - если флаг=конкретный адрес, проверить уникальность заданного адреса
             - если флаг=любая свободная, проверить заданное кол-во на null и 0
             - проверить задан ли owner
             - проверить дату освобождения на null, если пользователь неадмин
        */

        if (reservedIpRequest.getOwnerId() == null) {
            return false;
        }

        // @todo проверка дат

        if (reservedIpRequest.isExact()) {
            if (StringUtils.isBlank(reservedIpRequest.getIpAddress()) ||
                    !isValidIpAddress(reservedIpRequest.getIpAddress())) {
                return false;
            }

            if (StringUtils.isNotBlank(reservedIpRequest.getMacAddress()) &&
                    !isValidMacAddress(reservedIpRequest.getMacAddress())) {
                return false;
            }
        } else {
/*            // @todo если списка нет, берем из любой подсети
            if (reservedIpRequest.getSubnets() == null || reservedIpRequest.getSubnets().isEmpty()) {
                return false;
            }*/

            if ((reservedIpRequest.getNumber() < MIN_IPS_COUNT || reservedIpRequest.getNumber() >= MAX_IPS_COUNT)) {
                return false;
            }
        }

        if(reservedIpRequest.getDateIntervalType() == null ||
                ( En_DateIntervalType.FIXED.equals(reservedIpRequest.getDateIntervalType()) &&
                ( reservedIpRequest.getReserveDate() == null ||
                  reservedIpRequest.getReleaseDate() == null )
                )
        ) {
            return false;
        }

        return true;
    }

    private boolean validateFields(ReservedIp reservedIp) {
        if (reservedIp == null) {
            return false;
        }

        if (reservedIp.getOwnerId() == null) {
            return false;
        }

        if (StringUtils.isBlank(reservedIp.getIpAddress()) ||
                (StringUtils.isNotBlank(reservedIp.getIpAddress()) && !isValidMacAddress(reservedIp.getMacAddress())
            )) {
            return false;
        }

        /* @todo
             - проверить дату освобождения на null, если пользователь неадмин
        */

        return true;
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
        return true;
    }

    private boolean isValidMacAddress(String macAddress) {
        return true;
    }

    private boolean hasSubnetFreeIps(Long subnetId) {
        Long count = reservedIpDAO.countByExpression("subnet_id=?", subnetId);
        return count < MAX_IPS_COUNT;
    }

    private String getAnyFreeIpInSubnet(Long subnetId) {
        Subnet subnet = subnetDAO.get(subnetId);

        ReservedIpQuery query = new ReservedIpQuery(null, En_SortField.ip_address, En_SortDir.ASC);
        query.setSubnetId(subnetId);
        SearchResult<ReservedIp> sr = reservedIpDAO.getSearchResultByQuery(query);

        if (CollectionUtils.isNotEmpty(sr.getResults()) && sr.getResults().size() < MAX_IPS_COUNT) {
            for (int i=MIN_IPS_COUNT; i < MAX_IPS_COUNT; i++) {
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

    public static final int SEND_RELEASE_DATE_EXPIRES_TO_EXPIRE_DATE_IN_DAYS = 3;
    public static final int MIN_IPS_COUNT = 1;
    public static final int MAX_IPS_COUNT = 256;

    private final static Logger log = LoggerFactory.getLogger( IpReservationService.class );
}