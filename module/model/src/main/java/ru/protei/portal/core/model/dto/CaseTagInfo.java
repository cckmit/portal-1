package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;

import java.io.Serializable;

import static ru.protei.portal.core.model.util.CrmConstants.CaseTag.DEFAULT_COLOR;

public class CaseTagInfo implements Serializable {
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

    public static CaseTag toCaseTag(CaseTagInfo info) {
        if (info == null) {
            return null;
        }

        CaseTag caseTag = new CaseTag();

        caseTag.setId(info.getId());
        caseTag.setName(info.getName());
        caseTag.setColor(DEFAULT_COLOR);
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
