package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;

@Table(name = "\"Resource\".Tm_Emails")
public class ExtCompanyEmailSubs implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "strEmail")
    private String email;

    @Column(name = "strInfo")
    private String langInfo;

    @Column(name = "strSystem")
    private String system;

    @Column(name = "nCompanyId")
    private Long companyId;


    public ExtCompanyEmailSubs() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangInfo() {
        return langInfo;
    }

    public void setLangInfo(String langInfo) {
        this.langInfo = langInfo;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }


    public String uniqueKey () {
        return (this.email + "_" + String.valueOf(this.companyId));
    }

    @Override
    public int hashCode() {
        return uniqueKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ExtCompanyEmailSubs && ((ExtCompanyEmailSubs) obj).uniqueKey().equals(uniqueKey());
    }
}
