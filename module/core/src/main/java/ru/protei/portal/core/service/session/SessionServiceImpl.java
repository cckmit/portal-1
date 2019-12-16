package ru.protei.portal.core.service.session;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.util.CrmConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class SessionServiceImpl implements SessionService {

    @Override
    public void setSessionLifetime(HttpServletRequest request, long lifetimeSec) {
        request.getSession().setMaxInactiveInterval((int) lifetimeSec);
    }

    @Override
    public void setAuthToken(HttpServletRequest request, AuthToken authToken) {
        request.getSession().setAttribute(CrmConstants.Session.AUTH_TOKEN, authToken);
    }

    @Override
    public AuthToken getAuthToken(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(CrmConstants.Session.AUTH_TOKEN);
        if (!(attribute instanceof AuthToken)) return null;
        return (AuthToken) attribute;
    }

    @Override
    public void setFile(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute(CrmConstants.Session.FILE_ITEM, fileItem);
    }

    @Override
    public void setFilePdf(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute(CrmConstants.Session.FILE_ITEM_PDF, fileItem);
    }

    @Override
    public void setFileDoc(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute(CrmConstants.Session.FILE_ITEM_DOC, fileItem);
    }

    @Override
    public FileItem getFile(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute(CrmConstants.Session.FILE_ITEM);
    }

    @Override
    public FileItem getFilePdf(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute(CrmConstants.Session.FILE_ITEM_PDF);
    }

    @Override
    public FileItem getFileDoc(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute(CrmConstants.Session.FILE_ITEM_DOC);
    }

    @Override
    public void clearAllFiles(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(CrmConstants.Session.FILE_ITEM, null);
        session.setAttribute(CrmConstants.Session.FILE_ITEM_PDF, null);
        session.setAttribute(CrmConstants.Session.FILE_ITEM_DOC, null);
    }
}
