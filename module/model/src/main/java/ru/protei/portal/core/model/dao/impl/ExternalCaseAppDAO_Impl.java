package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;

import java.util.List;

/**
 * Created by michael on 01.06.17.
 */
public class ExternalCaseAppDAO_Impl extends PortalBaseJdbcDAO<ExternalCaseAppData> implements ExternalCaseAppDAO {

    @Override
    public ExternalCaseAppData getByExternalAppId(String extAppId) {
        return getByCondition("EXT_APP_ID=?", extAppId);
    }

    @Override
    public boolean saveExtAppData (ExternalCaseAppData data) {
        return partialMerge(data, "EXT_APP_DATA");
    }

    @Override
    public List<ExternalCaseAppData> getListByParameters(String extAppType, String projectId, String extAppId) {
        return getListByCondition("EXT_APP=? and EXT_APP_DATA=? and EXT_APP_ID like ?", extAppType, projectId, extAppId);
    }
}
