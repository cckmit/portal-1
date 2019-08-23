package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

public interface ContractReminderService {

    Result<Integer> notifyAboutDates();
}
