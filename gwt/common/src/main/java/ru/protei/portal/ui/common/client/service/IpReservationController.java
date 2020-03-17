package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Map;

/**
 * Сервис управления резервированием IP-адресов
 */
@RemoteServiceRelativePath("springGwtServices/IpReservationController")
public interface IpReservationController extends RemoteService {

    SearchResult<Subnet> getSubnetList(ReservedIpQuery query) throws RequestFailedException;

    Subnet getSubnet(Long subnetId) throws RequestFailedException;

    Subnet saveSubnet(Subnet subnet) throws RequestFailedException;

/*    List<SubnetShortView> getSubnetViewList(ReservedIpQuery query) throws RequestFailedException;*/

    boolean isSubnetUnique(String address, Long exceptId) throws RequestFailedException;

    Map<Subnet, List<ReservedIp>> getReservedIpsBySubnets(ReservedIpQuery query) throws RequestFailedException;

    SearchResult<ReservedIp> getReservedIpList(ReservedIpQuery query) throws RequestFailedException;

    ReservedIp getReservedIp(Long reservedIpId) throws RequestFailedException;

    List<ReservedIp> createReservedIp(ReservedIp reservedIp) throws RequestFailedException;

    ReservedIp updateReservedIp(ReservedIp reservedIp) throws RequestFailedException;

    Long removeSubnet(Subnet subnet) throws RequestFailedException;

    Long removeReservedIp(ReservedIp reservedIp) throws RequestFailedException;
}
