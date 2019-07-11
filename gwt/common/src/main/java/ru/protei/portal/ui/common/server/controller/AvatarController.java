package ru.protei.portal.ui.common.server.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.winter.core.utils.mime.MimeUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class AvatarController {

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    PolicyService policyService;

    @Autowired
    ServletContext context;

    @Autowired
    PersonDAO personDAO;

    private static final String NOPHOTO_PATH = "/images/nophoto.png";
    private static final String MALE_PATH = "/images/user-icon-m.svg";
    private static final String FEMALE_PATH = "/images/user-icon-f.svg";

    private static final Logger logger = LoggerFactory.getLogger( AvatarController.class );

    @RequestMapping( value = "/avatars/{fileName:.+}" )
    public void getAvatar( @PathVariable String fileName,
                           HttpServletRequest request,
                           HttpServletResponse response ) throws IOException {

        if ( loadFile( portalConfig.data().getEmployee().getAvatarPath() + fileName , response ) ) return;

        Person person = null;
        try {
            Long id = Long.valueOf( fileName.substring( 0, fileName.indexOf( "." ) ) );
            person = personDAO.get( id );
        } catch ( Exception e ) {
            logger.debug( "Person {} not found" );
        }

        if ( person == null || person.getGender().equals( En_Gender.UNDEFINED ) ) {
            loadFile( context.getRealPath( NOPHOTO_PATH ), response );
            return;
        }

        loadFile( context.getRealPath( person.getGender().equals( En_Gender.MALE ) ? MALE_PATH : FEMALE_PATH ), response );
    }

    private UserSessionDescriptor getDescriptor( HttpServletRequest request ) {
        return  (UserSessionDescriptor) request.getSession().getAttribute( CrmConstants.Auth.SESSION_DESC );
    }

    private boolean loadFile( String pathname, HttpServletResponse response ) throws IOException {

        File file = new File( pathname );

        if ( file.exists() ) {

            try ( InputStream is = new FileInputStream( file ) ) {

                response.setContentLength( new Long( file.length() ).intValue() );
                response.setContentType( MimeUtils.getContentType( file.getName() ) );
                IOUtils.copy( is, response.getOutputStream() );

                response.setHeader( "Cache-control", "" );
                response.setHeader( "Pragma", "" );

                response.flushBuffer();

                return true;
            }
        }
        return false;
    }
}
