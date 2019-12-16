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

    void setFile(HttpServletRequest request, FileItem fileItem);

    void setFilePdf(HttpServletRequest request, FileItem fileItem);

    void setFileDoc(HttpServletRequest request, FileItem fileItem);

    FileItem getFile(HttpServletRequest request);

    FileItem getFilePdf(HttpServletRequest request);

    FileItem getFileDoc(HttpServletRequest request);

    void clearAllFiles(HttpServletRequest request);
}
