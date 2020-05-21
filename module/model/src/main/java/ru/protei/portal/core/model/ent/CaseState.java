package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JdbcEntity(table = "case_state")
public class CaseState implements Serializable {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="STATE")
    String state;

    @JdbcColumn(name="INFO")
    String info;

    @JdbcColumn(name = "usage_in_companies")
    @JdbcEnumerated( EnumType.ORDINAL )
    private En_CaseStateUsageInCompanies usageInCompanies;

    @JdbcManyToMany(linkTable = "case_state_to_company", localLinkColumn = "state_id", remoteLinkColumn = "company_id")
    public List<Company> companies;

    @JdbcColumn(name = "is_terminal")
    private boolean terminal;

    private int viewOrder;

    public CaseState() {
    }

    public CaseState(Long id) {
        this.id = id;
    }

    public CaseState(Long id, String state) {
        this.id = id;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public En_CaseStateUsageInCompanies getUsageInCompanies() {
        return usageInCompanies;
    }

    public void setUsageInCompanies(En_CaseStateUsageInCompanies usageInCompanies) {
        this.usageInCompanies = usageInCompanies;
    }

    public int getViewOrder() {
        return viewOrder;
    }

    public CaseState withViewOrder(int viewOrder) {
        this.viewOrder = viewOrder;
        return this;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseState state = (CaseState) o;
        return Objects.equals(id, state.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CaseState{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", info='" + info + '\'' +
                ", usageInCompanies=" + usageInCompanies +
                ", companies=" + companies +
                '}';
    }
}
