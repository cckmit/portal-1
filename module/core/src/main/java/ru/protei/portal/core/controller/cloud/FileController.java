package ru.protei.portal.core.controller.cloud;

/**
 * Created by bondarenko on 26.12.16.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.user.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
public class FileController {

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    CaseService caseService;

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    AuthService authService;


    private ObjectMapper mapper = new ObjectMapper();
    private FileStorage fileStorage = new FileStorage();
    private ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

    private final String ERROR_RESULT = "error";


    @RequestMapping(value = "/issueUploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String issueFileUpload (HttpServletRequest request){
        UserSessionDescriptor ud = authService.getUserSessionDescriptor(request);

        if(ud != null){
            Long creatorId = ud.getPerson().getId();

            try {
                for(FileItem item: upload.parseRequest(request)) {
                    if (!item.isFormField()) {

                        Attachment attachment = saveAttachment(item, creatorId);
                        return mapper.writeValueAsString(attachment);

                    }
                }
            }catch (FileUploadException | SQLException | IOException e){
                e.printStackTrace();
            }
        }
        return ERROR_RESULT;
    }

    @RequestMapping(value = "/uploadFileToCase{caseId:[0-9]+}", method = RequestMethod.POST)
    @ResponseBody
    public String issueFileUpload (HttpServletRequest request, @PathVariable("caseId") Long caseId){
        UserSessionDescriptor ud = authService.getUserSessionDescriptor(request);

        if(ud != null) {
            Long creatorId = ud.getPerson().getId();

            try {
                for (FileItem item : upload.parseRequest(request)) {
                    if (!item.isFormField()) {

                        Attachment attachment = saveAttachment(item, creatorId);
                        bindAttachmentToCase(attachment, caseId);
                        return mapper.writeValueAsString(attachment);

                    }
                }
            } catch (FileUploadException | SQLException | IOException e) {
                e.printStackTrace();
            }
        }

        return ERROR_RESULT;
    }

    @RequestMapping(value = "/files/{folder}/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getFile (HttpServletResponse response,
                           @PathVariable("folder") String folder,
                           @PathVariable("fileName") String fileName){

        FileStorage.File file = fileStorage.getFile(folder +"/"+ fileName);
        if(file == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return new byte[0];
        }

        response.setStatus(HttpStatus.CREATED.value());
        response.setContentType(file.getContentType());

        return file.getData();
    }

    public boolean removeFiles(Collection<Long> ids){
        List<Attachment> attachments = attachmentDAO.partialGetListByKeys(ids, "ext_link");
        for(Attachment attachment: attachments) {
            boolean isDeleted = fileStorage.deleteFile(attachment.getExtLink());
            if (!isDeleted)
                return false;
        }
        return true;
    }

    private String saveFile(FileItem file) throws IOException{
        String fileName = file.getName();
        if (fileName != null) {
            fileName = FilenameUtils.getName(fileName);
        }
        String path = fileStorage.save(fileName, file.get());
        if(path == null)
            throw new IOException("File upload error");
        return path;
    }

    private Long bindAttachmentToCase(Attachment attachment, Long caseId) throws SQLException{
        CaseAttachment caseAttachment = new CaseAttachment();
        caseAttachment.setAttachmentId(attachment.getId());
        caseAttachment.setCaseId(caseId);

        Long caseAttachId = caseAttachmentDAO.persist(caseAttachment);
        if (caseAttachId == null)
            throw new SQLException("insert attachment error");

        boolean result = caseService.updateCaseModified(caseId, new Date()).getData();
        if(!result)
            throw new SQLException("caseObject update error");

        return caseAttachId;
    }


    private Attachment saveAttachment(FileItem item, Long creatorId) throws IOException, SQLException{
        String filePath = saveFile(item);

        Attachment attachment = new Attachment();
        attachment.setCreatorId(creatorId);
        attachment.setFileName(item.getName());
        attachment.setDataSize(item.getSize());
        attachment.setExtLink(filePath);
        attachment.setMimeType(item.getContentType());

        Long attachId = attachmentDAO.saveAttachment(attachment);
        if(attachId == null)
            throw new SQLException("insert attachment error");

        return attachment;
    }


}
