package ru.protei.portal.test.svn;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class TestSvnUploading {

    private static final Logger log = LoggerFactory.getLogger(TestSvnUploading.class);
    private final static String REPO_URL = "https://svn.riouxsvn.com/portal-autotest";
    private final static String USERNAME = "portal";
    private final static String PASSWORD = "shutdown";
    private final static String FILE_PATH = "scoring.pdf";
    private final static long LATEST_REVISION = -1;

    @Test
    public void testSvnConnection() {
        DAVRepositoryFactory.setup();
        try {
            SVNRepository repository = createSVNRepository();
            String dirName = makeTestDirName();
            SVNCommitInfo commitFileInfo = commitFile(repository, dirName);
            log.info("Upload successfully committed: " + commitFileInfo);
            SVNCommitInfo commitRemoveInfo = removeDir(repository, dirName);
            log.info("Removal successfully committed: " + commitRemoveInfo);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.getMessage());
        }
    }

    private SVNCommitInfo commitFile(SVNRepository repository, String dirName) throws SVNException, FileNotFoundException {

        String filePath = getResourceFilePath(FILE_PATH);

        ISVNEditor editor = repository.getCommitEditor("Test - upload file to dir " + dirName, null);

        editor.openRoot(LATEST_REVISION);
        editor.addDir(dirName, null, LATEST_REVISION);

        editor.addFile(filePath, null, LATEST_REVISION);
        editor.applyTextDelta(filePath, null);
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(filePath, new FileInputStream(filePath), editor, true);
        editor.closeFile(filePath, checksum);

        editor.closeDir();
        editor.closeDir();

        return editor.closeEdit();
    }

    private SVNCommitInfo removeDir(SVNRepository repository, String dirName) throws SVNException {
        ISVNEditor editor = repository.getCommitEditor("Test - remove dir " + dirName, null);
        editor.openRoot(LATEST_REVISION);
        editor.deleteEntry(dirName, LATEST_REVISION);
        editor.closeDir();
        return editor.closeEdit();
    }

    private SVNRepository createSVNRepository() throws SVNException {
        SVNRepository repository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(REPO_URL));
        repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(USERNAME, PASSWORD));
        return repository;
    }

    private String getResourceFilePath(String path) {
        return getClass().getClassLoader().getResource(path).getPath();
    }

    private String makeTestDirName() {
        return String.valueOf(new Date().getTime());
    }
}
