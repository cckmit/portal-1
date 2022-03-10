package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления резервированием IP-адресов
 */
@RemoteServiceRelativePath("springGwtServices/IpReservationController")
public interface IpReservationController extends RemoteService {

    SearchResult<Subnet> getSubnetList(ReservedIpQuery query) throws RequestFailedException;

    Subnet getSubnet(Long subnetId) throws RequestFailedException;

    Subnet saveSubnet(Subnet subnet) throws RequestFailedException;

    List<SubnetOption> getSubnetsOptionList(ReservedIpQuery query) throws RequestFailedException;

    Boolean isSubnetAddressExists(String address, Long exceptId) throws RequestFailedException;

    Long getFreeIpsCountBySubnets(List<Long> subnetIds) throws RequestFailedException;

    SearchResult<ReservedIp> getReservedIpList(ReservedIpQuery query) throws RequestFailedException;

    ReservedIp getReservedIp(Long reservedIpId) throws RequestFailedException;

    List<ReservedIp> createReservedIp(ReservedIpRequest reservedIpRequest) throws RequestFailedException;

    ReservedIp updateReservedIp(ReservedIp reservedIp) throws RequestFailedException;

    Long removeSubnet(Subnet subnet, boolean removeWithIps) throws RequestFailedException;

    Boolean isReservedIpAddressExists(String address) throws RequestFailedException;

    Boolean isSubnetAvailableToRemove(Long subnetId) throws RequestFailedException;

    Long removeReservedIp(ReservedIp reservedIp) throws RequestFailedException;

    Boolean isIpOnline(ReservedIp reservedIp) throws RequestFailedException;
}
