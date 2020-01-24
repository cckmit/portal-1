package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dao.DocumentTypeDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.query.DocumentTypeQuery;

import java.util.List;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class DocumentTypeServiceImpl implements DocumentTypeService {
    @Autowired
    DocumentTypeDAO documentTypeDAO;

    @Autowired
    DocumentDAO documentDAO;

    @Override
    public Result<List<DocumentType>> documentTypeList( AuthToken token, DocumentTypeQuery query) {
        List<DocumentType> list = documentTypeDAO.listByQuery(query);
        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        return ok(list);
    }

    @Override
    public Result<DocumentType> saveDocumentType( AuthToken token, DocumentType documentType ) {
        if ( !documentTypeDAO.saveOrUpdate(documentType) ) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return ok(documentType);
    }

    @Override
    public Result<Long> removeDocumentType(AuthToken authToken, DocumentType documentType) {
        DocumentQuery documentQuery = new DocumentQuery();
        documentQuery.setDocumentType(documentType);

        List<Document> listByQuery = documentDAO.getListByQuery(documentQuery);

        if (listByQuery == null) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        if (!listByQuery.isEmpty()) {
            return error(En_ResultStatus.NOT_ALLOWED_REMOVE_USED_DOCUMENT_TYPE);
        }

        if (!documentTypeDAO.removeByKey(documentType.getId())) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return ok(documentType.getId());
    }
}
