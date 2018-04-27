package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.*;

import java.util.Set;

public class DocumentTypeQuery extends BaseQuery {

    private Set<En_DocumentCategory> categories;

    public DocumentTypeQuery(String searchString, En_SortField sortField, En_SortDir sortDir, Set<En_DocumentCategory> categories) {
        super(searchString, sortField, sortDir);
        this.categories = categories;
    }

    public DocumentTypeQuery() {}

    public Set<En_DocumentCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<En_DocumentCategory> categories) {
        this.categories = categories;
    }
}
