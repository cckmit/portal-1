package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

@JdbcEntity(table = "bootstrapchangelog")
public class BootstrapAction {

    @JdbcId(name = Columns.ID, idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name = Columns.NAME)
    private String name;

    public interface Columns {
        String ID = "id";
        String NAME = "name";
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
