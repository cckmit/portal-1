package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface UserLoginShortViewDAO extends PortalBaseDAO<UserLoginShortView> {
    SearchResult<UserLoginShortView> getSearchResult(UserLoginShortViewQuery query);
}
