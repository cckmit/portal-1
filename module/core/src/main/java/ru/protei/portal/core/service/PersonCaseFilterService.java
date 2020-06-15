package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

public interface PersonCaseFilterService {
    Result<Void> processMailNotification();
}
