package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;

public interface EmployeeRegistrationReminderService {

    CoreResponse<Boolean> notifyAboutProbationPeriod();

    CoreResponse<Boolean> notifyAboutEmployeeFeedback();

    CoreResponse<Boolean> notifyAboutDevelopmentAgenda();
}
