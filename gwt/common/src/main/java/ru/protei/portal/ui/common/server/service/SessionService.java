package ru.protei.portal.ui.common.server.service;

import org.apache.commons.fileupload.FileItem;
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

    /**
     * Сохраняет в сессии файл
     */
    void setFileItem(HttpServletRequest request, FileItem fileItem);

    /**
     * Получает из сессии загружаемый файл
     */
    FileItem getFileItem(HttpServletRequest request);

    void clearFileItem(HttpServletRequest request);
}
