package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ModuleDAO;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;

public class ModuleDAO_Impl extends PortalBaseJdbcDAO<Module> implements ModuleDAO {
    @Override
    public List<Module> getListByKitId(Long kitId) {
        return getListByCondition("kit_id = ?", kitId);
    }

    @Override
    public List<String> getSerialNumbersByKitId(Long kitId) {
        StringBuilder sql = new StringBuilder("select serial_number from ")
                .append(getTableName())
                .append(" where kit_id=?");
        return jdbcTemplate.queryForList(sql.toString(), String.class, kitId);
    }
}