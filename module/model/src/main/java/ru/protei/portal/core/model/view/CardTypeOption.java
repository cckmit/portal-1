package ru.protei.portal.core.model.view;

import java.io.Serializable;

public class CardTypeOption implements Serializable {

    private String displayText;
    private Long id;

    public CardTypeOption() {
    }

    public CardTypeOption(String displayText, Long id) {
        this.displayText = displayText;
        this.id = id;
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

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CardTypeOption) {
            Long oid = ((CardTypeOption)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }
}
