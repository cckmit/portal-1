package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

//import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by michael on 05.07.16.
 */
@JdbcEntity(table = "MigrationEntry")
public class MigrationEntry {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "entry_code")
    private String code;

    @JdbcColumn(name = "lastUpdate")
    private Date lastUpdate;


    public MigrationEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    public Long getLastId() {
//        return lastId;
//    }
//    public void setLastId(Long lastId) {
//        this.lastId = lastId;
//    }

    public Date getLastUpdate() {
        return new Date(lastUpdate.getTime() + 10800000); // +3 UTC
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = new Date(lastUpdate);
    }
}
