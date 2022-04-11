package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.marker.HasLongId;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "calculation_type")
public class CalculationType implements Serializable, HasLongId {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "ref_key")
    private String refKey;

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

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationType that = (CalculationType) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(refKey, that.refKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, refKey);
    }

    @Override
    public String toString() {
        return "CalculationType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", refKey='" + refKey + '\'' +
                '}';
    }
}
