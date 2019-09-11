package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

public interface EmployeeRegistrationReminderService {

    Result<Boolean> notifyAboutProbationPeriod();

    Result<Boolean> notifyAboutEmployeeFeedback();

    Result<Boolean> notifyAboutDevelopmentAgenda();
}
