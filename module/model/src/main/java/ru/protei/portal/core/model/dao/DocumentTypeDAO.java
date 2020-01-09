package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DocumentType;

import java.util.Set;

public interface DocumentTypeDAO extends PortalBaseDAO<DocumentType> {

    String makeSelectIdByCategoriesQuery(Set<En_DocumentCategory> categories);
}
