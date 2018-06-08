package ru.protei.portal.core.controller.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.core.model.helper.HelperFunc.encodeToRFC2231;

@RestController
public class DocumentController {
    private static final Logger logger = Logger.getLogger(DocumentController.class);
    private final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    CaseService caseService;

    @Autowired
    AuthService authService;

    @Autowired
    PortalConfig config;

    @Autowired
    DocumentIndex documentStorage;

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

        return lockService.doWithLock(DocumentIndex.class, "", LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {
            try {
                addToIndex(projectId, documentId, item.get().getInputStream());
            } catch (IOException e) {
                logger.error("failed to add file to the index", e);
                documentDAO.removeByKey(documentId);
                return "error";
            }
            try {
                return saveInRepository(projectId.toString(), getFileName(projectId, documentId), item.get().getInputStream());
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
            writeDocument(projectId, documentId, response.getOutputStream());
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

    private String saveInRepository(String dirName, String fileName, InputStream fileData) throws SVNException {
        DAVRepositoryFactory.setup();
        SVNRepository repository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(config.data().svn().getUrl()));
        repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(config.data().svn().getUsername(), config.data().svn().getPassword()));
        ISVNEditor editor = repository.getCommitEditor("Add document " + fileName + " to " + dirName, null);

        editor.openRoot(-1);
        try {
            editor.addDir(dirName, null, -1);
        } catch (SVNException e) {
            // ignore if directory already exists
        }
        editor.addFile(fileName, null, -1);
        editor.applyTextDelta(fileName, null);

        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(fileName, fileData, editor, true);

        editor.closeFile(fileName, checksum);
        editor.closeDir();
        editor.closeDir();
        editor.closeEdit();
        repository.closeSession();
        return dirName + "/" + fileName;
    }

    private void writeDocument(Long projectId, Long documentId, OutputStream outputStream) throws SVNException {
        DAVRepositoryFactory.setup();
        SVNRepository repository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(config.data().svn().getUrl()));
        repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(config.data().svn().getUsername(), config.data().svn().getPassword()));
        repository.getFile(getFilePath(projectId, documentId), -1, new SVNProperties(), outputStream);
        repository.closeSession();
    }

    private void addToIndex(Long projectId, Long documentId, InputStream stream) throws IOException {
        try (PDDocument doc = PDDocument.load(stream)) {
            String content = new PDFTextStripper().getText(doc);
            documentStorage.addDocument(content, documentId, projectId);
        }
    }

    private static String getFilePath(Long projectId, Long documentId) {
        return "/" + projectId + "/" + getFileName(projectId, documentId);
    }

    private static String getFileName(Long projectId, Long documentId) {
        return documentId + ".pdf";
    }
}
