package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Сервис управления резервированием IP
 */
public interface IpReservationService {

    Result<Boolean> isSubnetAddressExists(String address, Long excludeId);

    Result<Boolean> isReservedIpAddressExists(String address);

    Result<Long> getFreeIpsCountBySubnets(AuthToken token, List<Long> subnetIds);

    @Privileged(En_Privilege.RESERVED_IP_VIEW)
    Result<SearchResult<ReservedIp>> getReservedIps(AuthToken token, ReservedIpQuery query);

    /**
     * Список IP-адресов, сгруппированных по подсетям
     * @param token
     * @param query
     * @return
     */
    @Privileged({ En_Privilege.RESERVED_IP_VIEW })
    Result<Map<Subnet, List<ReservedIp>>> getReservedIpsBySubnets(AuthToken token, ReservedIpQuery query);

    @Privileged(En_Privilege.RESERVED_IP_VIEW)
    Result<ReservedIp> getReservedIp(AuthToken token, Long id);

    @Privileged(En_Privilege.RESERVED_IP_VIEW)
    Result<SearchResult<Subnet>> getSubnets(AuthToken token, ReservedIpQuery query);

    @Privileged( requireAny = { En_Privilege.SUBNET_VIEW, En_Privilege.RESERVED_IP_VIEW })
    Result<List<SubnetOption>> getSubnetsOptionList(AuthToken token, ReservedIpQuery query);

    @Privileged(En_Privilege.SUBNET_VIEW)
    Result<Subnet> getSubnet(AuthToken token, Long id);

    /**
     * Создание подсети
     * @param token
     * @param subnet
     * @return
     */
    @Privileged(En_Privilege.SUBNET_CREATE)
    @Auditable(En_AuditType.SUBNET_CREATE)
    Result<Subnet> createSubnet(AuthToken token, Subnet subnet);

    /**
     * Изменение параметров подсети
     * @param token
     * @param subnet
     * @return
     */
    @Privileged(En_Privilege.SUBNET_EDIT)
    @Auditable(En_AuditType.SUBNET_MODIFY)
    Result<Subnet> updateSubnet(AuthToken token, Subnet subnet);

    /**
     * Удаление подсети
     * @param token
     * @param subnet
     * @return Идентификатор удаленной подсети
     */
    @Privileged(En_Privilege.SUBNET_REMOVE)
    @Auditable(En_AuditType.SUBNET_REMOVE)
    Result<Long> removeSubnet(AuthToken token, Subnet subnet, boolean removeWithIps);

    /**
     *
     * @param token
     * @param subnetId
     * @return
     */
    Result<Boolean> isSubnetAvailableToRemove(AuthToken token, Long subnetId);

    /**
     * Резервирование IP-адресов
     * @param token
     * @param reservedIpRequest
     * @return
     */
    @Privileged(En_Privilege.RESERVED_IP_CREATE)
    @Auditable(En_AuditType.RESERVED_IP_CREATE)
    Result<List<ReservedIp>> createReservedIp(AuthToken token, ReservedIpRequest reservedIpRequest);

    /**
     * Редактирование параметров зарезервированного IP-адреса
     * @param token
     * @param reservedIp
     * @return
     */
    @Privileged(En_Privilege.RESERVED_IP_EDIT)
    @Auditable(En_AuditType.RESERVED_IP_MODIFY)
    Result<ReservedIp> updateReservedIp(AuthToken token, ReservedIp reservedIp);

    /**
      Удаление/освобождение зарезервированного IP-адреса
     * @param token
     * @param reservedIp
     * @return Идентификатор освобожденного IP-адреса
     */
    @Privileged(En_Privilege.RESERVED_IP_REMOVE)
    @Auditable(En_AuditType.RESERVED_IP_REMOVE)
    Result<Long> removeReservedIp(AuthToken token, ReservedIp reservedIp);

    Result<Void> notifyOwnersAboutReleaseIp();

    Result<Void> notifyAdminsAboutExpiredReleaseDates();

    @Privileged(En_Privilege.RESERVED_IP_VIEW)
    Result<Boolean> isIpOnline(AuthToken token, ReservedIp reservedIp);
}
