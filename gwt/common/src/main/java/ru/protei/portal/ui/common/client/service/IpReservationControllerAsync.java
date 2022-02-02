package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Асинхронный сервис управления резервированием IP-адресов
 */
public interface IpReservationControllerAsync {

    void getSubnetList(ReservedIpQuery query, AsyncCallback<SearchResult<Subnet>> async);

    void getSubnet(Long subnetId, AsyncCallback<Subnet> async);

    void saveSubnet(Subnet subnet, AsyncCallback<Subnet> async);

    void getSubnetsOptionList(ReservedIpQuery query, AsyncCallback<List<SubnetOption>> async);

    void isSubnetAddressExists(String address, Long id, AsyncCallback<Boolean> async);

    void getFreeIpsCountBySubnets(List<Long> subnetIds, AsyncCallback<Long> async);

    void getReservedIpList(ReservedIpQuery query, AsyncCallback<SearchResult<ReservedIp>> async);

    void getReservedIp(Long reservedIpId, AsyncCallback<ReservedIp> async);

    void createReservedIp(ReservedIpRequest reservedIpRequest, AsyncCallback<List<ReservedIp>> async);

    void updateReservedIp(ReservedIp reservedIp, AsyncCallback<ReservedIp> async);

    void removeSubnet(Subnet subnet, boolean removeWithIps, AsyncCallback<Long> async);

    void isSubnetAvailableToRemove(Long subnetId, AsyncCallback<Boolean> async);

    void removeReservedIp(ReservedIp reservedIp, AsyncCallback<Long> async);

    void isReservedIpAddressExists(String address, AsyncCallback<Boolean> async);

    void isIpOnline(ReservedIp reservedIp, AsyncCallback<Boolean> async);
}
