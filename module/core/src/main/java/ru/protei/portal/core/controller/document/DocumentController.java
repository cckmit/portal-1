package ru.protei.portal.core.controller.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;
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
import java.io.IOException;
import java.io.InputStream;

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

    @RequestMapping(value = "/uploadDocument/{projectId:[0-9]+}/{documentId:[0-9]+}", method = RequestMethod.POST)
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
                return saveInRepository(projectId.toString(), documentId.toString() + ".pdf", item.getInputStream());
            }
        } catch (FileUploadException | IOException | SVNException e) {
            logger.error(e);
        }
        return "error";
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
        return dirName + "/" + fileName;
    }
}
