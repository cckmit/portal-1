package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DocumentTypeDAO;
import ru.protei.portal.core.model.dao.DocumentationDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.query.DocumentationQuery;

import java.util.List;

public class DocumentationServiceImpl implements DocumentationService {
    @Autowired
    DocumentationDAO documentationDAO;

    @Autowired
    DocumentTypeDAO documentTypeDAO;


    @Override
    public CoreResponse<Long> count(AuthToken token, DocumentationQuery query) {
        return new CoreResponse<Long>().success(documentationDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<Documentation>> documentationList(AuthToken token, DocumentationQuery query) {
        List<Documentation> list = documentationDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<Documentation>>().error(En_ResultStatus.GET_DATA_ERROR);
        }
        return new CoreResponse<List<Documentation>>().success(list);
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
