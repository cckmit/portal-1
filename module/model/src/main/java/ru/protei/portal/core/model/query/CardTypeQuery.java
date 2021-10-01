package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

public class CardTypeQuery extends BaseQuery {

    public CardTypeQuery() {
    }

    public CardTypeQuery(String searchString, En_SortField sortField, En_SortDir sortDir, Boolean isDisplay ) {
        super(searchString, sortField, sortDir);
        this.isDisplay = isDisplay;
    }

    public Boolean getDisplay() {
        return isDisplay;
    }

    private Boolean isDisplay;
}
