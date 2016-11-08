package ru.protei.portal.core.model.view;

/**
 * Created by michael on 08.11.16.
 */
public class EntityOption {

    private String displayText;
    private Long id;

    public EntityOption() {
    }

    public EntityOption(String displayText, Long id) {
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
        if (obj instanceof EntityOption) {
            Long oid = ((EntityOption)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }
}
