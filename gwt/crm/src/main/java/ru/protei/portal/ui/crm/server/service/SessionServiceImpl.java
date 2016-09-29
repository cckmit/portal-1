package ru.protei.portal.ui.crm.server.service;

import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;

import javax.servlet.http.HttpServletRequest;

@Component
public class SessionServiceImpl implements SessionService {
    @Override
    public void setUserSessionDescriptor( HttpServletRequest request, UserSessionDescriptor value ) {
        request.getSession().setAttribute( "userSessionDescriptor", value );
    }

    @Override
    public UserSessionDescriptor getUserSessionDescriptor( HttpServletRequest request ) {
        return (UserSessionDescriptor) request.getSession().getAttribute( "userSessionDescriptor" );
    }
}


