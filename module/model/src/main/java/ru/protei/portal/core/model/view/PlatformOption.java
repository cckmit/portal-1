package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.marker.HasLongId;

import java.io.Serializable;

public class PlatformOption implements Serializable, HasLongId {

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

    @Override
    public Long getId() {
        return id;
    }

    @Override
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
