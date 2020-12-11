package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author michael
 */

@JdbcEntity(table = "importance_level")
public class ImportanceLevel implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="code")
    private String code;

    @JdbcColumn(name="info")
    private String info;

    public ImportanceLevel() {}

    public ImportanceLevel(Long id) {
        this.id = id;
    }

    public ImportanceLevel(Long id, String code) {
        this.id = id;
        this.code = code;
    }

    public ImportanceLevel(Long id, String code, String info) {
        this.id = id;
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return this.code;
    }

    public Long getId() {
        return this.id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportanceLevel that = (ImportanceLevel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
