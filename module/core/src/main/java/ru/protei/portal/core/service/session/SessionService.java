package ru.protei.portal.core.service.session;

import org.apache.commons.fileupload.FileItem;
import ru.protei.portal.core.model.ent.AuthToken;

import javax.servlet.http.HttpServletRequest;

/**
 * Сервис по работе с сессией
 */
public interface SessionService {

    void setSessionLifetime(HttpServletRequest request, long lifetimeSec);


    void setAuthToken(HttpServletRequest request, AuthToken authToken);

    AuthToken getAuthToken(HttpServletRequest request);


    void setFileItem(HttpServletRequest request, FileItem fileItem);

    FileItem getFileItem(HttpServletRequest request);

    void clearFileItem(HttpServletRequest request);
}
