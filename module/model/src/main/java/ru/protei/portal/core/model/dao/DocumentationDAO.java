package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.query.DocumentationQuery;

import java.util.List;

public interface DocumentationDAO extends PortalBaseDAO<Documentation> {
    List getListByQuery(DocumentationQuery query);

    Long countByQuery(DocumentationQuery query);
}
