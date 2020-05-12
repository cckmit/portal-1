package ru.protei.portal.ui.common.server.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_FileUploadStatus;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.UploadResult;
import ru.protei.portal.ui.common.client.service.AvatarUtils;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class AvatarController {

    private static final String NOPHOTO_PATH = "./images/nophoto.png";

    private static final Logger logger = LoggerFactory.getLogger( AvatarController.class );

    private ServletFileUpload upload = new ServletFileUpload();

    @Autowired
    private PortalConfig portalConfig;

    @Autowired
    private ServletContext context;

    @Autowired
    private PersonDAO personDAO;


    @PostConstruct
    public void onInit(){
        upload.setFileItemFactory(new DiskFileItemFactory());
        upload.setHeaderEncoding(StandardCharsets.UTF_8.name());
    }

    @RequestMapping( value = "/avatars/{gender}/{fileName:.+}" )
    public void getAvatar(
            @PathVariable String gender,
            @PathVariable String fileName,
            HttpServletResponse response) throws IOException {

        En_Gender g = En_Gender.parse(gender);

        if ( loadFile( portalConfig.data().getEmployee().getAvatarPath() + fileName , response ) ) return;

        loadFile( context.getRealPath( g.equals(En_Gender.MALE) ? AvatarUtils.MALE_AVATAR_URL : g.equals(En_Gender.FEMALE) ? AvatarUtils.FEMALE_AVATAR_URL : AvatarUtils.NOGENDER_AVATAR_URL ), response );
    }

    @RequestMapping( value = "/avatars/{fileName:.+}" )
    public void getAvatar(
            @PathVariable String fileName,
            HttpServletResponse response) throws IOException {

        if ( loadFile( portalConfig.data().getEmployee().getAvatarPath() + fileName , response ) ) return;

        loadFile( context.getRealPath( NOPHOTO_PATH ), response );
    }

    @RequestMapping( value = "/avatars/old/{id}" )
    public void getAvatarByOldId(
            @PathVariable String id,
            HttpServletResponse response) throws IOException {

        if (!id.matches("[0-9]+")) return;

        Person person = personDAO.getEmployeeByOldId(Long.parseLong(id));
        if (person == null) return;

        String newFileName = person.getId() + ".jpg";

        if ( loadFile( portalConfig.data().getEmployee().getAvatarPath() + newFileName , response ) ) return;

        loadFile( context.getRealPath( NOPHOTO_PATH ), response );
    }

    @RequestMapping(
            value = "/avatar-upload/{personId:[0-9]+}",
            method = RequestMethod.POST
    )
    @ResponseBody
    public void setAvatar(
            HttpServletRequest request,
            @PathVariable("personId") Long personId
    )  {
        String fileName = portalConfig.data().getEmployee().getAvatarPath() + personId + ".jpg";

        try {
            for (FileItem item : upload.parseRequest(request)) {
                if (item.isFormField())
                    continue;


                Files.write(Paths.get(fileName), item.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
