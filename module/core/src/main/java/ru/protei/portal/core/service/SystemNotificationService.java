package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

public interface SystemNotificationService {

    void fillCommonManagerMapping();

    Result<Void> startNotification(Long managerId);
}
