package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface ContractDAO extends PortalBaseDAO<Contract> {

    SearchResult<Contract> getSearchResult(ContractQuery query);

    Contract getByIdAndManagerId(Long id, Long managerId);

    int countByQuery(ContractQuery query);
}
