package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;

@Table(name = "\"Resource\".view_person_contact_data")
public class ExtContactProperty implements LegacyEntity {
    @Column(name = "nID")
    public Long id;

    @Column(name = "nPersonID")
    public Long personId;

    @Column(name = "strProp")
    public String propType;

    @Column(name = "strCategory")
    public String category;

    @Column(name = "strValue")
    public String value;

    public ExtContactProperty() {
    }

    public String makeKey () {
        return this.propType + " " + this.category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropType() {
        return propType;
    }

    public void setPropType(String propType) {
        this.propType = propType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
