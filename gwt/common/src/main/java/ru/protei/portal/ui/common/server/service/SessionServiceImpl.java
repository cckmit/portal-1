package ru.protei.portal.ui.common.server.service;

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
}


