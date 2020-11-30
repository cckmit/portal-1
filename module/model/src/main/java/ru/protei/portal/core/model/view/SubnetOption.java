package ru.protei.portal.core.model.view;

import java.io.Serializable;

public class SubnetOption implements Serializable {

    private String displayText;
    private Long id;
    private boolean allowForReserve;

    public SubnetOption() {
    }

    public SubnetOption(String displayText, Long id, boolean allowForReserve) {
        this.displayText = displayText;
        this.id = id;
        this.allowForReserve = allowForReserve;
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

    public boolean isAllowForReserve() { return allowForReserve; }

    public void setAllowForReserve(boolean allowForReserve) { this.allowForReserve = allowForReserve; }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SubnetOption) {
            Long oid = ((SubnetOption)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }
}
