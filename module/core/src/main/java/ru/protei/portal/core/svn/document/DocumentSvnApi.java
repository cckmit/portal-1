package ru.protei.portal.core.svn.document;

import org.tmatesoft.svn.core.SVNException;
import ru.protei.portal.core.model.dict.En_DocumentFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface DocumentSvnApi {

    void saveDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, String commitMessage, InputStream inputStream) throws SVNException;

    void updateDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, String commitMessage, InputStream inputStream) throws SVNException, IOException;

    void getDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, OutputStream outputStream) throws SVNException;

    void removeDocument(Long projectId, Long documentId, En_DocumentFormat documentFormat, String commitMessage) throws SVNException;

    List<String> listDocuments(Long projectId, Long documentId) throws SVNException;
}
