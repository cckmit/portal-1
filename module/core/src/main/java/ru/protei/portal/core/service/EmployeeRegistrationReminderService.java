package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.EmployeeRegistration;

public interface EmployeeRegistrationReminderService {

    Result<Boolean> notifyAboutProbationPeriod();

    Result<Boolean> notifyAboutEmployeeFeedback();

    Result<Boolean> notifyAboutDevelopmentAgenda();

    Result<Boolean> notifyAboutEmployeeProbationPeriod(EmployeeRegistration employeeRegistration);

    Result<Boolean> notifyEmployeeAboutDevelopmentAgenda(EmployeeRegistration employeeRegistration);
}
