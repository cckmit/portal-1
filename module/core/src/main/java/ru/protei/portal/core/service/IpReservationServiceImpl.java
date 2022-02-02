package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.ReservedIpAdminNotificationEvent;
import ru.protei.portal.core.event.ReservedIpNotificationEvent;
import ru.protei.portal.core.event.ReservedIpReleaseRemainingEvent;
import ru.protei.portal.core.event.SubnetNotificationEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.ReservedIpDAO;
import ru.protei.portal.core.model.dao.SubnetDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEHostReachable;
import ru.protei.portal.core.model.struct.nrpe.response.NRPERaw;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEResponse;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.nrpe.NRPEService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.event.ReservedIpReleaseRemainingEvent.Recipient;
import static ru.protei.portal.core.model.dict.En_ResultStatus.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeDateWithOffset;

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

    @Autowired
    NRPEService nrpeService;

    @Override
    public Result<Boolean> isSubnetAddressExists( String address, Long excludeId) {

        if( address == null || address.isEmpty() )
            return error(INCORRECT_PARAMS);

        return ok(checkSubnetExists(address, excludeId));
    }

    @Override
    public Result<Boolean> isReservedIpAddressExists(String address) {

        if (address == null || address.isEmpty()) {
            return error(INCORRECT_PARAMS);
        }

        return ok(isReserved(address));
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
            return error(GET_DATA_ERROR);
        }

        List<SubnetOption> options = result.stream()
                .map(s -> new SubnetOption(
                        s.getAddress() + "." +  s.getMask() + " " + s.getComment(),
                        s.getId(),
                        s.isAllowForReserve()))
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
                : error( NOT_FOUND);
    }

    @Override
    public Result<ReservedIp> getReservedIp( AuthToken authToken, Long id ) {
        ReservedIp reservedIp = getReservedIp(id);

        return reservedIp != null ? Result.ok( reservedIp)
                : error( NOT_FOUND);
    }

    @Override
    @Transactional
    public Result<Subnet> createSubnet( AuthToken token, Subnet subnet) {

        if (!isValidSubnet(subnet)) {
            return error(INCORRECT_PARAMS);
        }

        if (checkSubnetExists(subnet.getAddress(), subnet.getId()))
            return error(ALREADY_EXIST);

        subnet.setCreated(new Date());
        subnet.setCreatorId(token.getPersonId());
        Long subnetId = subnetDAO.persist(subnet);

        if (subnetId == null)
            return error(NOT_CREATED);

        subnet.setId(subnetId);
        return ok(subnet);
    }

    @Override
    @Transactional
    public Result<Subnet> updateSubnet( AuthToken token, Subnet subnet ) {

        if (subnet == null || subnet.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!isValidSubnet(subnet)) {
            return error(INCORRECT_PARAMS);
        }

        Subnet oldSubnet = subnetDAO.get(subnet.getId());

        if (!Objects.equals(oldSubnet.getAddress(), subnet.getAddress()) ||
            !Objects.equals(oldSubnet.getMask(), subnet.getMask())) {
            return error(INCORRECT_PARAMS);
        }

        Boolean result = subnetDAO.merge(subnet);
        if ( !result ) {
            return error(NOT_UPDATED);
        }
        return ok(subnet);
    }

    @Override
    public Result<Boolean> isSubnetAvailableToRemove(AuthToken token, Long subnetId) {
        if( subnetId == null )
            return error(INCORRECT_PARAMS);

        return ok(subnetAvailableToRemove(subnetId));
    }

    @Override
    @Transactional
    public Result<Long> removeSubnet( AuthToken token, Subnet subnet, boolean removeWithIps) {
        if (subnet == null) {
            return error(INCORRECT_PARAMS);
        }

        if (removeWithIps || subnetAvailableToRemove(subnet.getId())) {

            Subnet stored = subnetDAO.get(subnet.getId());
            List<ReservedIp> reservedIps = reservedIpDAO.getReservedIpsBySubnetId(subnet.getId());

            if (!subnetDAO.removeByKey(subnet.getId())) {
                return error(NOT_FOUND);
            }

            if (CollectionUtils.isEmpty(reservedIps)) {
                return ok(stored.getId());
            }

            return ok(stored.getId())
                    .publishEvent(new SubnetNotificationEvent(
                            this,
                            stored,
                            getInitiator(token),
                            SubnetNotificationEvent.Action.REMOVED,
                            makeNotificationListFromReservedIps(reservedIps)
                    ));
        }

        return error(NOT_REMOVED);
    }

    @Override
    @Transactional
    public Result<List<ReservedIp>> createReservedIp( AuthToken token, ReservedIpRequest reservedIpRequest) {

        if (!isValidRequest(reservedIpRequest)) {
            return error(INCORRECT_PARAMS);
        }

        List<ReservedIp> reservedIps = new ArrayList<>();
        List<String> nrpeNonAvailableIps = new ArrayList<>();

        ReservedIp templateIp = new ReservedIp();
        templateIp.setCreated(new Date());
        templateIp.setCreatorId(token.getPersonId());
        templateIp.setOwnerId(reservedIpRequest.getOwnerId());
        templateIp.setComment(reservedIpRequest.getComment());

        En_DateIntervalType intervalType = reservedIpRequest.getDateIntervalType();

        fillDatesInterval(reservedIpRequest.getReserveDate(), reservedIpRequest.getReleaseDate(), templateIp, intervalType);

        if (reservedIpRequest.isExact()) {
            if (isReserved(reservedIpRequest.getIpAddress())) {
                return error(ALREADY_EXIST);
            }

            if (config.data().getNrpeConfig().getEnable()) {
                NRPEResponse nrpeResponse = nrpeService.checkIp(reservedIpRequest.getIpAddress());
                if (nrpeResponse == null) {
                    return error(NRPE_ERROR);
                }

                if (nrpeResponse.getNRPEStatus() == En_NRPEStatus.HOST_REACHABLE) {
                    return error(NRPE_IP_NON_AVAILABLE, null, Collections.singletonList(
                                    new ReservedIpAdminNotificationEvent(this, ((NRPEHostReachable)nrpeResponse).ipsAndMacs()))
                    );
                }
            }

            templateIp.setIpAddress(reservedIpRequest.getIpAddress());
            templateIp.setMacAddress(StringUtils.isBlank(reservedIpRequest.getMacAddress()) ?
                            null : reservedIpRequest.getMacAddress().trim());

            Subnet subnet = subnetDAO.getSubnetByAddress(reservedIpRequest.getSubnetAddress());
            if (subnet == null) {
                return error(SUBNET_DOES_NOT_EXIST);
            }
            if (!subnet.isAllowForReserve()) {
                return error(SUBNET_NOT_ALLOWED_FOR_RESERVE);
            }
            templateIp.setSubnetId(subnet.getId());

            Long reservedIpId = reservedIpDAO.persist(templateIp);

            if (reservedIpId == null)
                return error(NOT_CREATED);

            reservedIps.add(getReservedIp(reservedIpId));

        } else {
            List<SubnetOption> subnets = getAvailableSubnets(token, new ArrayList<>(reservedIpRequest.getSubnets()));
            if (CollectionUtils.isEmpty(subnets)) {
                return error(NOT_CREATED);
            }

            IpInfoIterator ipInfoIterator = new IpInfoIterator(
                    subnets,
                    this::hasSubnetFreeIps,
                    subnetDAO::get,
                    this::getReservedIpNumInSubnet
            );
            Stream<IpInfo> freeIpInfoStream = CollectionUtils.stream(ipInfoIterator);

            if (config.data().getNrpeConfig().getEnable()) {
                freeIpInfoStream = freeIpInfoStream.filter(makeNRPETest(nrpeNonAvailableIps, ipInfoIterator));
            }

            List<ReservedIp> reservations = freeIpInfoStream
                    .limit(reservedIpRequest.getNumber())
                    .map(ipInfo -> {
                        ReservedIp reservedIp = ReservedIp.createByTemplate(templateIp);
                        reservedIp.setSubnetId(ipInfo.getSubnetId());
                        reservedIp.setIpAddress(ipInfo.getIp());
                        return reservedIp;
                    }).collect(Collectors.toList());

            if (ipInfoIterator.getStatus() != OK) {
                return error(ipInfoIterator.getStatus());
            }

            reservedIps.addAll(stream(reservations)
                    .map(reservedIp -> {
                        Long id = reservedIpDAO.persist(reservedIp);
                        if (id == null) {
                            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
                        }
                        return id;
                    })
                    .map(this::getReservedIp)
                    .collect(Collectors.toList()));
        }

        Result<List<ReservedIp>> result = ok(reservedIps)
                .publishEvent(new ReservedIpNotificationEvent(
                        this,
                        reservedIps,
                        getInitiator(token),
                        ReservedIpNotificationEvent.Action.CREATED,
                        makeNotificationListFromPersonIds(
                                token.getPersonId(),
                                reservedIpRequest.getOwnerId())
                ));
        if (!nrpeNonAvailableIps.isEmpty()) {
            result.publishEvent(new ReservedIpAdminNotificationEvent(this, nrpeNonAvailableIps));
        }
        return result;
    }

    @Override
    @Transactional
    public Result<ReservedIp> updateReservedIp( AuthToken token, ReservedIp reservedIp ) {

        if (reservedIp == null || reservedIp.getId() == null) {
            return error(INCORRECT_PARAMS);
        }

        ReservedIp stored = reservedIpDAO.get(reservedIp.getId());

        if (token == null
            || !(hasAccessForReservedIp(token, En_Privilege.RESERVED_IP_EDIT, reservedIp)
            || hasAccessForReservedIp(token, En_Privilege.RESERVED_IP_EDIT, stored))
        ) {
            throw new RollbackTransactionException(PERMISSION_DENIED);
        }

        if (!isValidReservedIp(token, reservedIp, stored)) {
            return error(INCORRECT_PARAMS);
        }

        ReservedIp oldReservedIp = reservedIpDAO.get(reservedIp.getId());

        if (!Objects.equals(oldReservedIp.getIpAddress(), reservedIp.getIpAddress())) {
            return error(INCORRECT_PARAMS);
        }

        Boolean result = reservedIpDAO.merge(reservedIp);
        if ( !result ) {
            return error(NOT_UPDATED);
        }

        reservedIp = getReservedIp(reservedIp.getId());

        return ok(reservedIp)
                .publishEvent(new ReservedIpNotificationEvent(
                        this,
                        reservedIp,
                        getInitiator(token),
                        ReservedIpNotificationEvent.Action.UPDATED,
                        makeNotificationListFromPersonIds(
                                token.getPersonId(),
                                reservedIp.getOwnerId(),
                                stored.getOwnerId())
                ));
    }

    @Override
    @Transactional
    public Result<Long> removeReservedIp(AuthToken token, ReservedIp reservedIp ) {
        if (reservedIp == null) {
            return error(INCORRECT_PARAMS);
        }

        if (token == null || !hasAccessForReservedIp(token, En_Privilege.RESERVED_IP_REMOVE, reservedIp)) {
            throw new RollbackTransactionException(PERMISSION_DENIED);
        }

        ReservedIp stored = getReservedIp(reservedIp.getId());

        if (!reservedIpDAO.removeByKey(reservedIp.getId())) {
            return error(NOT_FOUND);
        }

        return ok(stored.getId())
                .publishEvent(new ReservedIpNotificationEvent(
                        this,
                        stored,
                        getInitiator(token),
                        ReservedIpNotificationEvent.Action.REMOVED,
                        makeNotificationListFromPersonIds(token.getPersonId(), stored.getOwnerId())
                ));
    }

    /**
     * Уведомление ответственных о приближении даты освобождения IP адреса в ближайшие 3 дня
     * @return
     */
    @Override
    public Result<Void> notifyOwnersAboutReleaseIp() {

        Date releaseDateStart = makeDateWithOffset(1);
        Date releaseDateEnd = makeDateWithOffset(CrmConstants.IpReservation.RELEASE_DATE_EXPIRES_IN_DAYS);

        log.info("notifyOwnersAboutReleaseIp(): start : from {} to {}", releaseDateStart, releaseDateEnd);

        ReservedIpQuery query = new ReservedIpQuery();
        query.setReleasedFrom(releaseDateStart);
        query.setReleasedTo(releaseDateEnd);
        Map<Long, List<ReservedIp>> reservedIpsByOwners = getReservedIpsByOwners(query);
        if (reservedIpsByOwners.isEmpty()) {
            log.info("notifyOwnersAboutReleaseIp(): expired reservedIps is empty for period from {} to {}", releaseDateStart, releaseDateEnd);
            return ok();
        }

        int notificationSentAmount = 0;

        for (Long ownerId : reservedIpsByOwners.keySet()) {

            List<NotificationEntry> notificationEntries = Arrays.asList(makeNotificationEntryFromPersonId(ownerId));

            if (CollectionUtils.isEmpty(notificationEntries)) {
                log.info("notifyOwnersAboutReleaseIp(): notification for owner {} to release IPs: no entries to be notified", ownerId);
                continue;
            }
            log.info("notifyOwnersAboutReleaseIp(): notification for owner {} to release IPs: entries to be notified: {}",
                    ownerId, notificationEntries);
            publisherService.publishEvent(new ReservedIpReleaseRemainingEvent(
                    this, reservedIpsByOwners.get(ownerId), releaseDateStart, releaseDateEnd, notificationEntries, Recipient.OWNER_IP));
            notificationSentAmount++;
        }

        log.info("notifyOwnersAboutReleaseIp(): done {} notification(s)", notificationSentAmount);
        return ok();
    }

    /**
     * Уведомление системных администраторов об истечении даты освобождения IP адресов
     * @return
     */
    @Override
    public Result<Void> notifyAdminsAboutExpiredReleaseDates() {

        Date today = makeDateWithOffset(0);

        log.info("notifyAdminsAboutExpiredReleaseDates(): start : already expired to {}", today);

        ReservedIpQuery query = new ReservedIpQuery();
        query.setReleasedTo(today);
        List<ReservedIp> reservedIps = getReservedIps(query);

        if (CollectionUtils.isEmpty(reservedIps)) {
            log.info("notifyAdminsAboutExpiredReleaseDates(): expired release date IP list is empty for today {}", today);
            return ok();
        }

        List<NotificationEntry> notificationEntries = makeNotificationListFromConfiguration();

        if (CollectionUtils.isEmpty(notificationEntries)) {
            log.info("notifyAdminsAboutExpiredReleaseDates(): notification for expired release date IPs: no entries to be notified");
            return ok();
        }

        log.info("notifyAdminsAboutExpiredReleaseDates(): notification for expired release date IPs: entries to be notified: {}", notificationEntries);
        publisherService.publishEvent(new ReservedIpReleaseRemainingEvent(
                this, reservedIps, null, today, notificationEntries, Recipient.ADMIN));

        log.info("notifyAdminsAboutExpiredReleaseDates(): done");
        return ok();
    }

    @Override
    @Transactional
    public Result<Boolean> isIpOnline(AuthToken token, ReservedIp reservedIp) {
        if (reservedIp == null || reservedIp.getIpAddress() == null) {
            return error(INCORRECT_PARAMS);
        }

        if (!config.data().getNrpeConfig().getEnable()) {
            return error(NRPE_NOT_CONFIGURED);
        }

        NRPEResponse nrpeResponse = nrpeService.checkIp(reservedIp.getIpAddress());
        if (nrpeResponse == null) {
            return error(NRPE_ERROR);
        }

        switch (nrpeResponse.getNRPEStatus()) {
            case HOST_REACHABLE:
                final Date now = new Date();
                reservedIp.setLastActiveDate(now);
                reservedIp.setLastCheckInfo(makeCheckInfo((NRPERaw) nrpeResponse, now));
                reservedIpDAO.partialMerge(reservedIp, ReservedIp.Columns.LAST_ACTIVE_DATE, ReservedIp.Columns.LAST_CHECK_INFO);
                return ok(true);
            case HOST_UNREACHABLE:
                reservedIp.setLastCheckInfo(makeCheckInfo((NRPERaw) nrpeResponse, new Date()));
                reservedIpDAO.partialMerge(reservedIp, ReservedIp.Columns.LAST_CHECK_INFO);
                return ok(false);
            case SERVER_UNAVAILABLE:
            case INCORRECT_PARAMS:
                return error(NRPE_ERROR);
            default:
                return error(INTERNAL_ERROR);
        }
    }

    private String makeCheckInfo(NRPERaw response, Date now) {
        final List<String> rawResponse = response.getRawResponse();
        return now + "\n" + String.join("\n", rawResponse);
    }

    private void fillDatesInterval(Date reserveDate, Date releaseDate, ReservedIp templateIp, En_DateIntervalType intervalType) {
        Date today = makeDateWithOffset(0);
        Date throughMonth = makeDateWithOffset(30);
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
                templateIp.setReserveDate(reserveDate);
                templateIp.setReleaseDate(releaseDate);
        }
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
            if (CollectionUtils.isEmpty(reservedIpRequest.getSubnets()))
                return false;

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

    private boolean isValidReservedIp(AuthToken token, ReservedIp reservedIp, ReservedIp stored) {
        return reservedIp.getOwnerId() != null
                && isValidIpAddress(reservedIp.getIpAddress())
                && isValidMacAddress(reservedIp.getMacAddress())
                && isValidUseRange(token, reservedIp.getReserveDate(), reservedIp.getReleaseDate(), stored.getReleaseDate());
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

    private boolean isValidUseRange(AuthToken token, Date from, Date to, Date storedTo) {
        boolean isAllowToSetNullDate = isSystemAdministrator(token, En_Privilege.RESERVED_IP_CREATE) || null == storedTo;
        return from != null
                && (to != null && from.before(to) || isAllowToSetNullDate);
    }

    private List<SubnetOption> getAvailableSubnets(AuthToken token, List<SubnetOption> selectedSubnets) {
        List<SubnetOption> subnets;
        if (CollectionUtils.isNotEmpty(selectedSubnets)) {
            subnets = selectedSubnets;
        } else {
            ReservedIpQuery query = new ReservedIpQuery("", En_SortField.address, En_SortDir.ASC);
            query.setAllowForReserve(true);
            List<SubnetOption> result = getSubnetsOptionList(token, query).getData();

            if (CollectionUtils.isEmpty(result)) {
                return null;
            }

            subnets = result;
        }

        return subnets;
    }

    private boolean hasSubnetFreeIps(Long subnetId) {
        Long count = reservedIpDAO.countByExpression("subnet_id=?", subnetId);
        return count < CrmConstants.IpReservation.MAX_IPS_COUNT;
    }

    private Set<Integer> getReservedIpNumInSubnet(Subnet subnet) {
        if (subnet == null || !subnet.isAllowForReserve()) {
            return null;
        }

        ReservedIpQuery query = new ReservedIpQuery(null, En_SortField.ip_address, En_SortDir.ASC);
        query.setSubnetId(subnet.getId());
        SearchResult<ReservedIp> sr = reservedIpDAO.getSearchResultByQuery(query);

        if (sr.getTotalCount() == CrmConstants.IpReservation.MAX_IPS_COUNT) {
            return null;
        }

       return sr.getResults().stream()
               .map(ReservedIp::getIpAddress)
               .map(ip -> Integer.parseInt(ip.substring(ip.lastIndexOf('.') + 1)))
               .collect(Collectors.toSet());
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

    private boolean isReserved(String address) {
        List<ReservedIp> reservedIps = reservedIpDAO.getReservedIpsByAddress(address);
        return !isEmpty(reservedIps);
    }

    private boolean checkIntersections(Date reserveDate, Date releaseDate, List<ReservedIp> reservedIps) {
        boolean exists = false;

        for (ReservedIp reservedIp : reservedIps) {
            Date reservedIpReserveDate = reservedIp.getReserveDate();
            Date reservedIpReleaseDate = reservedIp.getReleaseDate();

            if (Objects.equals(new Date(reservedIpReserveDate.getTime()), reserveDate)) {
                return true;
            } else if (reservedIpReleaseDate == null || releaseDate == null) {
                exists = checkInfinityReleaseDates(reservedIpReserveDate, reserveDate, reservedIpReleaseDate, releaseDate);
            } else if (reservedIpReserveDate.before(reserveDate)) {
                exists = Objects.equals(new Date(reservedIpReleaseDate.getTime()), reserveDate)  || reservedIpReleaseDate.after(reserveDate);
            } else if (reserveDate.before(reservedIpReserveDate)) {
                exists = Objects.equals(new Date(reservedIpReserveDate.getTime()), releaseDate) || releaseDate.after(reservedIpReserveDate);
            }

            if (exists) {
                return true;
            }
        }

        return false;
    }

    private boolean checkInfinityReleaseDates(Date reservedIpReserveDate, Date newIpReserveDate, Date reservedIpReleaseDate, Date newIpReleaseDate) {
        if (newIpReleaseDate == null && reservedIpReleaseDate == null) {
            return true;
        }

        if (reservedIpReleaseDate == null) {
            return newIpReleaseDate.after(reservedIpReserveDate);
        }

        return reservedIpReleaseDate.after(newIpReserveDate);
    }

    private boolean subnetAvailableToRemove (Long subnetId) {
        ReservedIpQuery query = new ReservedIpQuery();
        query.setSubnetId(subnetId);
        Long result = reservedIpDAO.count(query);

        return result == null || result == 0;
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

    private List<NotificationEntry> makeNotificationListFromReservedIps(List<ReservedIp> reservedIps) {
        return stream(reservedIps)
                .map(ReservedIp::getOwnerId)
                .distinct()
                .map(ownerId -> {
                    return makeNotificationEntryFromPersonId(ownerId);
                })
                .filter(Objects::nonNull)
                .filter(entry -> StringUtils.isNotEmpty(entry.getAddress()))
                .collect(Collectors.toList());
    }

    private List<NotificationEntry> makeNotificationListFromPersonIds(Long ... personIds) {
        return Stream.of(personIds)
                .distinct()
                .map(ownerId -> {
                    return makeNotificationEntryFromPersonId(ownerId);
                })
                .filter(Objects::nonNull)
                .filter(entry -> StringUtils.isNotEmpty(entry.getAddress()))
                .collect(Collectors.toList());
    }

    private NotificationEntry makeNotificationEntryFromPersonId(Long personId) {
        Person person = personDAO.partialGet(personId, "id", "locale");
        if (person == null) {
            return null;
        }
        helper.fill(person, Person.Fields.CONTACT_ITEMS);

        PlainContactInfoFacade contact = new PlainContactInfoFacade(person.getContactInfo());
        return NotificationEntry.email(contact.getEmail(), person.getLocale());
    }

    private List<NotificationEntry> makeNotificationListFromConfiguration() {
        return Stream.of(
                config.data().getMailNotificationConfig().getCrmIpReservationNotificationsRecipients()
        )
                .filter(Objects::nonNull)
                .filter(not(String::isEmpty))
                .map(address -> NotificationEntry.email(address, CrmConstants.DEFAULT_LOCALE))
                .collect(Collectors.toList());
    }

    private Person getInitiator (AuthToken token) {
        return personDAO.get(token.getPersonId());
    }

    private Predicate<IpInfo> makeNRPETest(List<String> NRPENonAvailableIps, IpInfoIterator ipInfoIterator) {
        return checkedIpInfo -> {
            NRPEResponse nrpeResponse = nrpeService.checkIp(checkedIpInfo.getIp());
            if (nrpeResponse == null) {
                ipInfoIterator.interrupt(NRPE_ERROR);
                return false;
            }

            switch (nrpeResponse.getNRPEStatus()) {
                case HOST_UNREACHABLE:
                    return true;
                case HOST_REACHABLE:
                    NRPENonAvailableIps.addAll(((NRPEHostReachable) nrpeResponse).ipsAndMacs());
                    return false;
                case SERVER_UNAVAILABLE:
                case INCORRECT_PARAMS:
                    ipInfoIterator.interrupt(NRPE_ERROR);
                    return false;
                default:
                    ipInfoIterator.interrupt(INTERNAL_ERROR);
                    return false;
            }
        };
    }

    static class IpInfo {
        private final String ip;
        private final long subnetId;

        public IpInfo(String ip, long subnetId) {
            this.ip = ip;
            this.subnetId = subnetId;
        }

        public String getIp() {
            return ip;
        }

        public long getSubnetId() {
            return subnetId;
        }
    }

    static class IpInfoIterator implements Iterator<IpInfo> {
        private final List<SubnetOption> subnets;
        private int nextSubnetIndex;
        private Subnet currentSubnet;
        private int nextNumber;
        private Set<Integer> subnetDBReservedIps;

        private IpInfo nextIpInfo;
        private En_ResultStatus status = OK;

        private final Predicate<Long> isSubnetHasFreeIp;
        private final Function<Long, Subnet> subnetById;
        private final Function<Subnet, Set<Integer>> subnetReservedIpsBySubnet;

        public IpInfoIterator(List<SubnetOption> subnets, Predicate<Long> isSubnetHasFreeIp,
                              Function<Long, Subnet> subnetById, Function<Subnet, Set<Integer>> subnetReservedIpsBySubnet) {
            this.subnets = subnets;
            this.isSubnetHasFreeIp = isSubnetHasFreeIp;
            this.subnetById = subnetById;
            this.subnetReservedIpsBySubnet = subnetReservedIpsBySubnet;
            this.nextSubnetIndex = 0;
            refreshData();
        }

        public En_ResultStatus getStatus() {
            return status;
        }

        public void interrupt(En_ResultStatus status) {
            this.status = status;
        }

        private void refreshData() {
            if (status != OK) {
                return;
            }

            SubnetOption subnetOption;
            do {
                int index = nextSubnetIndex++;
                if (index >= subnets.size()) {
                    status = NRPE_NO_FREE_IPS;
                    return;
                }
                subnetOption = subnets.get(index);
            } while (!isSubnetHasFreeIp.test(subnetOption.getId()));

            nextNumber = CrmConstants.IpReservation.MIN_IPS_COUNT;
            currentSubnet = subnetById.apply(subnetOption.getId());
            subnetDBReservedIps = subnetReservedIpsBySubnet.apply(currentSubnet);
        }

        private IpInfo getNextIpInfo() {
            if (status != OK) {
                return null;
            }
            int number;
            do {
                if (nextNumber > CrmConstants.IpReservation.MAX_IPS_COUNT) {
                    refreshData();
                }
                if (status != OK) {
                    return null;
                }
                number = nextNumber++;
            } while (subnetDBReservedIps.contains(number));
            return new IpInfo(currentSubnet.getAddress() + "." + number, currentSubnet.getId());
        }

        @Override
        public boolean hasNext() {
            IpInfo next = getNextIpInfo();
            if (status == OK && next != null) {
                nextIpInfo = next;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public IpInfo next() {
            if (nextIpInfo == null) {
                throw new NoSuchElementException();
            }
            IpInfo next = nextIpInfo;
            nextIpInfo = null;
            return next;
        }
    }

    private final static Logger log = LoggerFactory.getLogger( IpReservationService.class );
}
