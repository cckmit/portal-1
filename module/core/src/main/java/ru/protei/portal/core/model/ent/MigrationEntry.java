package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 05.07.16.
 */
@JdbcEntity(table = "MigrationEntry")
public class MigrationEntry {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private int id;

    @JdbcColumn(name = "entry_code")
    private String code;

    @JdbcColumn(name = "last_id")
    private Long lastId;


    public MigrationEntry() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }
}
