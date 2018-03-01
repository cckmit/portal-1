package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;

@Table(name = "\"resource\".Tm_CompanyLogin")
public class ExtContactLogin implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "nCompanyID")
    private Long companyId;

    @Column(name = "strLogin")
    private String login;

    @Column(name = "strInfo")
    private String info;

    @Column(name = "nPersonId")
    private Long personId;

    @Column(name = "strPassword")
    private String password;


    public ExtContactLogin() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString () {
        return new StringBuilder("company-login{").append(this.id).append("/")
                .append(login)
                .append("[").append(info).append("]")
                .append("}").toString();
    }

    public String translatedLogin () {
        return this.login.contains("@") ? this.login : (this.login+ "@crm.protei.ru");
    }
}
