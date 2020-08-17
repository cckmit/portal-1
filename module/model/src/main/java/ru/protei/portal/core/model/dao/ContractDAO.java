package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface ContractDAO extends PortalBaseDAO<Contract> {

    SearchResult<Contract> getSearchResult(ContractQuery query);

    Contract getByIdAndManagerId(Long id, Long managerId);

    int countByQuery(ContractQuery query);

    List<Contract> getByProjectId(Long projectId);

    List<Contract> getByRefKeys(List<String> refKeys);
}
