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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Base64Facade;
import ru.protei.portal.core.model.struct.FileStream;
import ru.protei.portal.core.service.AttachmentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EventAssemblerService;
import ru.protei.portal.core.service.user.AuthService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collections;

import static ru.protei.portal.util.EncodeUtils.encodeToRFC2231;

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

    @Autowired
    EventAssemblerService publisherService;


    private static final Logger logger = LoggerFactory.getLogger(FileStorage.class);
    private ObjectMapper mapper = new ObjectMapper();
    private ServletFileUpload upload = new ServletFileUpload();

    @PostConstruct
    public void onInit() {
        upload.setFileItemFactory(new DiskFileItemFactory());
        upload.setHeaderEncoding("UTF-8");
    }

    @RequestMapping(
            value = "/uploadFile",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"
    )
    @ResponseBody
    public String uploadFile(HttpServletRequest request, HttpServletResponse response){
        return uploadFileToCase(request, null, null, response);
    }

    @RequestMapping(
            value = "/uploadFileToCase/{caseTypeId:[0-9]+}/{caseNumber:[0-9]+}",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"
    )
    @ResponseBody
    public String uploadFileToCase(
            HttpServletRequest request,
            @PathVariable("caseTypeId") Integer caseTypeId,
            @PathVariable("caseNumber") Long caseNumber,
            HttpServletResponse response
    ) {

        UserSessionDescriptor ud = authService.getUserSessionDescriptor(request);

        if(ud != null) {
            try {

                logger.debug("uploadFileToCase: caseNumber=" + getCaseNumberOrNull(caseNumber));

                for (FileItem item : upload.parseRequest(request)) {
                    if(item.isFormField())
                        continue;

                    logger.debug("uploadFileToCase: caseNumber=" + getCaseNumberOrNull(caseNumber) + " | found file to be uploaded");

                    Person creator = ud.getPerson();
                    Attachment attachment = saveAttachment(item, creator.getId());

                    if(caseNumber != null) {
                        En_CaseType caseType = En_CaseType.find(caseTypeId);
                        CoreResponse<Long> caseAttachId = caseService.bindAttachmentToCaseNumber(ud.makeAuthToken(), caseType, attachment, caseNumber);
                        if(caseAttachId.isError()) {
                            logger.debug("uploadFileToCase: caseNumber=" + caseNumber + " | failed to bind attachment to case | status=" + caseAttachId.getStatus().name());
                            break;
                        }

                        shareNotification(attachment, caseNumber, creator, ud.makeAuthToken());
                    }

                    return mapper.writeValueAsString(attachment);
                }

                logger.debug("uploadFileToCase: caseNumber=" + getCaseNumberOrNull(caseNumber) + " | file to be uploaded not found");

            } catch (FileUploadException | SQLException | IOException e) {
                logger.error("uploadFileToCase", e);
            }
        }
        return "error";
    }

    @PostMapping(value = "/uploadBase64File", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String uploadBase64File(HttpServletRequest request, @RequestBody Base64Facade base64Facade) {
        UserSessionDescriptor ud = authService.getUserSessionDescriptor(request);
        if (ud == null) {
            return "error";
        }
        if (StringUtils.isEmpty(base64Facade.getBase64())) {
            return "error";
        }
        try {
            String[] parts = base64Facade.getBase64().split(",");
            if (parts.length != 2) {
                return "error";
            }
            byte[] bytes = Base64.getDecoder().decode(parts[1]);
            Person creator = ud.getPerson();
            Attachment attachment = saveAttachment(bytes, base64Facade, creator.getId());
            return mapper.writeValueAsString(attachment);
        } catch (IOException | SQLException e) {
            logger.error("uploadBase64File", e);
        }
        return "error";
    }

    @RequestMapping(value = "/files/{folder}/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public void getFile (HttpServletResponse response,
                                   @PathVariable("folder") String folder,
                                   @PathVariable("fileName") String fileName) throws IOException{

        logger.debug("getFile: folder=" + folder + ", fileName=" + fileName);

        FileStorage.File file = fileStorage.getFile(folder +"/"+ fileName);
        if(file == null) {
            logger.debug("getFile: folder=" + folder + ", fileName=" + fileName + " | file is null");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(file.getContentType());
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Cache-Control", "max-age=86400, must-revalidate"); // 1 day
//        response.setHeader("Content-Disposition", "filename=" + extractRealFileName(fileName));
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                encodeToRFC2231(extractRealFileName(fileName)));
        IOUtils.copy(file.getData(), response.getOutputStream());
    }

    public Long saveAttachment(Attachment attachment, InputStreamSource content, long caseId) throws Exception {
        return saveAttachment(attachment, content, attachment.getDataSize(), attachment.getMimeType(), caseId);
    }

    public Long saveAttachment(Attachment attachment, InputStreamSource content, long fileSize, String contentType, Long caseId) throws IOException, SQLException {
        if(caseId == null)
            throw new RuntimeException("Case-ID is required");

        /**
         * судя по всему, у нас раньше не закрывался inputStream
         * добавляю в блок try, для гарантированного закрытия
         */
        try (InputStream contentStream = content.getInputStream()) {
            String filePath = saveFileStream(contentStream, attachment.getFileName(), fileSize, contentType);
            attachment.setExtLink(filePath);
        }

        if(attachmentService.saveAttachment(attachment).isError()) {
            fileStorage.deleteFile(attachment.getExtLink());
            throw new SQLException("attachment not saved");
        }

        CoreResponse<Long> caseAttachId = caseService.attachToCaseId(attachment, caseId);
        if(caseAttachId.isError())
            throw new SQLException("unable to bind attachment to case");

//        try {
//            shareNotification(attachment, caseId, null, person);
//        }catch (NullPointerException e){
//            logger.error("Notification error! "+ e.getMessage());
//        }

        return caseAttachId.getData();
    }

    private void shareNotification(Attachment attachment, Long caseNumber, Person initiator, AuthToken token){
        CoreResponse<CaseObject> issue = caseService.getCaseObject(token, caseNumber);
        if(issue.isError()){
            logger.error("Notification error! Database exception: "+ issue.getStatus().name());
            return;
        }

        publisherService.publishEvent(new CaseAttachmentEvent(
                ServiceModule.GENERAL,
                this,
                issue.getData(),
                Collections.singletonList(attachment),
                null,
                initiator
        ));
    }

    private String saveFileStream(InputStream inputStream, String fileName, long fileSize, String contentType) throws IOException {
        return fileStorage.save(
                generateUniqueFileName(fileName),
                new FileStream(inputStream, fileSize, contentType)
        );
    }

    private Attachment saveAttachment(byte[] bytes, Base64Facade facade, Long creatorId) throws IOException, SQLException{
        String fileName = facade.getName();
        if (StringUtils.isBlank(fileName)) {
            fileName = generateUniqueName();
        }
        return saveAttachment(new ByteArrayInputStream(bytes), creatorId, fileName, facade.getSize(), facade.getType());
    }

    private Attachment saveAttachment(FileItem item, Long creatorId) throws IOException, SQLException{
        String fileName = getFileNameFromFileItem(item);
        return saveAttachment(item.getInputStream(), creatorId, fileName, item.getSize(), item.getContentType());
    }

    private Attachment saveAttachment(InputStream is, Long creatorId, String fileName, Long size, String contentType) throws IOException, SQLException {

        logger.debug("saveAttachment: creatorId=" + creatorId);

        String filePath = saveFile(is, fileName, size, contentType);

        logger.debug("saveAttachment: creatorId=" + creatorId + ", filePath=" + filePath);

        Attachment attachment = new Attachment();
        attachment.setCreatorId(creatorId);
        attachment.setFileName(fileName);
        attachment.setDataSize(size);
        attachment.setExtLink(filePath);
        attachment.setMimeType(contentType);

        if (attachmentService.saveAttachment(attachment).isError()) {
            fileStorage.deleteFile(filePath);
            throw new SQLException("attachment not saved");
        }

        return attachment;
    }

    private String saveFile(InputStream is, String fileName, Long size, String contentType) throws IOException {
        return saveFileStream(is, fileName, size, contentType);
    }

    private String getFileNameFromFileItem(FileItem fileItem) {

        String fileName = fileItem.getName();

        if (StringUtils.isBlank(fileName)) {
            fileName = generateUniqueName();
        }

        return fileName;
    }

    private String generateUniqueFileName(String filename){
        return generateUniqueName() + "_" + filename;
    }

    private String generateUniqueName() {
        return Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
    }

    private String extractRealFileName(String fileName){
        final Base64.Decoder decoder = Base64.getUrlDecoder();
        final int underscorePos = fileName.indexOf('_');
        int dotLastPos = fileName.lastIndexOf('.');
        if (dotLastPos == -1) {
            dotLastPos = fileName.length();
        }
        final String encodedPart = fileName.substring(underscorePos + 1, dotLastPos);
        final String fileExt = fileName.substring(dotLastPos);
        final String val = new String(decoder.decode(encodedPart));
        return val + fileExt;
    }


    private String getCaseNumberOrNull(Long caseNumber) {
        return caseNumber == null ? "null" : caseNumber.toString();
    }
}
