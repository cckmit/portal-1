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
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.user.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

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
    DocumentStorage documentStorage;


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

        UserSessionDescriptor ud = authService.getUserSessionDescriptor(request);

        if (ud == null) {
            return "error";
        }
        try {
            for (FileItem item : upload.parseRequest(request)) {
                if (item.isFormField())
                    continue;

                addToIndex(projectId, documentId, item.getInputStream());
                return saveInRepository(projectId.toString(), getFileName(projectId, documentId), item.getInputStream());
            }
        } catch (FileUploadException | IOException | SVNException e) {
            logger.error(e);
        }
        return "error";
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
        response.setHeader("Cache-Control", "max-age=86400, must-revalidate"); // 1 day
        response.setHeader("Content-Disposition", "filename=" + documentId + ".pdf");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                encodeToRFC2231(documentId + ".pdf"));
    }

    public String encodeToRFC2231(String value) {
        StringBuilder buf = new StringBuilder();
        byte[] bytes;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // cannot happen with UTF-8
            bytes = new byte[]{'?'};
        }
        for (byte b : bytes) {
            if (b < '+' || b == ';' || b == ',' || b == '\\' || b > 'z') {
                buf.append('%');
                String s = Integer.toHexString(b & 0xff).toUpperCase();
                if (s.length() < 2) {
                    buf.append('0');
                }
                buf.append(s);
            } else {
                buf.append((char) b);
            }
        }
        return buf.toString();
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
