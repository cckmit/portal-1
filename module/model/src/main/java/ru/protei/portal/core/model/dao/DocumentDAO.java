package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface DocumentDAO extends PortalBaseDAO<Document> {

    SearchResult<Document> getSearchResult(DocumentQuery query);

    List<Document> getListByQuery(DocumentQuery query);

    boolean checkInventoryNumberExists(long inventoryNumber);

    boolean checkDecimalNumberExists(String decimalNumber);

    void updateState(Document document);
}
