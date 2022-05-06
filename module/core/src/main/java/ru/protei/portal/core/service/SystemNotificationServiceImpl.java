package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.model.dao.CommonManagerToNotifyListDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CommonManagerToNotifyList;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.sn.model.dto.AbonentInsertResult;
import ru.protei.sn.model.dto.ExistsNotifyListReferences;
import ru.protei.sn.model.jdbc.*;
import ru.protei.sn.model.jdbc.attachment.AttachmentUnit;
import ru.protei.sn.model.jdbc.attachment.impl.ContentAttachmentUnit;
import ru.protei.winter.audit.model.dto.AuthToken;
import ru.protei.winter.core.utils.scheduler.DaysOfWeek;
import ru.protei.winter.core.utils.scheduler.ScheduleDays;
import ru.protei.winter.core.utils.scheduler.SchedulerItem;
import ru.protei.winter.core.utils.scheduler.Time;
import ru.protei.winter.core.utils.schedulerutils.ScheduleData;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;
import ru.protei.winter.repo.model.dto.ContentInfo;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_ContactItemType.GENERAL_PHONE;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.SN.DEFAULT_SCHEME_REASON_ID;
import static ru.protei.portal.core.model.util.CrmConstants.SN.NOTIFY_LIST_NAME;

/**
 * Сервис для работы с системой оповещения SN
 */
public class SystemNotificationServiceImpl implements SystemNotificationService {

    private static Logger log = LoggerFactory.getLogger(SystemNotificationServiceImpl.class);

    @Autowired
    PortalConfig portalConfig;
    @Autowired
    ru.protei.sn.api.AccountService accountService;
    @Autowired
    ru.protei.sn.api.NotifyListService notifyListService;
    @Autowired
    ru.protei.sn.api.NotifyAbonentService abonentService;
    @Autowired
    PersonService personService;
    @Autowired
    CommonManagerToNotifyListDAO commonManagerToNotifyListDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public void fillCommonManagerMapping() {
        log.info("fillCommonManagerMapping");

        PortalConfigData.SnConfig snConfig = portalConfig.data().getSnConfig();

        if (!snConfig.isNotificationEnabled()){
            log.warn("fillCommonManagerMapping failed: notification not allowed");
            return;
        }

        List<CommonManagerToNotifyList> commonManagerToNotifyLists = commonManagerToNotifyListDAO.getAll();

        if (CollectionUtils.isEmpty(commonManagerToNotifyLists)){
            log.warn("fillCommonManagerMapping: no common managers in the system");
            return;
        }

        stream(commonManagerToNotifyLists).forEach(this::checkNotifyListCreate);
    }

    //Пока не используется, продолжение разработки в задаче PORTAL-2186
    @Override
    public Result<Void> startNotification(Long managerId) {
        log.debug("startNotification managerId = {}", managerId);

        PortalConfigData.SnConfig snConfig = portalConfig.data().getSnConfig();
        if (!snConfig.isNotificationEnabled()){
            log.warn("startNotification failed: notification not allowed");
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Result<ru.protei.winter.audit.model.dto.AuthToken> authorizeResult = authorize();

        if (authorizeResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        ru.protei.winter.audit.model.dto.AuthToken authTokenSN = authorizeResult.getData();

        Result<Long> notifyListIdResult = personService.getNotifyListIdByCommonManagerId(managerId);
        if (notifyListIdResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        Result<NotifyList> getNotifyListResult = getNotifyList(authTokenSN, notifyListIdResult.getData());

        if (getNotifyListResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        NotifyList notifyList = getNotifyListResult.getData();

        //требуется устанавливать новые даты оповещения для каждого запуска списка
        notifyList.getScheduleData().setStartStopDate(new Date(), new Date(System.currentTimeMillis() + snConfig.getScheduleInterval()));
        Result<ExistsNotifyListReferences> updateNotifyListScheduleResult = updateNotifyList(authTokenSN, notifyList);

        if (updateNotifyListScheduleResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        Result<Long> startNotifyListResult = startNotifyList(authTokenSN, notifyList.getId(), false);

        if (startNotifyListResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok();
    }

    private Result<AuthToken> authorize() {

        log.info("authorize");

        PortalConfigData.SnConfig snConfig = portalConfig.data().getSnConfig();

        if (snConfig == null || isEmpty(snConfig.getLogin()) || isEmpty(snConfig.getPassword())){
            log.warn("authorize failed: incorrect credentials");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        ru.protei.sn.model.dto.Result<AuthToken> result = accountService.authorization(snConfig.getLogin(), snConfig.getPassword(), getHostAddress());

        log.info("authorize status = {}", result.getStatus());

        if (result.isError()){
            log.error("authorize failed");
            return error(En_ResultStatus.GET_DATA_ERROR, getThrowableMessage(result));
        }

        log.debug("authorize result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    private String getHostAddress() {
        String hostAddress;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostAddress = "0.0.0.0";
        }
        return hostAddress;
    }

    private Result<Long> createNotifyList(AuthToken authToken, String notifyListName ) {

        log.info("createNotifyList, token = {}, notifyListName = {}", authToken, notifyListName);

        PortalConfigData.SnConfig snConfig = portalConfig.data().getSnConfig();

        if (isEmpty(notifyListName) || isEmpty(snConfig.getFromNumber())){
            log.warn("createNotifyList failed: incorrect params");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        NotifyList notifyList = new NotifyList();
        notifyList.setName(notifyListName);
        notifyList.setCustomerId(authToken.getCustomerId());
        notifyList.setMediaType(MediaType.VOICE);
        notifyList.setFromAddress(snConfig.getFromNumber());

        ru.protei.sn.model.dto.Result<Long> result = notifyListService.createNotifyList(authToken, notifyList);

        log.info("createNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.error("createNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, getThrowableMessage(result));
        }

        log.debug("createNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    private Result<NotifyList> getNotifyList(AuthToken authToken, Long notifyListId) {

        log.info("getNotifyList, token = {}, notifyListId = {}", authToken, notifyListId);

        ru.protei.sn.model.dto.Result<NotifyList> result = notifyListService.getNotifyList(authToken, notifyListId);

        log.info("getNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.error("getNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, getThrowableMessage(result));
        }

        log.debug("getNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    private Result<List<ContentInfo>> getContentsInfo(AuthToken authToken, Set<Long> attachmentIds) {

        log.info("getContentsInfo, token = {}, attachmentIds = {}", authToken, attachmentIds);

        ru.protei.sn.model.dto.Result<List<ContentInfo>> result = notifyListService.getContentsInfo(authToken, attachmentIds);

        log.info("getContentsInfo status = {}", result.getStatus());

        if (result.isError()){
            log.error("getContentsInfo failed");
            return error(En_ResultStatus.GET_DATA_ERROR, getThrowableMessage(result));
        }

        log.debug("getContentsInfo result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    private Result<ExistsNotifyListReferences> updateNotifyList(AuthToken authToken, NotifyList notifyList) {
        log.info("updateNotifyList, token = {}, notifyList = {}", authToken, notifyList);

        ru.protei.sn.model.dto.Result<ExistsNotifyListReferences> result = notifyListService.updateNotifyList(authToken, notifyList);

        log.info("updateNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.error("updateNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, getThrowableMessage(result));
        }

        log.debug("updateNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    private Result<AbonentInsertResult> insertAbonents(AuthToken authToken, Set<String> abonents, Long notifyListId) {

        log.info("insertAbonents, token = {}, abonents = {}, notifyListId = {}", authToken, abonents, notifyListId);

        ru.protei.sn.model.dto.Result<AbonentInsertResult> result = abonentService.insertAbonents(authToken, abonents, notifyListId);

        log.info("insertAbonents status = {}", result.getStatus());

        if (result.isError()){
            log.error("insertAbonents failed");
            return error(En_ResultStatus.GET_DATA_ERROR, getThrowableMessage(result));
        }

        log.debug("insertAbonents result = {}", result.getResultObject());

        log.debug("NumberInsertAbonents = {}", result.getResultObject().getNumberInsertAbonents());
        log.debug("AbonentsInBlackListCount = {}", result.getResultObject().getAbonentsInBlackListCount());
        log.debug("DuplicateAddressesList = {}", result.getResultObject().getDuplicateAddressesList());
        log.debug("SkippedDuplicateAbonents = {}", result.getResultObject().getSkippedDuplicateAbonents());
        log.debug("FromAddressSetToNullCount = {}", result.getResultObject().getFromAddressSetToNullCount());
        log.debug("UnresolvedTimeZoneAbonents = {}", result.getResultObject().getUnresolvedTimeZoneAbonents());

        return ok(result.getResultObject());
    }

    private Result<Long> startNotifyList(AuthToken authToken, Long notifyListId, boolean onlyNotNotified) {

        log.info("startNotifyList, token = {}, notifyListId = {}, onlyNotNotified = {}", authToken, notifyListId, onlyNotNotified);

        ru.protei.sn.model.dto.Result<Long> result = notifyListService.startNotifyList(authToken, notifyListId, onlyNotNotified);

        log.info("startNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.error("startNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, getThrowableMessage(result));
        }

        log.debug("startNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    private void checkNotifyListCreate(CommonManagerToNotifyList managerToNotifyList) {

        log.debug("checkNotifyListCreate managerToNotifyList = {}", managerToNotifyList);

        if (managerToNotifyList == null) return;

        Long managerId = managerToNotifyList.getManagerId();
        Long notifyListId = managerToNotifyList.getNotifyListId();
        if (notifyListId == null){
            createNotifyListForManagerWithCallback(managerId);
        } else {
            checkNotifyListExists(notifyListId)
                    .ifError(o->createNotifyListForManagerWithCallback(managerId))
                    .ifOk(okCheckNotifyListExistsConsumer);
        }
    }

    private void createNotifyListForManagerWithCallback(Long managerId){
        createNotifyListForManager(managerId)
                .ifError(errorCreateNotifyListConsumer)
                .ifOk(newNotifyListId -> personService.updateCommonManagerToNotifyList(managerId, newNotifyListId));
    }

    private Result<Long> checkNotifyListExists(Long notifyListId) {
        log.info("checkNotifyListExists notifyListId = {}", notifyListId);

        Result<ru.protei.winter.audit.model.dto.AuthToken> authorizeResult = authorize();

        if (authorizeResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        ru.protei.winter.audit.model.dto.AuthToken authTokenSN = authorizeResult.getData();

        Result<NotifyList> getNotifyListResult = getNotifyList(authTokenSN, notifyListId);

        if (getNotifyListResult.isError() || getNotifyListResult.getData() == null){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        NotifyList notifyList = getNotifyListResult.getData();

        String fromNumber = portalConfig.data().getSnConfig().getFromNumber();
        if (StringUtils.isEmpty(fromNumber)){
            log.debug("checkNotifyListExists failed: incorrect fromNumber param");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!fromNumber.equals(notifyList.getFromAddress())){
            log.debug("checkNotifyListExists found notify list for another fromNumber: {}", notifyList.getFromAddress());
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(notifyList.getId());
    }

    private Result<Long> createNotifyListForManager(Long managerId) {
        log.debug("createNotifyListForManager managerId = {}", managerId);

        Result<ru.protei.winter.audit.model.dto.AuthToken> authorizeResult = authorize();

        if (authorizeResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        ru.protei.winter.audit.model.dto.AuthToken authTokenSN = authorizeResult.getData();

        Result<Long> createNotifyListResult = createNotifyList(authTokenSN, createNotifyListName(String.valueOf(managerId)));

        if (createNotifyListResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        Result<NotifyList> getNotifyListResult = getNotifyList(authTokenSN, createNotifyListResult.getData());

        if (getNotifyListResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        PortalConfigData.SnConfig snConfig = portalConfig.data().getSnConfig();

        Result<List<ContentInfo>> getContentsInfoResult = getContentsInfo(authTokenSN, Collections.singleton(snConfig.getPromptId()));

        if (getContentsInfoResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        List<ContentInfo> contentInfoList = getContentsInfoResult.getData();

        if (CollectionUtils.isEmpty(contentInfoList) || contentInfoList.size() != 1){
            log.warn("No attachment found for attachmentId = {}", snConfig.getPromptId());
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        ContentInfo contentInfo = contentInfoList.get(0);
        String contentInfoName = contentInfo == null ? "" : contentInfo.getName();
        log.debug("Found attachment with id = {} and name = {}", snConfig.getPromptId(), contentInfoName);

        NotifyList newNotifyList = getNotifyListResult.getData();

        Result<ExistsNotifyListReferences> updateNotifyListResult = updateNotifyList(authTokenSN, newNotifyList, contentInfoName, snConfig);

        if (updateNotifyListResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        Result<AbonentInsertResult> insertAbonentsResult = insertAbonents(authTokenSN, getAbonents(managerId), newNotifyList.getId());

        if (insertAbonentsResult.isError()){
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(newNotifyList.getId());
    }

    private Result<ExistsNotifyListReferences> updateNotifyList(ru.protei.winter.audit.model.dto.AuthToken authTokenSN, NotifyList notifyList, String contentInfoName, PortalConfigData.SnConfig snConfig) {

        Long promptId = snConfig.getPromptId();
        ru.protei.sn.model.jdbc.attachment.Attachment attachment = new ru.protei.sn.model.jdbc.attachment.Attachment();
        AttachmentUnit attachmentUnit = new ContentAttachmentUnit(promptId, contentInfoName);
        List<AttachmentUnit> attachmentUnits = Collections.singletonList(attachmentUnit);
        attachment.setAttachmentUnits(attachmentUnits);
        notifyList.setAttachments(Collections.singletonList(attachment));

        int schemaMaxTries = snConfig.getSchemaMaxTries();
        List<Integer> schemaIntervalList = Arrays.stream(snConfig.getSchemaIntervalList()).map(Integer::parseInt).collect(Collectors.toList());
        Map<Integer, SchemaIntervals> intervalMap = createIntervalsMap(schemaIntervalList);
        TimeUnit timeUnit = createTimeUnit(snConfig.getSchemaTimeUnit());

        Schema schema = new Schema();
        schema.setMaxCount( schemaMaxTries );
        schema.setIntervalsMap( intervalMap );
        schema.setTimeUnit( timeUnit );
        notifyList.setSchema(schema);

        notifyList.setDistributionInfo(createDistributionInfo());
        notifyList.setScheduleData(createScheduleData());
        return updateNotifyList(authTokenSN, notifyList);
    }

    //Оповещение будет круглосуточно, в любой день
    private ScheduleData createScheduleData() {
        ScheduleData scheduleData = new ScheduleData();
        SchedulerItem schedulerItem = new SchedulerItem();
        Time start = new Time(0, 0, 0);
        Time end = new Time(23, 59, 59);
        schedulerItem.addTimePeriod(start, end);
        ScheduleDays scheduleDays = new DaysOfWeek(Arrays.asList(1,2,3,4,5,6,7));
        schedulerItem.setScheduleDays(scheduleDays);
        scheduleData.addSchedulerItem(schedulerItem);
        return scheduleData;
    }

    //значения согласованы с командой SN, они обязательны, но ни на что не влияют при оповещении одного абонента
    private DistributionInfo createDistributionInfo() {
        DistributionInfo di = new DistributionInfo();
        di.setMediaType(MediaType.VOICE);
        di.setWeight(1);
        di.setMaxRate(1);
        di.setMaxCurrentTransactionCount(1);
        return di;
    }

    private Map<Integer, SchemaIntervals> createIntervalsMap(List<Integer> schemaIntervalList) {
        Map<Integer, SchemaIntervals> intervalMap = new HashMap<>();
        SchemaIntervals intervals = new SchemaIntervals(schemaIntervalList, false);
        intervalMap.put(DEFAULT_SCHEME_REASON_ID, intervals);

        return intervalMap;
    }

    private TimeUnit createTimeUnit(String schemaTimeUnit) {

        if (StringUtils.isEmpty(schemaTimeUnit)){
            return TimeUnit.MINUTES;
        }

        switch ( schemaTimeUnit.toLowerCase() ) {
            case "seconds":
                return TimeUnit.SECONDS;
            case "minutes":
                return TimeUnit.MINUTES;
            case "hours":
                return TimeUnit.HOURS;
        }
        return TimeUnit.MINUTES;
    }


    private String createNotifyListName(String commonManagername) {
        return NOTIFY_LIST_NAME + commonManagername + "_" + System.currentTimeMillis();
    }

    private String getThrowableMessage(ru.protei.sn.model.dto.Result<?> result) {
        if (result == null || result.getThrowable() == null){
            return "";
        }
        return result.getThrowable().getMessage();
    }

    private Set<String> getAbonents(Long managerId) {

        Person person = personDAO.get(managerId);
        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);

        if (person == null || person.getContactInfo() == null){
            return Collections.emptySet();
        }
        return stream(person.getContactInfo().getItems())
                .filter(item -> GENERAL_PHONE.equals(item.type()))
                .map(ContactItem::value)
                .collect(Collectors.toSet());
    }

    Consumer<? super Long> okCheckNotifyListExistsConsumer = new Consumer<Long>() {
        @Override
        public void accept(Long notifyListId) {
            log.debug("checkNotifyListExists notifyList with id = {} exists", notifyListId);
        }
    };

    Consumer<Result<Long>> errorCreateNotifyListConsumer = new Consumer<Result<Long>>() {
        @Override
        public void accept(Result<Long> result) {
            log.error("createNotifyListForManager error = {}", result.getStatus());
        }
    };
}
