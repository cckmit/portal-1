package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 17.05.16.
 */
@JdbcEntity(table = "sys_config")
public class SystemConfig {

    @JdbcId(name="id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    @JdbcColumn(name="ownCompanyId")
    private Long ownCompanyId;

    public SystemConfig () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnCompanyId() {
        return ownCompanyId;
    }

    public void setOwnCompanyId(Long ownCompanyId) {
        this.ownCompanyId = ownCompanyId;
    }
}
