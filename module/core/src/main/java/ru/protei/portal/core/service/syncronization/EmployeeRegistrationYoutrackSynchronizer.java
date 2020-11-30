package ru.protei.portal.core.service.syncronization;

public interface EmployeeRegistrationYoutrackSynchronizer {
    boolean isScheduleSynchronizationNeeded();

    void synchronizeAll();
}
