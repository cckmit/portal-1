package ru.protei.portal.core.svn.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_DocumentFormat;
import ru.protei.portal.core.model.helper.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DocumentSvnApiImpl implements DocumentSvnApi {

    private static final Logger log = LoggerFactory.getLogger(DocumentSvnApiImpl.class);
    private SVNRepository repository;
    private static final long HEAD_REVISION = -1;
    private static final String NO_COPY_FROM_PATH = null;
    private static final long NO_COPY_FROM_REVISION = -1;

    @PostConstruct
    public void init() {
        String repositoryUrl = config.data().svn().getUrl();
        String username = config.data().svn().getUsername();
        String password = config.data().svn().getPassword();
        try {
            DAVRepositoryFactory.setup();
            repository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(repositoryUrl));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
            repository.setAuthenticationManager(authManager);
        } catch (SVNException e) {
            log.error("Failed to init repository (" + repositoryUrl + ") using username=" +
                      username + " and password=" + (StringUtils.isEmpty(password) ? "<no>" : "<yes>"), e);
        }
    }

    @Autowired
    PortalConfig config;

    @Override
    public void saveDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, String commitMessage, InputStream inputStream) throws SVNException {
        String fileName = getFileName(projectId, documentId, documentFormat);

        ISVNEditor editor = repository.getCommitEditor(commitMessage, null);

        try {
            editor.openRoot(HEAD_REVISION);
            try {
                editor.addDir(projectId.toString(), NO_COPY_FROM_PATH, NO_COPY_FROM_REVISION);
            } catch (SVNException e) {
                // ignore if directory already exists
            }
            editor.addFile(fileName, NO_COPY_FROM_PATH, NO_COPY_FROM_REVISION);
            editor.applyTextDelta(fileName, null);

            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta(fileName, inputStream, editor, true);

            editor.closeFile(fileName, checksum);
            editor.closeDir(); // close project directory
            editor.closeDir(); // close root directory
        } catch (SVNException e) {
            editor.abortEdit();
            log.error("saveDocument(p=" + projectId + ",d=" + documentId + "): Failed to commit", e);
            throw e;
        }
        SVNCommitInfo svnCommitInfo = editor.closeEdit();
        log.info("saveDocument(p=" + projectId + ",d=" + documentId + "): Commit info: " + svnCommitInfo);
    }

    @Override
    public void updateDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, String commitMessage, InputStream newDocumentStream) throws SVNException, IOException {
        // see "File modification" at https://wiki.svnkit.com/Committing_To_A_Repository#line-264

        String fileName = getFileName(projectId, documentId, documentFormat);

        final InputStream oldDocumentStream;
        try {
            oldDocumentStream = getDocument(projectId, documentId, documentFormat);
        } catch (SVNException e) {
            log.error("updateDocument(p=" + projectId + ",d=" + documentId + "): Failed to get old document stream");
            throw e;
        } catch (IOException e) {
            log.error("updateDocument(p=" + projectId + ",d=" + documentId + "): Failed to close old document stream");
            throw e;
        }

        ISVNEditor editor = repository.getCommitEditor(commitMessage, null);

        try {
            editor.openRoot(HEAD_REVISION);
            editor.openDir(projectId.toString(), HEAD_REVISION);
            editor.openFile(fileName, HEAD_REVISION);
            editor.applyTextDelta(fileName, null);

            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta(fileName, oldDocumentStream, 0, newDocumentStream, editor, true);

            editor.closeFile(fileName, checksum);
            editor.closeDir(); // close project directory
            editor.closeDir(); // close root directory
        } catch (SVNException e) {
            editor.abortEdit();
            log.error("updateDocument(p=" + projectId + ",d=" + documentId + "): Failed to commit", e);
            throw e;
        }
        SVNCommitInfo svnCommitInfo = editor.closeEdit();
        log.info("updateDocument(p=" + projectId + ",d=" + documentId + "): Commit info: " + svnCommitInfo);
    }

    @Override
    public void getDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, OutputStream outputStream) throws SVNException {
        repository.getFile(getFilePath(projectId, documentId, documentFormat), HEAD_REVISION, new SVNProperties(), outputStream);
    }

    @Override
    public void removeDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, String commitMessage) throws SVNException {
        String fileName = getFileName(projectId, documentId, documentFormat);

        ISVNEditor editor = repository.getCommitEditor(commitMessage, null);

        try {
            editor.openRoot(HEAD_REVISION);
            editor.openDir(projectId.toString(), HEAD_REVISION);
            editor.deleteEntry(fileName, HEAD_REVISION);
            editor.closeDir(); // close project directory
            editor.closeDir(); // close root directory
        } catch (SVNException e) {
            editor.abortEdit();
            log.error("removeDocument(p=" + projectId + ",d=" + documentId + "): Failed to commit", e);
            throw e;
        }
        SVNCommitInfo svnCommitInfo = editor.closeEdit();
        log.info("removeDocument(p=" + projectId + ",d=" + documentId + "): Commit info: " + svnCommitInfo);
    }

    @Override
    public List<String> listDocuments(Long projectId, Long documentId) throws SVNException {
        List<String> result = new ArrayList<>();
        String path = getDirPath(projectId);
        //noinspection rawtypes
        Collection entries = repository.getDir(path, HEAD_REVISION, null, (Collection<SVNDirEntry>) null);
        for (Object o : entries) {
            SVNDirEntry entry = (SVNDirEntry) o;
            if (entry.getKind() == SVNNodeKind.FILE) {
                String filePath = (path.isEmpty() ? "" : path + "/") + entry.getName();
                result.add(filePath);
            }
        }
        return result;
    }

    private InputStream getDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat) throws SVNException, IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            getDocument(projectId, documentId, documentFormat, out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static String getFilePath(Long projectId, Long documentId, En_DocumentFormat documentFormat) {
        return getDirPath(projectId) + "/" + getFileName(projectId, documentId, documentFormat);
    }

    private static String getDirPath(Long projectId) {
        return "/" + projectId;
    }

    private static String getFileName(Long projectId, Long documentId, En_DocumentFormat documentFormat) {
        return documentFormat.getFilename(documentId);
    }
}
