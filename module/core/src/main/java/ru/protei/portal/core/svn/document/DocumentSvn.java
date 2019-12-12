package ru.protei.portal.core.svn.document;

import org.tmatesoft.svn.core.SVNException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DocumentSvn {

    void saveDocument(Long projectId, Long documentId, InputStream inputStream) throws SVNException;

    void updateDocument(Long projectId, Long documentId, InputStream inputStream) throws SVNException, IOException;

    void getDocument(Long projectId, Long documentId, OutputStream outputStream) throws SVNException;

    void removeDocument(Long projectId, Long documentId) throws SVNException;
}
