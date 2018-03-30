package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;

import java.util.List;

public interface DocumentDAO extends PortalBaseDAO<Document> {
    List getListByQuery(DocumentQuery query);

    Long countByQuery(DocumentQuery query);
}