package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DocumentTypeDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentTypeQuery;

import java.util.List;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class DocumentTypeServiceImpl implements DocumentTypeService {
    @Autowired
    DocumentTypeDAO documentTypeDAO;

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
}
