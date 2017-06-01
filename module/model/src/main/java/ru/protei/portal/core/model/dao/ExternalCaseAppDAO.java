package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ExternalCaseAppData;

/**
 * Created by michael on 19.05.16.
 */
public interface ExternalCaseAppDAO extends PortalBaseDAO<ExternalCaseAppData> {

    ExternalCaseAppData getByExternalAppId(String extAppId);

    boolean saveExtAppData(ExternalCaseAppData data);
}
