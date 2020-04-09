package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

/**
 * Created by michael on 05.07.16.
 */
@JdbcEntity(table = "migrationentry")
public class MigrationEntry {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "entry_code")
    private String code;

    @JdbcColumn(name = "lastUpdate")
    private Date lastUpdate;

    @JdbcColumn(name = "last_id")
    private Long lastId;


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

    public Long getLastId() {
        return lastId;
    }
    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public Date getLastUpdate() { return lastUpdate;   }

    public void setLastUpdate(Date lastUpdate) {  this.lastUpdate = lastUpdate;   }


    public En_MigrationEntry getEntryType () {
        return En_MigrationEntry.find(this.code);
    }
}
