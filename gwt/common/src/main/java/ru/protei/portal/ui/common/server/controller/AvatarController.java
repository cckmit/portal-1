package ru.protei.portal.ui.common.server.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.ui.common.client.service.AvatarUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
public class AvatarController {

    private static final String NOPHOTO_PATH = "./images/nophoto.png";

    private static final Logger logger = LoggerFactory.getLogger( AvatarController.class );

    @Autowired
    private PortalConfig portalConfig;

    @Autowired
    private ServletContext context;


    @RequestMapping( value = "/avatars/{gender}/{fileName:.+}" )
    public void getAvatar(
            @PathVariable String gender,
            @PathVariable String fileName,
            HttpServletResponse response ) throws IOException {

        En_Gender g = En_Gender.parse(gender);

        if ( loadFile( portalConfig.data().getEmployee().getAvatarPath() + fileName , response ) ) return;

        loadFile( context.getRealPath( g.equals(En_Gender.MALE) ? AvatarUtils.MALE_AVATAR_URL : g.equals(En_Gender.FEMALE) ? AvatarUtils.FEMALE_AVATAR_URL : AvatarUtils.NOGENDER_AVATAR_URL ), response );
    }

    @RequestMapping( value = "/avatars/{fileName:.+}" )
    public void getAvatar(@PathVariable String fileName,
                          HttpServletResponse response ) throws IOException {

        if ( loadFile( portalConfig.data().getEmployee().getAvatarPath() + fileName , response ) ) return;

        loadFile( context.getRealPath( NOPHOTO_PATH ), response );
    }

    private boolean loadFile( String pathname, HttpServletResponse response ) throws IOException {

        File file = new File( pathname );

        if ( file.exists() ) {

            try ( InputStream is = new FileInputStream( file ) ) {

                response.setContentLength( new Long( file.length() ).intValue() );
                response.setContentType( Files.probeContentType( file.toPath() ) );
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
