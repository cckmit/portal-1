package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.ReservedIpNotificationEvent;
import ru.protei.portal.core.event.ReservedIpReleaseRemainingEvent;
import ru.protei.portal.core.event.SubnetNotificationEvent;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.not;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

/**
 * Реализация сервиса управления подсистемой резервирования IP
 */
public class IpReservationServiceImpl implements IpReservationService {

    @Autowired
    SubnetDAO subnetDAO;

    @Autowired
    ReservedIpDAO reservedIpDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    JdbcManyRelationsHelper helper;

    @Autowired
    PolicyService policyService;

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    PortalConfig config;

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
    public Result<Long> getFreeIpsCountBySubnets(AuthToken token, List<Long> subnetIds) {
        Map<Long, Long> map = reservedIpDAO.countBySubnetIds(subnetIds);

        Long reservedIpCount = map.values().stream().reduce((s1, s2) -> s1 + s2).orElse(0L);
        Long subnetCount = CollectionUtils.isEmpty(subnetIds) ?
                subnetDAO.count(new ReservedIpQuery("", En_SortField.address, En_SortDir.ASC)) :
                subnetIds.size();
        Long potentialCount = subnetCount * new Long(CrmConstants.IpReservation.MAX_IPS_COUNT);

        return ok(potentialCount - reservedIpCount);
    }

    @Override
    public Result<SearchResult<ReservedIp>> getReservedIps(AuthToken token, ReservedIpQuery query) {
        SearchResult<ReservedIp> sr = reservedIpDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    public Result<SearchResult<Subnet>> getSubnets(AuthToken token, ReservedIpQuery query) {
        SearchResult<Subnet> sr = subnetDAO.getSearchResultByQuery(query);

        if ( CollectionUtils.isEmpty(sr.getResults())) {
            return ok(sr);
        }

        Map<Long, Long> map = reservedIpDAO.countBySubnetIds(sr.getResults().stream()
                .map(Subnet::getId)
                .collect(Collectors.toList()));

        sr.getResults().forEach(subnet -> {
            Long count = map.getOrDefault(subnet.getId(), 0L);
            subnet.setReservedIPs(count);
            subnet.setFreeIps(CrmConstants.IpReservation.MAX_IPS_COUNT - count);
        });

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

        SearchResult<ReservedIp> sr = reservedIpDAO.getSearchResultByQuery(query);

        if (CollectionUtils.isEmpty(sr.getResults())) {
            return ok( subnetToReservedIpsMap );
        }

        subnetToReservedIpsMap = sr.getResults().stream()
                .collect(Collectors.groupingBy(
                        ReservedIp::getSubnet,
                        LinkedHashMap::new, Collectors.toList()));

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
        ReservedIp reservedIp = getReservedIp(id);

        return reservedIp != null ? Result.ok( reservedIp)
                : error( En_ResultStatus.NOT_FOUND);
    }

    @Override
    public Result<Subnet> createSubnet( AuthToken token, Subnet subnet) {

        if (token == null || !hasAccessForSubnet(token, En_Privilege.SUBNET_CREATE)) {
            throw new ResultStatusException(En_ResultStatus.PERMISSION_DENIED);
        }

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

        if (subnet == null || subnet.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (token == null || !hasAccessForSubnet(token, En_Privilege.SUBNET_EDIT)) {
            throw new ResultStatusException(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!isValidSubnet(subnet)) {
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

        return ok(subnetAvailableToRemove(subnetId));
    }

    @Override
    public Result<Subnet> removeSubnet( AuthToken token, Subnet subnet, boolean removeWithIps) {
        if (subnet == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (token == null || !hasAccessForSubnet(token, En_Privilege.SUBNET_REMOVE)) {
            throw new ResultStatusException(En_ResultStatus.PERMISSION_DENIED);
        }

        if (removeWithIps || subnetAvailableToRemove(subnet.getId())) {

            Subnet stored = subnetDAO.get(subnet.getId());

            if (!subnetDAO.removeByKey(subnet.getId()))
                return error(En_ResultStatus.INTERNAL_ERROR);

            return ok(stored)
                    .publishEvent(new SubnetNotificationEvent(
                            this,
                            stored,
                            getInitiator(token),
                            SubnetNotificationEvent.Action.REMOVED,
                            makeNotificationListFromSubnet(stored)
                    ));
        }

        return error(En_ResultStatus.NOT_REMOVED);
    }

    @Override
    /* @todo задача резервирования должна выполняться в фоне? */
    public Result<ArrayList<ReservedIp>> createReservedIp( AuthToken token, ReservedIpRequest reservedIpRequest) {

        if (token == null || !hasAccessForReservedIp(token, En_Privilege.RESERVED_IP_CREATE, null)) {
            throw new ResultStatusException(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!isValidRequest(reservedIpRequest)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        ArrayList<ReservedIp> reservedIps = new ArrayList<>();

        ReservedIp templateIp = new ReservedIp();
        templateIp.setCreated(new Date());
        templateIp.setCreatorId(token.getPersonId());
        templateIp.setOwnerId(reservedIpRequest.getOwnerId());
        templateIp.setComment(reservedIpRequest.getComment());

        En_DateIntervalType intervalType = reservedIpRequest.getDateIntervalType();

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date throughMonth = calendar.getTime();

        switch (intervalType) {
            case UNLIMITED:
                templateIp.setReserveDate(today);
                templateIp.setReleaseDate(null);
                break;
            case MONTH:
                templateIp.setReserveDate(today);
                templateIp.setReleaseDate(throughMonth);
                break;
            case FIXED:
                templateIp.setReserveDate(reservedIpRequest.getReserveDate());
                templateIp.setReleaseDate(reservedIpRequest.getReserveDate());
        }

        if (reservedIpRequest.isExact()) {
            if (checkReservedIpExists(reservedIpRequest.getIpAddress(), null)) {
                return error(En_ResultStatus.ALREADY_EXIST);
            }

            templateIp.setIpAddress(reservedIpRequest.getIpAddress());
            templateIp.setMacAddress(StringUtils.isBlank(reservedIpRequest.getMacAddress()) ?
                            null : reservedIpRequest.getMacAddress().trim());

            Subnet subnet = subnetDAO.getSubnetByAddress(reservedIpRequest.getSubnetAddress());
            if (subnet == null) {
                return error(En_ResultStatus.SUBNET_DOES_NOT_EXIST);
            }
            templateIp.setSubnetId(subnet.getId());

            Long reservedIpId = reservedIpDAO.persist(templateIp);

            if (reservedIpId == null)
                return error(En_ResultStatus.NOT_CREATED);

            templateIp.setId(reservedIpId);
            reservedIps.add(templateIp);

        } else {
         /*
           @todo если задано "любая свободная подсеть"
                Для начала необходиом определить список подсетей, в которых достаточно свободных IP адресов
                - проверка подсетей на наличие требуемого кол-ва свободных IP
                - пройти по всем подходящим подсетям, резервируя IP, попутно проверяя их через NRPE
         */

            Set<SubnetOption> subnets = getAvailableSubnets(token, reservedIpRequest.getSubnets());
            if (CollectionUtils.isEmpty(subnets)) {
                return error(En_ResultStatus.NOT_CREATED);
            }

            while (reservedIps.size() < reservedIpRequest.getNumber()) {
                ReservedIp reservedIp = ReservedIp.createByTemplate(templateIp);

                subnets.forEach( s -> {
                            if (hasSubnetFreeIps(s.getId())) {
                                String freeIp = getAnyFreeIpInSubnet(s.getId());
                                if (freeIp != null) {
                                    reservedIp.setSubnetId(s.getId());
                                    reservedIp.setIpAddress(freeIp);
                                    return;
                                }
                            }
                        }
                );
                if (reservedIp.getIpAddress() == null || reservedIp.getSubnetId() == null) {
                    break;
                }

                Long reservedIpId = reservedIpDAO.persist(reservedIp);

                if (reservedIpId != null) {
                    reservedIps.add(reservedIp);
                }
            }
        }

        /*
           @todo
             - проверка IP через NRPE
             - update IP с ответом от NRPE
             -  ответ внести в reservedIp и merge в БД
        */

        List<ApplicationEvent> events = stream(reservedIps)
                .map(reservedIp -> new ReservedIpNotificationEvent(
                        this,
                        reservedIp,
                        getInitiator(token),
                        ReservedIpNotificationEvent.Action.CREATED,
                        makeNotificationListFromReservedIp(reservedIp)
                ))
                .collect(Collectors.toList());

        return ok(reservedIps).publishEvents(events);
    }

    @Override
    public Result<ReservedIp> updateReservedIp( AuthToken token, ReservedIp reservedIp ) {

        if (reservedIp == null || reservedIp.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (token == null || !hasAccessForReservedIp(token, En_Privilege.RESERVED_IP_EDIT, reservedIp)) {
            throw new ResultStatusException(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!isValidReservedIp(token, reservedIp)) {
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

        return ok(reservedIp)
                .publishEvent(new ReservedIpNotificationEvent(
                        this,
                        reservedIp,
                        getInitiator(token),
                        ReservedIpNotificationEvent.Action.UPDATED,
                        makeNotificationListFromReservedIp(reservedIp)
                ));
    }

    @Override
    public Result<ReservedIp> removeReservedIp( AuthToken token, ReservedIp reservedIp ) {
        if (reservedIp == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (token == null || !hasAccessForReservedIp(token, En_Privilege.RESERVED_IP_REMOVE, reservedIp)) {
            throw new ResultStatusException(En_ResultStatus.PERMISSION_DENIED);
        }

        ReservedIp stored = getReservedIp(reservedIp.getId());

        if (!reservedIpDAO.removeByKey(reservedIp.getId())) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return ok(stored)
                .publishEvent(new ReservedIpNotificationEvent(
                        this,
                        stored,
                        getInitiator(token),
                        ReservedIpNotificationEvent.Action.REMOVED,
                        makeNotificationListFromReservedIp(stored)
                ));
    }

    /**
     * Уведомление ответственных о приближении даты освобождения IP адреса в ближайшие 3 дня
     * @return
     */
    @Override
    public Result<Boolean> notifyOwnersAboutReleaseIp() {
        /*
        @todo
            - найти все IP с подходящими под критерий RELEASE_DATE_EXPIRES_IN_DAYS датами освобождения
            - сгруппировать по owner_id
            - сформировать по одному уведомлению каждому owner'у со списком IP-адресов,
              которые он должен освободить/продлить в течение RELEASE_DATE_EXPIRES_IN_DAYS дней
         */

        log.info("notifyOwnersAboutReleaseIp(): start");

        Date releaseDateStart = makeDateWithOffset(1);
        Date releaseDateEnd = makeDateWithOffset(RELEASE_DATE_EXPIRES_IN_DAYS);

        log.info("notifyOwnersAboutReleaseIp(): start from {} to {}", releaseDateStart, releaseDateEnd);

        ReservedIpQuery query = new ReservedIpQuery();
        query.setReleasedFrom(releaseDateStart);
        query.setReleasedTo(releaseDateEnd);
        Map<Long, List<ReservedIp>> reservedIpsByOwners = getReservedIpsByOwners(query);
        if (reservedIpsByOwners.isEmpty()) {
            log.info("notifyOwnersAboutReleaseIp(): expired reservedIps is empty for period from {} to {}", releaseDateStart, releaseDateEnd);
            return ok(false);
        }

        int notificationSentAmount = 0;

        for (Long ownerId : reservedIpsByOwners.keySet()) {

            List<NotificationEntry> notificationEntries = makeNotificationListFromPersonId(ownerId);

            if (CollectionUtils.isEmpty(notificationEntries)) {
                log.info("notifyOwnersAboutReleaseIp(): notification for owner {} to release IPs: no entries to be notified", ownerId);
                continue;
            }
            log.info("notifyOwnersAboutReleaseIp(): notification for owner {} to release IPs: entries to be notified: {}",
                    ownerId, notificationEntries);
            publisherService.publishEvent(new ReservedIpReleaseRemainingEvent(
                    this, reservedIpsByOwners.get(ownerId), releaseDateStart, releaseDateEnd, notificationEntries));
            notificationSentAmount++;
        }

        log.info("notifyOwnersAboutReleaseIp(): done {} notification(s)", notificationSentAmount);
        return ok(notificationSentAmount > 0);
    }

    /**
     * Уведомление системных администраторов об истечении даты освобождения IP адресов
     * @return
     */
    @Override
    public Result<Boolean> notifyAdminsAboutExpiredReleaseDates() {
        /*
         @todo
            - найти все IP с наступившей датой освобождения
            - сгруппировать по owner_id
            - сформировать одно уведомление со списком IP в формате:
               IP адрес, ответственный, дата освобождения
         */

        log.info("notifyAdminsAboutExpiredReleaseDates(): start");

        Date today = makeDateWithOffset(0);

        log.info("notifyAdminsAboutExpiredReleaseDates(): already expired to {}", today);

        ReservedIpQuery query = new ReservedIpQuery();
        query.setReleasedTo(today);
        List<ReservedIp> reservedIps = getReservedIps(query);

        if (CollectionUtils.isEmpty(reservedIps)) {
            log.info("notifyAdminsAboutExpiredReleaseDates(): expired release date IP list is empty for today {}", today);
            return ok(false);
        }

        List<NotificationEntry> notificationEntries = makeNotificationListFromConfiguration();

        if (CollectionUtils.isEmpty(notificationEntries)) {
            log.info("notifyAdminsAboutExpiredReleaseDates(): notification for expired release date IPs: no entries to be notified");
            return ok(false);
        }

        log.info("notifyAdminsAboutExpiredReleaseDates(): notification for expired release date IPs: entries to be notified: {}", notificationEntries);
        publisherService.publishEvent(new ReservedIpReleaseRemainingEvent(this, reservedIps, null, today, notificationEntries));

        log.info("notifyAdminsAboutExpiredReleaseDates(): done");
        return ok(true);
    }

    private boolean isValidSubnet(Subnet subnet) {
        return StringUtils.isNotBlank(subnet.getAddress())
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

    private boolean isValidReservedIp(AuthToken token, ReservedIp reservedIp) {
        return reservedIp.getOwnerId() != null
                && isValidIpAddress(reservedIp.getIpAddress())
                && isValidMacAddress(reservedIp.getMacAddress())
                && isValidUseRange(token, reservedIp.getReserveDate(), reservedIp.getReleaseDate());
    }

    private boolean isValidIpAddress(String ipAddress) {
        return ipAddress.matches(CrmConstants.IpReservation.IP_ADDRESS);
    }

    private boolean isValidMacAddress(String macAddress) {
        return StringUtils.isBlank(macAddress)
                || macAddress.matches(CrmConstants.IpReservation.MAC_ADDRESS);
    }

    private boolean isValidUseRange(Date from, Date to) {
        return from != null
                && to != null

                && from.before(to);
    }

    private boolean isValidUseRange(AuthToken token, Date from, Date to) {
        return from != null
                && ((to != null && from.before(to))
                     || isSystemAdministrator(token, En_Privilege.RESERVED_IP_CREATE));
    }

    private Set<SubnetOption> getAvailableSubnets(AuthToken token, Set<SubnetOption> selectedSubnets) {

        Set<SubnetOption> subnets;
        if (CollectionUtils.isNotEmpty(selectedSubnets)) {
            subnets = selectedSubnets;
        } else {
            List<SubnetOption> result = getSubnetsOptionList(token,
                    new ReservedIpQuery("", En_SortField.address, En_SortDir.ASC))
                    .getData();

            if (CollectionUtils.isEmpty(result)) {
                return null;
            }

            subnets = result.stream().collect(Collectors.toSet());
        }

        return subnets;
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

        if (CollectionUtils.isEmpty(sr.getResults())) {
            return subnet.getAddress()+"." + CrmConstants.IpReservation.MIN_IPS_COUNT;
        }

        if (sr.getResults().size() == CrmConstants.IpReservation.MAX_IPS_COUNT) {
            return null;
        }

        for (int i=CrmConstants.IpReservation.MIN_IPS_COUNT; i <= CrmConstants.IpReservation.MAX_IPS_COUNT; i++) {
            String checkedIp = subnet.getAddress()+"."+i;
            if (!sr.getResults()
                    .stream()
                    .filter(r -> r.getIpAddress().equals(checkedIp))
                    .findFirst().isPresent()) {
                return checkedIp;
            }
        }

        return null;
    }

    private ReservedIp getReservedIp(Long reservedIpId) {
        return reservedIpDAO.get(reservedIpId);
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

    private boolean subnetAvailableToRemove (Long subnetId) {
        ReservedIpQuery query = new ReservedIpQuery();
        query.setSubnetId(subnetId);
        Long result = reservedIpDAO.count(query);

        return result == null || result == 0;
    }

    private boolean hasAccessForSubnet(AuthToken token, En_Privilege privilege) {
        return policyService.hasPrivilegeFor(privilege, token.getRoles());
    }

    private boolean hasAccessForReservedIp(AuthToken token, En_Privilege privilege, ReservedIp reservedIp) {
        return isSystemAdministrator(token, privilege)
                || reservedIp == null
                || (policyService.hasPrivilegeFor(privilege, token.getRoles())
                    && token.getPersonId().equals(reservedIp.getOwnerId())
        );
    }

    private boolean isSystemAdministrator (AuthToken token, En_Privilege privilege) {
        return policyService.hasScopeForPrivilege(token.getRoles(), privilege, En_Scope.SYSTEM);
    }

    private Date makeDateWithOffset(int dayOffset) {
        LocalDate localDate = LocalDate.now();
        localDate.plusDays(dayOffset);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private List<ReservedIp> getReservedIps(ReservedIpQuery query) {
        return reservedIpDAO.getSearchResultByQuery(query).getResults();
    }

    private Map< Long, List< ReservedIp>> getReservedIpsByOwners(ReservedIpQuery query) {
        Map< Long, List<ReservedIp> > ownerToReservedIpsMap = new HashMap<>();

        SearchResult<ReservedIp> sr = reservedIpDAO.getSearchResultByQuery(query);

        if (CollectionUtils.isNotEmpty(sr.getResults())) {
            ownerToReservedIpsMap = sr.getResults().stream()
                    .collect(Collectors.groupingBy(
                            ReservedIp::getOwnerId,
                            LinkedHashMap::new, Collectors.toList()));
        }

        return ownerToReservedIpsMap;
    }

    private List<NotificationEntry> makeNotificationListFromSubnet(Subnet subnet) {
        List<ReservedIp> reservedIps = reservedIpDAO.getReservedIpsBySubnetId(subnet.getId());

        if (CollectionUtils.isEmpty(reservedIps)) {
            return null;
        }

        return stream(reservedIps).collect(Collectors.groupingBy(ReservedIp::getOwnerId))
                .keySet().stream()
                .filter(Objects::nonNull)
                .map(ownerId -> {
                    Person person = personDAO.get(ownerId);
                    PlainContactInfoFacade contact = new PlainContactInfoFacade(person.getContactInfo());
                    return NotificationEntry.email(contact.getEmail(), DEFAULT_LOCALE);
                })
                .filter(entry -> StringUtils.isNotEmpty(entry.getAddress()))
                .collect(Collectors.toList());
    }

    private List<NotificationEntry> makeNotificationListFromReservedIp(ReservedIp reservedIp) {
        return Stream.of(
                makeNotificationListFromPersonId(reservedIp.getOwnerId()),
                makeNotificationListFromPersonId(reservedIp.getCreatorId())
        )
                .flatMap(Collection::stream) // flatten
                .distinct()
                .collect(Collectors.toList());
    }

    private List<NotificationEntry> makeNotificationListFromPersonId(Long personId) {
        Person person = personDAO.get(personId);
        if (person == null) {
            return null;
        }

        PlainContactInfoFacade contact = new PlainContactInfoFacade(person.getContactInfo());
        return Arrays.asList(NotificationEntry.email(contact.getEmail(), person.getLocale()));
    }

    private List<NotificationEntry> makeNotificationListFromConfiguration() {
        return stream(Arrays.asList(config.data().getMailNotificationConfig().getCrmIpReservationNotificationsRecipients()))
                .filter(Objects::nonNull)
                .filter(not(String::isEmpty))
                .map(address -> NotificationEntry.email(address, DEFAULT_LOCALE))
                .collect(Collectors.toList());
    }

    private Person getInitiator (AuthToken token) {
        return personDAO.get(token.getPersonId());
    }

    private static final String DEFAULT_LOCALE = "ru";
    private static final int RELEASE_DATE_EXPIRES_IN_DAYS = 3;
    private final static Logger log = LoggerFactory.getLogger( IpReservationService.class );
}