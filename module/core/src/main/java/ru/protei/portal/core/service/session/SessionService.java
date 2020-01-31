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


    void setFile(HttpServletRequest request, FileItem fileItem);

    void setFilePdf(HttpServletRequest request, FileItem fileItem);

    void setFileDoc(HttpServletRequest request, FileItem fileItem);

    void setFileApprovalSheet(HttpServletRequest request, FileItem fileItem);

    FileItem getFile(HttpServletRequest request);

    FileItem getFilePdf(HttpServletRequest request);

    FileItem getFileDoc(HttpServletRequest request);

    FileItem getFileApprovalSheet(HttpServletRequest request);

    void clearAllFiles(HttpServletRequest request);
}
