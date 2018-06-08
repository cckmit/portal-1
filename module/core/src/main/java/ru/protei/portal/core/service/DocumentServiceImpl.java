package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.controller.document.DocumentIndex;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dict.En_DecimalNumberEntityType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;

import java.io.IOException;
import java.util.List;

import static com.mysql.jdbc.StringUtils.isEmptyOrWhitespaceOnly;

public class DocumentServiceImpl implements DocumentService {
    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    DecimalNumberDAO decimalNumberDAO;

    @Autowired
    DocumentIndex documentIndex;


    @Override
    public CoreResponse<Long> count(AuthToken token, DocumentQuery query) {
        try {
            checkApplyFullTextSearchFilter(query);
        } catch (IOException e) {
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        return new CoreResponse<Long>().success(documentDAO.countByQuery(query));
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
        return new CoreResponse<List<Document>>().success(list);
    }

    private void checkApplyFullTextSearchFilter(DocumentQuery query)  throws IOException {
        if (!isEmptyOrWhitespaceOnly(query.getInTextQuery())) {
            query.setOnlyIds(documentIndex.getDocumentsByQuery(null, query.getInTextQuery(), query.limit));
        }
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
    @Transactional
    public CoreResponse<Document> saveDocument(AuthToken token, Document document) {
        if(!document.isValid()) {
            return new CoreResponse<Document>().error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!documentDAO.saveOrUpdate(document)) {
            return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        document.getDecimalNumber().setEntityId(document.getId());
        document.getDecimalNumber().setEntityType(En_DecimalNumberEntityType.DOCUMENT);
        if (!decimalNumberDAO.saveOrUpdate(document.getDecimalNumber())) {
            return new CoreResponse<Document>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        return new CoreResponse<Document>().success(document);
    }
}
