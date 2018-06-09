package ru.protei.portal.core.service;

import org.tmatesoft.svn.core.SVNException;

import java.io.InputStream;
import java.io.OutputStream;

public interface DocumentSvnService {
    void saveDocument(Long projectId, Long documentId, InputStream fileData) throws SVNException;

    void getDocument(Long projectId, Long documentId, OutputStream outputStream) throws SVNException;
}
