package ru.protei.portal.ui.common.server.service;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.util.CrmConstants;

import javax.servlet.http.HttpServletRequest;

@Component
public class SessionServiceImpl implements SessionService {

    @Override
    public void setUserSessionDescriptor( HttpServletRequest request, UserSessionDescriptor value ) {
        request.getSession().setAttribute( CrmConstants.Auth.SESSION_DESC, value );
    }

    @Override
    public UserSessionDescriptor getUserSessionDescriptor( HttpServletRequest request ) {
        return (UserSessionDescriptor) request.getSession().getAttribute( CrmConstants.Auth.SESSION_DESC );
    }

    @Override
    public void setFileItem(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute( CrmConstants.FileUpload.FILE_ITEM_DESC, fileItem);
    }

    @Override
    public FileItem getFileItem(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute( CrmConstants.FileUpload.FILE_ITEM_DESC);
    }
}


