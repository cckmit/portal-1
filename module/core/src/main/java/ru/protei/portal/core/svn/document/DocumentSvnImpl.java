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
import ru.protei.portal.core.model.helper.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;

public class DocumentSvnImpl implements DocumentSvn {

    private static final Logger log = LoggerFactory.getLogger(DocumentSvnImpl.class);
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
    public void saveDocument(Long projectId, Long documentId, InputStream inputStream) throws SVNException {
        String fileName = getFileName(projectId, documentId);

        ISVNEditor editor = repository.getCommitEditor(getFormattedAddCommitMessage(projectId, documentId), null);

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
    public void updateDocument(Long projectId, Long documentId, InputStream newDocumentStream) throws SVNException, IOException {
        // see "File modification" at https://wiki.svnkit.com/Committing_To_A_Repository#line-264

        String fileName = getFileName(projectId, documentId);

        final InputStream oldDocumentStream;
        try {
            oldDocumentStream = getDocument(projectId, documentId);
        } catch (SVNException e) {
            log.error("updateDocument(p=" + projectId + ",d=" + documentId + "): Failed to get old document stream");
            throw e;
        } catch (IOException e) {
            log.error("updateDocument(p=" + projectId + ",d=" + documentId + "): Failed to close old document stream");
            throw e;
        }

        ISVNEditor editor = repository.getCommitEditor(getFormattedUpdateCommitMessage(projectId, documentId), null);

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
    public void getDocument(Long projectId, Long documentId, OutputStream outputStream) throws SVNException {
        repository.getFile(getFilePath(projectId, documentId), HEAD_REVISION, new SVNProperties(), outputStream);
    }

    @Override
    public void removeDocument(Long projectId, Long documentId) throws SVNException {
        String fileName = getFileName(projectId, documentId);

        ISVNEditor editor = repository.getCommitEditor(getFormattedRemoveCommitMessage(projectId, documentId), null);

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

    private InputStream getDocument(Long projectId, Long documentId) throws SVNException, IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            getDocument(projectId, documentId, out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static String getFilePath(Long projectId, Long documentId) {
        return "/" + projectId + "/" + getFileName(projectId, documentId);
    }

    private static String getFileName(Long projectId, Long documentId) {
        return documentId + ".pdf";
    }

    private String getFormattedAddCommitMessage(Long projectId, Long documentId) {
        String commitMessage = config.data().svn().getCommitMessageAdd();
        return String.format(commitMessage, projectId, documentId);
    }

    private String getFormattedUpdateCommitMessage(Long projectId, Long documentId) {
        String commitMessage = config.data().svn().getCommitMessageUpdate();
        return String.format(commitMessage, projectId, documentId);
    }

    private String getFormattedRemoveCommitMessage(Long projectId, Long documentId) {
        String commitMessage = config.data().svn().getCommitMessageRemove();
        return String.format(commitMessage, projectId, documentId);
    }
}
