package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;

public class CaseTagInfo {
    private Long id;

    private String name;

    private Long companyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public CaseTagInfo() {}

    public CaseTagInfo(Long id, String name, Long companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }
    public static CaseTag fromInfo(CaseTagInfo info) {

        CaseTag caseTag = new CaseTag();

        caseTag.setId(info.getId());
        caseTag.setName(info.getName());
        caseTag.setCompanyId(info.getCompanyId());
        caseTag.setCaseType(En_CaseType.CRM_SUPPORT);

        return caseTag;
    }

    @Override
    public String toString() {
        return "CaseTagInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyId=" + companyId +
                '}';
    }
}
