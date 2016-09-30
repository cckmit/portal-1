package ru.protei.portal.ui.product.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.ui.product.client.service.ProductService;

import javax.servlet.http.HttpServletRequest;

/**
 * Сервис авторизации
 */
@Service( "ProductService" )
public class ProductServiceImpl extends RemoteServiceServlet implements ProductService {

    /*
    @Override
    public Profile authentificate( String login, String password ) throws RequestFailedException {
        if ( login == null && password == null ) {
            log.debug( "authentificate: empty auth params" );

            UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpRequest );
            log.debug( "authentificate: sessionDescriptior={}", descriptor );

            return descriptor == null ? null : makeProfileByDescriptor( descriptor );
        }

        log.debug("authentificate: login={}", login);

        AuthResult result = authService.login( httpRequest.getSession().getId(), login, password, httpRequest.getRemoteAddr(), httpRequest.getHeader( SystemConstants.USER_AGENT_HEADER ) );
        if ( !result.isOk() ) {
            throw new RequestFailedException( result.getResult().name() );
        }

        return makeProfileByDescriptor( result.getDescriptor() );
    }

*/
    @Autowired
    HttpServletRequest httpRequest;

//    @Autowired
//    SessionService sessionService;

    @Autowired
    private ru.protei.portal.core.service.dict.ProductService productService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}