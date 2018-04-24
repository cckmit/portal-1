package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DocumentTypeDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;

import java.util.List;

public class DocumentTypeServiceImpl implements DocumentTypeService {
    @Autowired
    DocumentTypeDAO documentTypeDAO;

    @Override
    public CoreResponse<List<DocumentType>> documentTypeList(AuthToken token) {
        List<DocumentType> list = documentTypeDAO.getAll();
        if (list == null) {
            return new CoreResponse<List<DocumentType>>().error(En_ResultStatus.GET_DATA_ERROR);
        }
        return new CoreResponse<List<DocumentType>>().success(list);
    }
}
