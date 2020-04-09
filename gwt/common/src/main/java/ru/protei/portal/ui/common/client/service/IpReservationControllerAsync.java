package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Map;

/**
 * Асинхронный сервис управления резервированием IP-адресов
 */
public interface IpReservationControllerAsync {

    void getSubnetList(ReservedIpQuery query, AsyncCallback<SearchResult<Subnet>> async);

    void getSubnet(Long subnetId, AsyncCallback<Subnet> async);

    void saveSubnet(Subnet subnet, AsyncCallback<Subnet> async);

    void getSubnetsOptionList(ReservedIpQuery query, AsyncCallback<List<SubnetOption>> async);

    void isSubnetUnique(String address, Long exceptId, AsyncCallback<Boolean> async);

     void getReservedIpsBySubnets(ReservedIpQuery query, AsyncCallback<Map<Subnet, List<ReservedIp>>> async);

    void getReservedIpList(ReservedIpQuery query, AsyncCallback<SearchResult<ReservedIp>> async);

    void getReservedIp(Long reservedIpId, AsyncCallback<ReservedIp> async);

    void createReservedIp(ReservedIpRequest reservedIpRequest, AsyncCallback<List<ReservedIp>> async);

    void updateReservedIp(ReservedIp reservedIp, AsyncCallback<ReservedIp> async);

    void removeSubnet(Subnet subnet, AsyncCallback<Long> async);

    void removeReservedIp(ReservedIp reservedIp, AsyncCallback<Long> async);
}
