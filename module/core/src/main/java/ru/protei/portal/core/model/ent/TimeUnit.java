package ru.protei.portal.core.model.ent;

/**
 *
 */
public class TimeUnit {

    private Long id;
    private Long minutes;
    private String unitName;

    public TimeUnit() {
    }

    public Long getId() {
        return this.id;
    }

    public Long getMinutes() {
        return this.minutes;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMinutes(Long minutes) {
        this.minutes = minutes;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

}
