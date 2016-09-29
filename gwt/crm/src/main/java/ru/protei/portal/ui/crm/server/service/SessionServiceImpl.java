package ru.protei.portal.ui.crm.server.service;

import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.ui.crm.server.util.SystemConstants;

import javax.servlet.http.HttpServletRequest;

@Component
public class SessionServiceImpl implements SessionService {

    @Override
    public void setUserSessionDescriptor( HttpServletRequest request, UserSessionDescriptor value ) {
        request.getSession().setAttribute( SystemConstants.AUTH_SESSION_DESC, value );
    }

    @Override
    public UserSessionDescriptor getUserSessionDescriptor( HttpServletRequest request ) {
        return (UserSessionDescriptor) request.getSession().getAttribute( SystemConstants.AUTH_SESSION_DESC );
    }
}


