package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.util.List;

@JdbcEntity(table = "case_state")
public class CaseState {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="STATE")
    String state;

    @JdbcColumn(name="INFO")
    String info;

    @JdbcManyToMany(linkTable = "case_state_to_company", localLinkColumn = "state_id", remoteLinkColumn = "company_id")
    public List<Company> companies;
}
