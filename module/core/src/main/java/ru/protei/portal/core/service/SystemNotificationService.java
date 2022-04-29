package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.sn.model.dto.AbonentInsertResult;
import ru.protei.sn.model.dto.ExistsNotifyListReferences;
import ru.protei.sn.model.jdbc.NotifyList;
import ru.protei.winter.audit.model.dto.AuthToken;
import ru.protei.winter.repo.model.dto.ContentInfo;

import java.util.List;
import java.util.Set;

public interface SystemNotificationService {


    Result<AuthToken> authorize();

    Result<Long> createNotifyList(AuthToken authToken, String notifyListName);

    Result<NotifyList> getNotifyList(AuthToken authToken, Long notifyListId);

    Result<List<ContentInfo>> getContentsInfo(AuthToken authToken, Set<Long> attachmentIds);

    Result<ExistsNotifyListReferences> updateNotifyList(AuthToken authToken, NotifyList notifyList);

    Result<AbonentInsertResult> insertAbonents(AuthToken authToken, Set<String> abonents, Long notifyListId);

    Result<Long> startNotifyList(AuthToken authToken, Long notifyListId, boolean onlyNotNotified);
}
