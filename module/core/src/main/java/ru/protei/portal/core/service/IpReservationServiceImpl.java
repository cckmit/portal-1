package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;
import static ru.protei.portal.core.model.helper.StringUtils.join;

/**
 * Реализация сервиса управления подсистемой резервирования IP
 */
public class IpReservationServiceImpl implements IpReservationService {

    @Autowired
    SubnetDAO subnetDAO;

    @Autowired
    ReservedIpDAO reservedIpDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    JdbcManyRelationsHelper helper;

    @Override
    public Result<Boolean> checkUniqueSubnet( AuthToken token, String address, Long excludeId) {

        if( address == null || address.isEmpty() )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkUniqueSubnet( address, excludeId));
    }

/*    @Override
    public Result<Boolean> checkUniqueReservedIp( AuthToken token, String address, Long excludeId) {

        if( address == null || address.isEmpty() )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkUniqueReservedIp( address, excludeId));
    }*/

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
    public Result<Boolean> removeSubnet( AuthToken token, Long id) {
        Subnet subnet = subnetDAO.get(id);

        if (subnet == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        boolean result = subnetDAO.remove(subnet);
        return ok(result);
    }

    @Override
    /* @todo задача резервирования долна выполняться в фоне? */
    public Result<List<ReservedIp>> createReservedIp( AuthToken token, ReservedIp reservedIp) {
        /*
           @todo если задан "конкретный IP"
                - проверка IP по маске
                - проверка уникальности IP
                - arping IP
                - создание
         */
        if (!validateFields(reservedIp)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        reservedIp.setCreated(new Date());
        Long reservedIpId = reservedIpDAO.persist(reservedIp);

        if (reservedIpId == null)
            return error(En_ResultStatus.NOT_CREATED);

        reservedIp.setId(reservedIpId);
        ArrayList<ReservedIp> reservedIps = new ArrayList<>();
        reservedIps.add(reservedIp);

         /*
           @todo если задано "любая свободная подсеть"
                - проверка валидности кол-ва на null и 0
                - проверка подсетей на налчие требуемого кол-ва свободных IP
                - пройти по всем подходящим подсетям, резервируя IP, попутно проверяя их через arping
         */


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
    public Result<Boolean> releaseReservedIp( AuthToken token, Long id ) {
        if ( subnetDAO.removeByKey( id ) ) {
            return ok(true );
        }
        /* @todo
            уведомление об освобождении IP
            надо ли его отправлять, если owner сам освобождает?
         */
        return error(En_ResultStatus.INTERNAL_ERROR );
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

    private boolean validateFields(ReservedIp reservedIp) {
        if (reservedIp == null) {
            return false;
        }

        /* @todo
             - если флаг=конкретный адрес, проверить уникальность заданного адреса
             - если флаг=любая свободная, проверить заданное кол-во на null и 0
             - проверить задан ли owner
             - проверить дату освобождения на null, если пользователь неадмин
        */

        if (reservedIp.getOwnerId() == null) {
            return false;
        }

        if (StringUtils.isBlank(reservedIp.getIpAddress())) {
            return false;
        }

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

    public static final int SEND_RELEASE_DATE_EXPIRES_TO_EXPIRE_DATE_IN_DAYS = 3;

    private final static Logger log = LoggerFactory.getLogger( IpReservationService.class );
}
