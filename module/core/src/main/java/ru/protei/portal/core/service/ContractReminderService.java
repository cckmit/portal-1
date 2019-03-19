package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;

public interface ContractReminderService {

    CoreResponse<Boolean> notifyAboutDates();
}
