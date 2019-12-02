package ru.protei.portal.core.service.session;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.util.CrmConstants;

import javax.servlet.http.HttpServletRequest;

@Component
public class SessionServiceImpl implements SessionService {

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
    public void setFileItem(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute(CrmConstants.Session.FILE_ITEM, fileItem);
    }

    @Override
    public FileItem getFileItem(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute(CrmConstants.Session.FILE_ITEM);
    }

    @Override
    public void clearFileItem(HttpServletRequest request) {
        setFileItem(request, null);
    }
}


