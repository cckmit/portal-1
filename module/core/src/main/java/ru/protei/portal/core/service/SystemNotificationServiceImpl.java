package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.sn.model.dto.AbonentInsertResult;
import ru.protei.sn.model.dto.ExistsNotifyListReferences;
import ru.protei.sn.model.jdbc.MediaType;
import ru.protei.sn.model.jdbc.NotifyList;
import ru.protei.winter.audit.model.dto.AuthToken;
import ru.protei.winter.repo.model.dto.ContentInfo;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

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

    @Override
    public Result<AuthToken> authorize() {

        log.info("SN authorize");

        PortalConfigData.SnConfig snConfig = portalConfig.data().getSnConfig();

        if (snConfig == null || isEmpty(snConfig.getLogin()) || isEmpty(snConfig.getPassword())){
            log.warn("SN authorize failed: incorrect credentials");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        String hostAddress;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostAddress = "0.0.0.0";
        }
        ru.protei.sn.model.dto.Result<AuthToken> result = accountService.authorization(snConfig.getLogin(), snConfig.getPassword(), hostAddress);

        log.info("SN authorize status = {}", result.getStatus());

        if (result.isError()){
            log.warn("SN authorize failed");
            return error(En_ResultStatus.GET_DATA_ERROR, result.getThrowable().getMessage());
        }

        log.debug("SN authorize result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    @Override
    public Result<Long> createNotifyList(AuthToken authToken, String notifyListName ) {

        log.info("SN createNotifyList, token = {}, notifyListName = {}", authToken, notifyListName);

        PortalConfigData.SnConfig snConfig = portalConfig.data().getSnConfig();

        if (isEmpty(notifyListName) || isEmpty(snConfig.getFromNumber())){
            log.warn("SN createNotifyList failed: incorrect params");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        NotifyList notifyList = new NotifyList();
        notifyList.setName(notifyListName);
        notifyList.setCustomerId(authToken.getCustomerId());
        notifyList.setMediaType(MediaType.VOICE);
        notifyList.setFromAddress(snConfig.getFromNumber());

        ru.protei.sn.model.dto.Result<Long> result = notifyListService.createNotifyList(authToken, notifyList);

        log.info("SN createNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.warn("SN createNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, result.getThrowable().getMessage());
        }

        log.debug("SN createNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    @Override
    public Result<NotifyList> getNotifyList(AuthToken authToken, Long notifyListId) {

        log.info("SN getNotifyList, token = {}, notifyListId = {}", authToken, notifyListId);

        ru.protei.sn.model.dto.Result<NotifyList> result = notifyListService.getNotifyList(authToken, notifyListId);

        log.info("SN getNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.warn("SN getNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, result.getThrowable().getMessage());
        }

        log.debug("SN getNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    @Override
    public Result<List<ContentInfo>> getContentsInfo(AuthToken authToken, Set<Long> attachmentIds) {

        log.info("SN getContentsInfo, token = {}, attachmentIds = {}", authToken, attachmentIds);

        ru.protei.sn.model.dto.Result<List<ContentInfo>> result = notifyListService.getContentsInfo(authToken, attachmentIds);

        log.info("SN getContentsInfo status = {}", result.getStatus());

        if (result.isError()){
            log.warn("SN getContentsInfo failed");
            return error(En_ResultStatus.GET_DATA_ERROR, result.getThrowable().getMessage());
        }

        log.debug("SN getContentsInfo result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    @Override
    public Result<ExistsNotifyListReferences> updateNotifyList(AuthToken authToken, NotifyList notifyList) {
        log.info("SN updateNotifyList, token = {}, notifyList = {}", authToken, notifyList);

        ru.protei.sn.model.dto.Result<ExistsNotifyListReferences> result = notifyListService.updateNotifyList(authToken, notifyList);

        log.info("SN updateNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.warn("SN updateNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, result.getThrowable().getMessage());
        }

        log.debug("SN updateNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

    @Override
    public Result<AbonentInsertResult> insertAbonents(AuthToken authToken, Set<String> abonents, Long notifyListId) {

        log.info("SN insertAbonents, token = {}, abonents = {}, notifyListId = {}", authToken, abonents, notifyListId);

        ru.protei.sn.model.dto.Result<AbonentInsertResult> result = abonentService.insertAbonents(authToken, abonents, notifyListId);

        log.info("SN insertAbonents status = {}", result.getStatus());

        if (result.isError()){
            log.warn("SN insertAbonents failed");
            return error(En_ResultStatus.GET_DATA_ERROR, result.getThrowable().getMessage());
        }

        log.debug("SN insertAbonents result = {}", result.getResultObject());

        log.debug("NumberInsertAbonents = {}", result.getResultObject().getNumberInsertAbonents());
        log.debug("AbonentsInBlackListCount = {}", result.getResultObject().getAbonentsInBlackListCount());
        log.debug("DuplicateAddressesList = {}", result.getResultObject().getDuplicateAddressesList());
        log.debug("SkippedDuplicateAbonents = {}", result.getResultObject().getSkippedDuplicateAbonents());
        log.debug("FromAddressSetToNullCount = {}", result.getResultObject().getFromAddressSetToNullCount());
        log.debug("UnresolvedTimeZoneAbonents = {}", result.getResultObject().getUnresolvedTimeZoneAbonents());

        return ok(result.getResultObject());
    }

    @Override
    public Result<Long> startNotifyList(AuthToken authToken, Long notifyListId, boolean onlyNotNotified) {

        log.info("SN startNotifyList, token = {}, notifyListId = {}, onlyNotNotified = {}", authToken, notifyListId, onlyNotNotified);

        ru.protei.sn.model.dto.Result<Long> result = notifyListService.startNotifyList(authToken, notifyListId, onlyNotNotified);

        log.info("SN startNotifyList status = {}", result.getStatus());

        if (result.isError()){
            log.warn("SN startNotifyList failed");
            return error(En_ResultStatus.GET_DATA_ERROR, result.getThrowable().getMessage());
        }

        log.debug("SN startNotifyList result = {}", result.getResultObject());

        return ok(result.getResultObject());
    }

}
