package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;

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
}
