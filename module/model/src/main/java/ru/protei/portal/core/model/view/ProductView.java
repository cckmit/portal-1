package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.ent.DevUnit;

import java.io.Serializable;
import java.util.Date;

/**
 * @author michael
 */
public class ProductView implements Serializable {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String info;

    @JsonProperty
    private Date created;

    @JsonProperty
    private boolean active;

    public ProductView() {
    }

    public ProductView (DevUnit unit) {
        this.id = unit.getId();
        this.name = unit.getName();
        this.info = unit.getInfo();
        this.created = unit.getCreated();
        this.active = unit.isActiveUnit();
    }


    public String getName() {
        return this.name;
    }

    public Long getId() {
        return this.id;
    }

    public String getInfo() {
        return this.info;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
