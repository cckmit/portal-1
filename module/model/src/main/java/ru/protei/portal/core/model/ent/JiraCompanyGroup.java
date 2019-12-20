package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

/**
 * The record is used as 'link' between jira Nexign company and company in our db
 *
 */
@JdbcEntity(table = "jira_company_group")
public class JiraCompanyGroup {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "jira_company_name")
    private String jiraCompanyName;

    @JdbcJoinedObject(localColumn = "company_id", table = "company", updateLocalColumn = true)
    private Company company;

    public JiraCompanyGroup() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJiraCompanyName() {
        return jiraCompanyName;
    }

    public void setJiraCompanyName(String jiraCompanyName) {
        this.jiraCompanyName = jiraCompanyName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
