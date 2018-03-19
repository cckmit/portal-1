package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dao.DocumentTypeDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentQuery;

import java.util.List;

import static ru.protei.portal.core.model.helper.DocumentHelper.isDocumentValid;

public class DocumentServiceImpl implements DocumentService {
    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    DocumentTypeDAO documentTypeDAO;


    @Override
    public CoreResponse<Long> count(AuthToken token, DocumentQuery query) {
        return new CoreResponse<Long>().success(documentDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<Document>> documentList(AuthToken token, DocumentQuery query) {
        List<Document> list = documentDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<Document>>().error(En_ResultStatus.GET_DATA_ERROR);
        }
        return new CoreResponse<List<Document>>().success(list);
    }

    @Override
    public CoreResponse<Document> getDocument(AuthToken token, Long id) {
        Document document = documentDAO.get(id);

        if (document == null) {
            return new CoreResponse<Document>().error(En_ResultStatus.NOT_FOUND);
        }
        return new CoreResponse<Document>().success(document);
    }

    @Override
    public CoreResponse<Document> saveDocument(AuthToken token, Document document) {
        if(!isDocumentValid(document)) {
            return new CoreResponse<Document>().error(En_ResultStatus.VALIDATION_ERROR);
        }
        if (documentDAO.saveOrUpdate(document)) {
            return new CoreResponse<Document>().success(document);
        }
        return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public CoreResponse<List<DocumentType>> documentTypeList(AuthToken token) {
        List<DocumentType> list = documentTypeDAO.getAll();
        if (list == null) {
            return new CoreResponse<List<DocumentType>>().error(En_ResultStatus.GET_DATA_ERROR);
        }
        return new CoreResponse<List<DocumentType>>().success(list);
    }
}
