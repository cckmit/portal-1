package ru.protei.portal.ui.common.server.service;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.util.CrmConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    public void setFile(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute(CrmConstants.FileUpload.FILE_ITEM, fileItem);
    }

    @Override
    public void setFilePdf(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute(CrmConstants.FileUpload.FILE_ITEM_PDF, fileItem);
    }

    @Override
    public void setFileDoc(HttpServletRequest request, FileItem fileItem) {
        request.getSession().setAttribute(CrmConstants.FileUpload.FILE_ITEM_DOC, fileItem);
    }

    @Override
    public FileItem getFile(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute(CrmConstants.FileUpload.FILE_ITEM);
    }

    @Override
    public FileItem getFilePdf(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute(CrmConstants.FileUpload.FILE_ITEM_PDF);
    }

    @Override
    public FileItem getFileDoc(HttpServletRequest request) {
        return (FileItem) request.getSession().getAttribute(CrmConstants.FileUpload.FILE_ITEM_DOC);
    }

    @Override
    public void clearAllFiles(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(CrmConstants.FileUpload.FILE_ITEM, null);
        session.setAttribute(CrmConstants.FileUpload.FILE_ITEM_PDF, null);
        session.setAttribute(CrmConstants.FileUpload.FILE_ITEM_DOC, null);
    }
}


