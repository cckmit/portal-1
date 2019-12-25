package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DocumentType;

public interface DocumentTypeDAO extends PortalBaseDAO<DocumentType> {

    String makeSelectIdByCategoryQuery();
}
