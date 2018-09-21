package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.controller.document.DocumentStorageIndex;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.winter.core.utils.services.lock.LockService;

import java.io.IOException;
import java.util.List;

import static com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly;

public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    DocumentStorageIndex documentStorageIndex;

    @Autowired
    LockService lockService;

    @Autowired
    DocumentSvnService documentSvnService;

    @Autowired
    DocumentControlService documentControlService;

    @Override
    public CoreResponse<Integer> count(AuthToken token, DocumentQuery query) {
        try {
            checkApplyFullTextSearchFilter(query);
        } catch (IOException e) {
            return new CoreResponse<Integer>().error(En_ResultStatus.INTERNAL_ERROR);
        }
      
        return new CoreResponse<Integer>().success(documentDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<Document>> documentList(AuthToken token, DocumentQuery query) {
        try {
            checkApplyFullTextSearchFilter(query);
        } catch (IOException e) {
            return new CoreResponse<List<Document>>().error(En_ResultStatus.INTERNAL_ERROR);
        }

        List<Document> list = documentDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<Document>>().error(En_ResultStatus.GET_DATA_ERROR);
        }

        list.forEach( document -> {
                    // RESET PRIVACY INFO
                    if (document.getContractor() != null) {
                        document.getContractor().resetPrivacyInfo();
                    }
                    if (document.getRegistrar() != null) {
                        document.getRegistrar().resetPrivacyInfo();
                    }
                }
        );


        return new CoreResponse<List<Document>>().success(list);
    }

    private void checkApplyFullTextSearchFilter(DocumentQuery query)  throws IOException {
        if (!isEmptyOrWhitespaceOnly(query.getInTextQuery())) {
            query.setOnlyIds(documentStorageIndex.getDocumentsByQuery(query.getInTextQuery(), query.limit));
        }
    }

    @Override
    public CoreResponse<Document> getDocument(AuthToken token, Long id) {
        return documentControlService.getDocument(id);
    }

    @Override
    public CoreResponse<Document> updateDocument(AuthToken token, Document document) {
        return documentControlService.updateDocument(document);
    }

    @Override
    public CoreResponse<Document> createDocument(AuthToken token, Document document, FileItem fileItem) {
        return documentControlService.createDocument(document, fileItem);
    }
}
