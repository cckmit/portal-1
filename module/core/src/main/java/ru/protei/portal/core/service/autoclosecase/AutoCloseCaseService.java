package ru.protei.portal.core.service.autoclosecase;

public interface AutoCloseCaseService {

    void processAutoCloseByDeadLine();

    void notifyAboutDeadlineExpire();
}
