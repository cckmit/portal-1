package ru.protei.portal.svn;

import org.junit.Assert;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.FileInputStream;
import java.util.Date;

public class TestSvnUploading {
    private final String REPO_URL = "https://svn.riouxsvn.com/portal-document";
    private final String USERNAME = "portal";
    private final String PASSWORD = "shutdown";
    private final String FILE_PATH = "scoring.pdf";


    @Test
    public void testSvnConnection() {
        DAVRepositoryFactory.setup();
        try {
            SVNRepository repository = DAVRepositoryFactory.create(SVNURL.parseURIEncoded(REPO_URL));
            repository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager(USERNAME, PASSWORD));
            ISVNEditor editor = repository.getCommitEditor("test 1", null);

            String filePath = getResourceFilePath(FILE_PATH);

            editor.openRoot(-1);
            editor.addDir("" + new Date().getTime(), null, -1);
            editor.addFile(filePath, null, -1);
            editor.applyTextDelta(filePath, null);

            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta(filePath, new FileInputStream(filePath), editor, true);

            editor.closeFile(filePath, checksum);
            editor.closeDir();
            editor.closeDir();

            SVNCommitInfo info = editor.closeEdit();
            System.out.println("Successfully committed: " + info);

        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.getMessage());
        }

    }


    private String getResourceFilePath(String path) {
        return getClass().getClassLoader().getResource(path).getPath();
    }
}
