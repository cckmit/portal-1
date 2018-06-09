package ru.protei.portal.core.controller.document;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.service.DocumentSvnService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.util.StringUtils.encodeToRFC2231;

@RestController
public class DocumentController {
    private static final Logger logger = Logger.getLogger(DocumentController.class);
    private final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

    @Autowired
    AuthService authService;

    @Autowired
    PortalConfig config;

    @Autowired
    DocumentStorageIndex documentStorage;

    @Autowired
    DocumentSvnService documentSvnService;

    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    LockService lockService;


    @RequestMapping(value = "/uploadDocument/{projectId:\\d+}/{documentId:\\d+}", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFileToCase(HttpServletRequest request, @PathVariable("projectId") Long projectId, @PathVariable("documentId") Long documentId) {
        if (projectId == null) {
            logger.error("project id is null");
            return "error";
        }
        if (documentId == null) {
            logger.error("document id is null");
            return "error";
        }
        if (authService.getUserSessionDescriptor(request) == null) {
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

        return lockService.doWithLock(DocumentStorageIndex.class, "", LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {
            try {
                documentStorage.addPdfDocument(item.get().getInputStream(), projectId, documentId);
            } catch (IOException e) {
                logger.error("failed to add file to the index", e);
                documentDAO.removeByKey(documentId);
                return "error";
            }
            try {
                documentSvnService.saveDocument(projectId, documentId, item.get().getInputStream());
                return "ok";
            } catch (SVNException | IOException e) {
                logger.error("failed to save in the repository", e);
                try {
                    removeFromIndex(documentId);
                } catch (IOException e1) {
                    logger.error("failed to delete document from the index");
                }
                documentDAO.removeByKey(documentId);
                return "error";
            }
        });
    }

    private void removeFromIndex(long documentId) throws IOException {
        documentStorage.removeDocument(documentId);
    }

    @RequestMapping(value = "/document/{projectId:\\d+}/{documentId:\\d+}", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(HttpServletResponse response,
                        @PathVariable("projectId") Long projectId,
                        @PathVariable("documentId") Long documentId) throws IOException {
        try {
            documentSvnService.getDocument(projectId, documentId, response.getOutputStream());
        } catch (SVNException e) {
            logger.error("Failed to get document from repository: projectId=" + projectId + ", documentId=" + documentId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return;
        }

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/pdf");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", "filename=" + documentId + ".pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodeToRFC2231(documentId + ".pdf"));
    }
}
