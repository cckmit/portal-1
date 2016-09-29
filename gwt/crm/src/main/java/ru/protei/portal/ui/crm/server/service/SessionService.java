package ru.protei.portal.ui.crm.server.service;

import ru.protei.portal.core.model.ent.UserSessionDescriptor;

import javax.servlet.http.HttpServletRequest;

/**
 * Сервис по работе с сессией
 */
public interface SessionService {
    /**
     * Сохраняет в сессии токен авторизации
     */
    void setUserSessionDescriptor( HttpServletRequest request, UserSessionDescriptor value );

    /**
     * Вытаскивает из сессии токен авторизации
     */
    UserSessionDescriptor getUserSessionDescriptor( HttpServletRequest request );
}
