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
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.service.DocumentService;
import ru.protei.portal.core.svn.document.DocumentSvn;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.ui.common.server.service.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.protei.portal.util.EncodeUtils.encodeToRFC2231;

@RestController
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

    @Autowired
    AuthService authService;
    @Autowired
    DocumentSvn documentSvn;
    @Autowired
    SessionService sessionService;
    @Autowired
    DocumentService documentService;


    @RequestMapping(value = "/uploadDocument", method = RequestMethod.POST)
    @ResponseBody
    public String uploadDocument(HttpServletRequest request) {
        logger.debug("upload document");
        if (authService.getUserSessionDescriptor(request) == null) {
            logger.error("user session descriptor not found");
            return "error";
        }

        List<FileItem> fileItems;
        try {
            fileItems = upload.parseRequest(request);
        } catch (FileUploadException e) {
            logger.error("failed to parse request", e);
            return "error";
        }
        Optional<FileItem> item = fileItems.stream().filter(i -> !i.isFormField()).findAny();
        if (!item.isPresent()) {
            logger.error("no file items in request");
            return "error";
        }

        sessionService.setFileItem(request, item.get());
        return "ok";
    }

    @RequestMapping(value = "/document/{projectId:\\d+}/{documentId:\\d+}", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(HttpServletResponse response,
                        @PathVariable("projectId") Long projectId,
                        @PathVariable("documentId") Long documentId) throws IOException {
        try {
            documentSvn.getDocument(projectId, documentId, response.getOutputStream());
        } catch (SVNException e) {
            if (e.getErrorMessage() != null && e.getErrorMessage().getErrorCode() == SVNErrorCode.FS_NOT_FOUND) {
                logger.info("getFile(): Document not found (projectId = " + projectId + ", documentId = " + documentId + ")", e);
                response.setStatus(HttpStatus.NOT_FOUND.value());
            } else {
                logger.error("getFile(): Failed to get document (projectId = " + projectId + ", documentId = " + documentId + "), " +
                        "error code = " + (e.getErrorMessage() != null ? e.getErrorMessage().getErrorCode() : "null"), e);
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return;
        }

        String documentName = getDocumentName(documentId);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/pdf");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodeToRFC2231(documentName + ".pdf"));
    }

    private String getDocumentName(Long documentId) {
        Result<String> result = documentService.getDocumentName(documentId);
        if (result.isOk() && StringUtils.isNotBlank(result.getData())) {
            return result.getData();
        } else {
            return String.valueOf(documentId);
        }
    }
}
