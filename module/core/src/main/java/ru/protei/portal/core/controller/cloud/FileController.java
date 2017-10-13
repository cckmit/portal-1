package ru.protei.portal.core.controller.cloud;

/**
 * Created by bondarenko on 26.12.16.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.AttachmentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.user.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@RestController
public class FileController {

    @Autowired
    CaseService caseService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    AuthService authService;

    @Autowired
    FileStorage fileStorage;

//    @Autowired
//    EventPublisherService eventPublisherService;


    private static final Logger logger = Logger.getLogger(FileStorage.class);
    private ObjectMapper mapper = new ObjectMapper();
    private ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());


    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(HttpServletRequest request){
        return uploadFileToCase(request, null);
    }

    @RequestMapping(value = "/uploadFileToCase{caseId:[0-9]+}", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFileToCase (HttpServletRequest request, @PathVariable("caseId") Long caseId){
        UserSessionDescriptor ud = authService.getUserSessionDescriptor(request);

        if(ud != null) {
            try {
                for (FileItem item : upload.parseRequest(request)) {
                    if(item.isFormField())
                        continue;

                    Long creatorId = ud.getPerson().getId();
                    Attachment attachment = saveAttachment(item, creatorId);

                    if(caseId != null) {
                        CoreResponse<Long> caseAttachId = caseService.bindAttachmentToCase(ud.makeAuthToken(), attachment, caseId);
                        if(caseAttachId.isError())
                            break;

//                        eventPublisherService.publishEvent(new CaseAttachmentEvent(this, attachment, caseId));
                    }

                    return mapper.writeValueAsString(attachment);
                }
            } catch (FileUploadException | SQLException | IOException e) {
                logger.error(e);
            }
        }
        return "error";
    }

    @RequestMapping(value = "/files/{folder}/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public void getFile (HttpServletResponse response,
                                   @PathVariable("folder") String folder,
                                   @PathVariable("fileName") String fileName) throws IOException{

        FileStorage.File file = fileStorage.getFile(folder +"/"+ fileName);
        if(file == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(file.getContentType());
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Cache-Control", "max-age=86400, must-revalidate"); // 1 day
        response.setHeader("Content-Disposition", "filename="+ extractRealFileName(fileName));
        IOUtils.copy(file.getData(), response.getOutputStream());
    }

    public Long saveAttachment (Attachment attachment, InputStreamSource content, Long caseId) throws IOException, SQLException {
        if(caseId == null)
            throw new RuntimeException("Case-ID is required");

        attachment.setExtLink(fileStorage.save(generateUniqueFileName(attachment.getFileName()), content.getInputStream()));

        if(attachmentService.saveAttachment(attachment).isError()) {
            fileStorage.deleteFile(attachment.getExtLink());
            throw new SQLException("attachment not saved");
        }

        CoreResponse<Long> caseAttachId = caseService.attachToCase(attachment, caseId);
        if(caseAttachId.isError())
            throw new SQLException("unable to bind attachment to case");

        return caseAttachId.getData();
    }

    private String saveFile(FileItem file) throws IOException{
        return fileStorage.save(generateUniqueFileName(file.getName()), file.getInputStream());
    }

    private Attachment saveAttachment(FileItem item, Long creatorId) throws IOException, SQLException{
        String filePath = saveFile(item);

        Attachment attachment = new Attachment();
        attachment.setCreatorId(creatorId);
        attachment.setFileName(item.getName());
        attachment.setDataSize(item.getSize());
        attachment.setExtLink(filePath);
        attachment.setMimeType(item.getContentType());

        if(attachmentService.saveAttachment(attachment).isError()) {
            fileStorage.deleteFile(filePath);
            throw new SQLException("attachment not saved");
        }

        return attachment;
    }

    private String generateUniqueFileName(String filename){
        return Long.toString(System.currentTimeMillis(), Character.MAX_RADIX) +"_"+ filename;
    }

    private String extractRealFileName(String fileName){
        return fileName.substring(fileName.indexOf('_') + 1);
    }
}
