package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

public interface ContractDAO extends JdbcDAO<Long, Contract> {

    List<Contract> getListByQuery(ContractQuery query);

    Contract getByIdAndManagerId(Long id, Long managerId);

    int countByQuery(ContractQuery query);
}
