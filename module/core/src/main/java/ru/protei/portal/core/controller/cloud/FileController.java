package ru.protei.portal.core.controller.cloud;

/**
 * Created by bondarenko on 26.12.16.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_FileUploadStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Base64Facade;
import ru.protei.portal.core.model.struct.FileStream;
import ru.protei.portal.core.model.struct.UploadResult;
import ru.protei.portal.core.model.util.JsonUtils;
import ru.protei.portal.core.service.AttachmentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventAssemblerService;
import ru.protei.portal.core.service.session.SessionService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
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
    @Autowired
    PortalConfig config;
    @Autowired
    AttachmentDAO attachmentDAO;
    @Autowired
    SessionService sessionService;


    private static final Logger logger = LoggerFactory.getLogger(FileStorage.class);
    private ObjectMapper mapper = new ObjectMapper();
    private ServletFileUpload upload = new ServletFileUpload();

    private static final int BYTES_IN_MEGABYTE = 1048576;

    @PostConstruct
    public void onInit() {
        upload.setFileItemFactory(new DiskFileItemFactory());
        upload.setHeaderEncoding(StandardCharsets.UTF_8.name());
    }

    @RequestMapping(
            value = "/uploadFile",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"
    )
    @ResponseBody
    public String uploadFile(HttpServletRequest request, HttpServletResponse response) {
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

        AuthToken authToken = sessionService.getAuthToken(request);
        if (authToken == null) {
            return uploadResultSerialize(new UploadResult(En_FileUploadStatus.SERVER_ERROR, "AuthToken is null"));
        }

        List<Attachment> bindAttachments = new ArrayList(  );
        UploadResult result = null;

        try {
            logger.debug("uploadFileToCase: caseNumber={}", caseNumber);

            for (FileItem item : upload.parseRequest(request)) {
                if (item.isFormField())
                    continue;

                if (isFileSizeExceed(item.getSize(), config.data().getMaxFileSize())) {
                    logger.debug("uploadFileToCase: caseNumber={} | file is too big", caseNumber);
                    result = new UploadResult(En_FileUploadStatus.SIZE_EXCEED_ERROR, String.valueOf(config.data().getMaxFileSize()));
                    break;
                }

                logger.debug("uploadFileToCase: caseNumber={} | found file to be uploaded", caseNumber);

                Attachment attachment = saveAttachment(item, authToken.getPersonId());

                if (caseNumber != null) {
                    En_CaseType caseType = En_CaseType.find(caseTypeId);
                    Result<Long> caseAttachId = caseService.bindAttachmentToCaseNumber(authToken, caseType, attachment, caseNumber);
                    if (caseAttachId.isError()) {
                        logger.debug("uploadFileToCase: caseNumber=" + caseNumber + " | failed to bind attachment to case | status=" + caseAttachId.getStatus().name());
                        result = new UploadResult(En_FileUploadStatus.SERVER_ERROR, "caseAttachId is Error");
                        break;
                    }
                    bindAttachments.add( attachment );
                }
                result = new UploadResult(En_FileUploadStatus.OK, mapper.writeValueAsString(attachment));
                break;
            }
            logger.debug("uploadFileToCase: caseNumber={} | file to be uploaded not found", caseNumber);

        }
        catch (FileUploadException | SQLException | IOException e) {
            logger.error("uploadFileToCase", e);
            result = new UploadResult(En_FileUploadStatus.SERVER_ERROR, "exception caught");
        }

        if (!isEmpty( bindAttachments )) {
            En_CaseType caseType = En_CaseType.find(caseTypeId);
            caseService.getCaseId( authToken, caseNumber, caseType ).ifOk(caseId->
                shareNotification(caseId, caseType, authToken.getPersonId(), bindAttachments )  );
        }

        if (result == null) result = new UploadResult(En_FileUploadStatus.SERVER_ERROR, "UploadResult is null");

        return uploadResultSerialize(result);
    }

    @PostMapping(value = "/uploadBase64File", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String uploadBase64File(HttpServletRequest request, @RequestBody Base64Facade base64Facade) {

        AuthToken authToken = sessionService.getAuthToken(request);
        UploadResult result = checkInputParams (authToken, base64Facade);

        try {
            if (result == null) {
                String[] parts = base64Facade.getBase64().split(",");
                byte[] bytes = Base64.getDecoder().decode(parts[1]);
                Attachment attachment = saveAttachment(bytes, base64Facade, authToken.getPersonId());
                result = new UploadResult(En_FileUploadStatus.OK, mapper.writeValueAsString(attachment));
            }
        } catch (IOException | SQLException e) {
            logger.error("uploadBase64File", e);
            result = new UploadResult(En_FileUploadStatus.SERVER_ERROR, "exception caught");
        }
        return uploadResultSerialize(result);
    }

    @PostMapping(value = "/uploadBase64Files", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String uploadBase64Files(HttpServletRequest request, @RequestBody List<Base64Facade> base64Facades) {
        List<String> attachmentsJsons = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();

        AuthToken authToken = sessionService.getAuthToken(request);
        if (authToken == null) {
            return uploadResultSerialize(new UploadResult(En_FileUploadStatus.SERVER_ERROR, "auth error"));
        }

        for (Base64Facade currB64facade : base64Facades) {
            UploadResult result = checkInputParams(authToken, currB64facade);

            if (result != null) {
                removeFiles(attachments);
                return uploadResultSerialize(result);
            }

            String[] parts = currB64facade.getBase64().split(",");
            byte[] bytes = Base64.getDecoder().decode(parts[1]);

            try {
                Attachment attachment = saveAttachment(bytes, currB64facade, authToken.getPersonId());
                attachmentsJsons.add(mapper.writeValueAsString(attachment));
                attachments.add(attachment);
            } catch (IOException | SQLException e) {
                logger.error("uploadBase64Files", e);
                return uploadResultSerialize(new UploadResult(En_FileUploadStatus.SERVER_ERROR, "exception caught"));
            }
        }

        return uploadResultSerialize(new UploadResult(En_FileUploadStatus.OK, JsonUtils.wrapJsonsToJsonList(attachmentsJsons)));
    }

    @RequestMapping(value = "/files/{folder}/{fileName:.+}", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(HttpServletResponse response,
                        @PathVariable("folder") String folder,
                        @PathVariable("fileName") String fileName) throws IOException {

        logger.debug("getFile: folder=" + folder + ", fileName=" + fileName);

        String filePath = folder + "/" + fileName;
        FileStorage.File file = fileStorage.getFile(filePath);
        if (file == null) {
            logger.debug("getFile: folder=" + folder + ", fileName=" + fileName + " | file is null");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(file.getContentType());
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Cache-Control", "max-age=86400, must-revalidate"); // 1 day
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                encodeToRFC2231(getRealFileName(filePath, fileName)));
        IOUtils.copy(file.getData(), response.getOutputStream());
    }

    public Long saveAttachment(Attachment attachment, InputStreamSource content, long fileSize, String contentType, CaseObject caseObject) throws IOException, SQLException {
        if (caseObject == null || caseObject.getId() == null)
            throw new RuntimeException("Case-ID is required");

        if (attachmentService.saveAttachment(attachment).isError()) {
            throw new SQLException("attachment not saved");
        }

        /**
         * судя по всему, у нас раньше не закрывался inputStream
         * добавляю в блок try, для гарантированного закрытия
         */
        try (InputStream contentStream = content.getInputStream()) {
            String filePath = saveFileStream(
                    contentStream,
                    generateCloudFileName(attachment.getId(), attachment.getFileName()),
                    fileSize, contentType);
            attachment.setExtLink(filePath);
            attachment.setLabelText(attachment.getFileName());
        }

        if (attachmentService.saveAttachment(attachment).isError()) {
            fileStorage.deleteFile(attachment.getExtLink());
            throw new SQLException("unable to save link to file");
        }

        Result<Long> caseAttachId = caseService.attachToCaseId(attachment, caseObject.getId(), caseObject.isPrivateCase());
        if (caseAttachId.isError())
            throw new SQLException("unable to bind attachment to case");

        return caseAttachId.getData();
    }

    private void removeFiles(List<Attachment> attachments) {
        for (Attachment currAttachment : attachments) {
            fileStorage.deleteFile(currAttachment.getExtLink());
        }
    }

    private void shareNotification(Long caseId, En_CaseType caseType, Long initiatorId, List<Attachment> addedAttachments) {
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            publisherService.onCaseAttachmentEvent( new CaseAttachmentEvent(this, ServiceModule.GENERAL, initiatorId, caseId,
                    addedAttachments, null ));
        }
    }

    private String saveFileStream(InputStream inputStream, String fileName, long fileSize, String contentType) throws IOException {
        return fileStorage.save(
                fileName,
                new FileStream(inputStream, fileSize, contentType)
        );
    }

    private Attachment saveAttachment(byte[] bytes, Base64Facade facade, Long creatorId) throws IOException, SQLException {
        String fileName = facade.getName();
        if (StringUtils.isBlank(fileName)) {
            fileName = generateUniqueName();
        }
        return saveAttachment(new ByteArrayInputStream(bytes), creatorId, fileName, facade.getSize(), facade.getType());
    }

    private Attachment saveAttachment(FileItem item, Long creatorId) throws IOException, SQLException {
        String fileName = getFileNameFromFileItem(item);
        return saveAttachment(item.getInputStream(), creatorId, fileName, item.getSize(), item.getContentType());
    }

    private Attachment saveAttachment(InputStream is, Long creatorId, String fileName, Long size, String contentType) throws IOException, SQLException {

        logger.debug("saveAttachment: creatorId=" + creatorId);

        Attachment attachment = new Attachment();
        attachment.setCreatorId(creatorId);
        attachment.setFileName(fileName);
        attachment.setLabelText(fileName);
        attachment.setDataSize(size);
        attachment.setMimeType(contentType);

        if (attachmentService.saveAttachment(attachment).isError()) {
            throw new SQLException("attachment not saved");
        }

        String filePath = saveFile(is, generateCloudFileName(attachment.getId(), attachment.getFileName()), size, contentType);
        attachment.setExtLink(filePath);

        logger.debug("saveAttachment: creatorId=" + creatorId + ", filePath=" + filePath);

        if (attachmentService.saveAttachment(attachment).isError()) {
            fileStorage.deleteFile(filePath);
            throw new SQLException("unable to save link to file");
        }

        return attachment;
    }

    private String saveFile(InputStream is, String fileName, Long size, String contentType) throws IOException {
        return saveFileStream(is, fileName, size, contentType);
    }

    private String getFileNameFromFileItem(FileItem fileItem) {

        String fileName = fileItem.getName();

        fileName = convertStringCharset(fileName, StandardCharsets.UTF_8, StandardCharsets.UTF_8);

        if (StringUtils.isBlank(fileName)) {
            fileName = generateUniqueName();
        }

        return fileName;
    }

    private String generateCloudFileName(Long id, String filename) {
        int i = filename.lastIndexOf(".");
        if (i <= 0)
            return String.valueOf(id);
        return id + filename.substring(i);
    }

    private String generateUniqueName() {
        return Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
    }

    private String getRealFileName(String filePath, String encodedFileName) {
        Result<Attachment> result = attachmentService.getAttachmentByExtLink(filePath);
        if (result.isOk() && result.getData() != null) {
            return result.getData().getFileName();
        }
        return extractRealFileName(encodedFileName);
    }

    private String extractRealFileName(String fileName) {
        final Base64.Decoder decoder = Base64.getUrlDecoder();
        final int underscorePos = fileName.indexOf('_');
        int dotLastPos = fileName.lastIndexOf('.');
        if (dotLastPos == -1) {
            dotLastPos = fileName.length();
        }
        final String encodedPart = fileName.substring(underscorePos + 1, dotLastPos);
        final String fileExt = fileName.substring(dotLastPos);
        final String val = new String(decoder.decode(encodedPart.getBytes(StandardCharsets.UTF_8)));
        return val + fileExt;
    }

    private String convertStringCharset(String input, Charset inputCharset, Charset outputCharset) {
        byte[] bytes;
        bytes = input.getBytes(inputCharset);
        Charset detectedInputCharset = tryDetectCharset(bytes);
        if (detectedInputCharset != null && !Objects.equals(detectedInputCharset, inputCharset)) {
            bytes = input.getBytes(detectedInputCharset);
        }
        return new String(bytes, outputCharset);
    }

    private Charset tryDetectCharset(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String detectedCharset = detector.getDetectedCharset();
        detector.reset();
        if (detectedCharset == null) {
            return null;
        }
        try {
            return Charset.forName(detectedCharset);
        } catch (UnsupportedCharsetException e) {
            return null;
        }
    }

    private String uploadResultSerialize (UploadResult result){
        try {
            if (result == null)
                return mapper.writeValueAsString(new UploadResult(En_FileUploadStatus.SERVER_ERROR, "Serialize error"));
            return mapper.writeValueAsString(result);
        }
        catch (JsonProcessingException e){
            logger.error("uploadResultSerialize( " + result + " ) ", e);
            return null;
            }
        }


    private boolean isFileSizeExceed(Long fileSize, Long maxSize){
        return fileSize > maxSize * BYTES_IN_MEGABYTE;
    }

    private UploadResult checkInputParams (AuthToken authToken, Base64Facade base64Facade){
        UploadResult result = null;

        if (authToken == null) {
            result = new UploadResult(En_FileUploadStatus.SERVER_ERROR, "AuthToken is null");
        }

        else if (isFileSizeExceed(base64Facade.getSize(), config.data().getMaxFileSize())){
            result = new UploadResult(En_FileUploadStatus.SIZE_EXCEED_ERROR, String.valueOf(config.data().getMaxFileSize()));
        }

        else if (StringUtils.isEmpty(base64Facade.getBase64())) {
            result = new UploadResult(En_FileUploadStatus.SERVER_ERROR, "base64Facade is null");
        }

        else {
            String[] parts = base64Facade.getBase64().split(",");
            if (parts.length != 2) {
                result = new UploadResult(En_FileUploadStatus.SERVER_ERROR, "string array after split != 2");
            }
        }
        return result;
    }
}
