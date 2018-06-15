package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import ru.protei.portal.config.PortalConfig;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;

public class DocumentSvnServiceImpl implements DocumentSvnService {

    private static final Logger log = LoggerFactory.getLogger(DocumentSvnServiceImpl.class);
    private SVNRepository repository;
    private static final long HEAD_REVISION = -1;
    private static final String NO_COPY_FROM_PATH = null;
    private static final long NO_COPY_FROM_REVISION = -1;

    @PostConstruct
    public void init() {
        DAVRepositoryFactory.setup();
        String repositoryUrl = config.data().svn().getUrl();
        try {
            repository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(repositoryUrl));
        } catch (SVNException e) {
            log.error("Failed to init repository " + repositoryUrl, e);
            return;
        }
        repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(config.data().svn().getUsername(), config.data().svn().getPassword()));
    }

    @Autowired
    PortalConfig config;

    @Override
    public void saveDocument(Long projectId, Long documentId, InputStream inputStream) throws SVNException {
        String fileName = getFileName(projectId, documentId);

        ISVNEditor editor = repository.getCommitEditor(getFormattedCommitMessage(projectId, documentId), null);

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
            log.error("Failed to commit: projectId=" + projectId + ", documentId=" + documentId, e);
            throw e;
        }
        SVNCommitInfo svnCommitInfo = editor.closeEdit();
        log.info("Commit info: " + svnCommitInfo);
    }

    @Override
    public void getDocument(Long projectId, Long documentId, OutputStream outputStream) throws SVNException {
        repository.getFile(getFilePath(projectId, documentId), HEAD_REVISION, new SVNProperties(), outputStream);
    }

    private static String getFilePath(Long projectId, Long documentId) {
        return "/" + projectId + "/" + getFileName(projectId, documentId);
    }

    private static String getFileName(Long projectId, Long documentId) {
        return documentId + ".pdf";
    }

    private String getFormattedCommitMessage(Long projectId, Long documentId) {
        String commitMessage = config.data().svn().getCommitMessage();
        return String.format(commitMessage, projectId, documentId);
    }
}
