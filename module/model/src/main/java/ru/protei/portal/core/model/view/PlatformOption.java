package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.Location;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;

import java.io.Serializable;

/**
 * Created by michael on 08.11.16.
 */
public class PlatformOption implements Serializable {

    private String displayText;
    private Long id;
    private Long companyId;

    public PlatformOption() {
    }

    public PlatformOption(String displayText, Long id) {
        this(displayText, id, null);
    }


    public PlatformOption(String displayText, Long id, Long companyId) {
        this.displayText = displayText;
        this.id = id;
        this.companyId = companyId;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
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

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlatformOption) {
            Long oid = ((PlatformOption)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }
}
