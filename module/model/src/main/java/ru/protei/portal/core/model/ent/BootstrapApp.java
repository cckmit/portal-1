package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

@JdbcEntity(table = "bootstrapchangelog")
public class BootstrapApp {


    @JdbcId(name = Columns.ID, idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name = Columns.UNIQUE_NAME)
    private String key;

    public interface Columns {
        String ID = "id";
        String UNIQUE_NAME = "unique_name";
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
