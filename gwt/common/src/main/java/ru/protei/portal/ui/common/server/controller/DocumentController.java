package ru.protei.portal.ui.common.server.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_DocumentFormat;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.service.DocumentService;
import ru.protei.portal.core.service.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static ru.protei.portal.util.EncodeUtils.encodeToRFC2231;

@RestController
public class DocumentController {

    @RequestMapping(value = "/upload/document/{format}", method = RequestMethod.POST)
    @ResponseBody
    public String uploadDocument(
            HttpServletRequest request,
            @PathVariable("format") String format
    ) {
        try {
            if (sessionService.getAuthToken(request) == null) {
                log.info("uploadDocument(): user session descriptor not found");
                return RESPONSE_ERROR;
            }

            En_DocumentFormat documentFormat = En_DocumentFormat.of(format);
            if (documentFormat == null) {
                log.info("uploadDocument(): got invalid format {}", format);
                return RESPONSE_ERROR;
            }

            Optional<FileItem> fileItem = fetchFileFromRequest(request);
            if (!fileItem.isPresent()) {
                log.warn("uploadDocument(): file is not present");
                return RESPONSE_ERROR;
            }

            saveFileToSession(request, fileItem.get(), documentFormat);
            return RESPONSE_SUCCESS;

        } catch (Exception e) {
            log.error("uploadDocument(): uncaught exception", e);
            return RESPONSE_ERROR;
        }
    }

    @RequestMapping(value = "/download/document/{projectId:\\d+}/{documentId:\\d+}/{format}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadDocument(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("projectId") Long projectId,
            @PathVariable("documentId") Long documentId,
            @PathVariable("format") String format
    ) {
        try {
            AuthToken token = sessionService.getAuthToken(request);
            if (token == null) {
                log.warn("downloadDocument(): auth token not found");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            En_DocumentFormat documentFormat = En_DocumentFormat.of(format);
            if (documentFormat == null) {
                log.info("downloadDocument(): got invalid format {}", format);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }

            Result<En_DocumentFormat> result = documentService.getDocumentFile(token, documentId, projectId, documentFormat, response.getOutputStream());
            if (result.isError()) {
                log.warn("downloadDocument(): service error result {}", result.getStatus());
                switch (result.getStatus()) {
                    case PERMISSION_DENIED: response.setStatus(HttpStatus.FORBIDDEN.value()); break;
                    case NOT_FOUND: response.setStatus(HttpStatus.NOT_FOUND.value()); break;
                    default: response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); break;
                }
                return;
            }

            En_DocumentFormat actualDocumentFormat = result.getData();
            String mimeType = actualDocumentFormat.getMimeType();
            String documentName = getDocumentName(documentId);
            String fileName = documentName + "." + actualDocumentFormat.getFormat();

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(mimeType);
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodeToRFC2231(fileName));

        } catch (Exception e) {
            log.error("downloadDocument(): uncaught exception", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private Optional<FileItem> fetchFileFromRequest(HttpServletRequest request) {
        try {
            return upload.parseRequest(request)
                .stream()
                .filter(item -> !item.isFormField())
                .findFirst();
        } catch (FileUploadException e) {
            log.error("fetchFile(): failed to parse request", e);
            return Optional.empty();
        }
    }

    private void saveFileToSession(HttpServletRequest request, FileItem fileItem, En_DocumentFormat documentFormat) {
        switch (documentFormat) {
            case PDF:
                sessionService.setFilePdf(request, fileItem);
                break;
            case DOCX:
            case DOC:
                sessionService.setFileDoc(request, fileItem);
                break;
            case AS:
                sessionService.setFileApprovalSheet(request, fileItem);
        }
    }

    private String getDocumentName(Long documentId) {
        Result<String> result = documentService.getDocumentName(documentId);
        if (result.isOk() && StringUtils.isNotBlank(result.getData())) {
            return result.getData();
        } else {
            return String.valueOf(documentId);
        }
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    DocumentService documentService;

    private static final String RESPONSE_SUCCESS = "ok";
    private static final String RESPONSE_ERROR = "error";
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    private final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
}
