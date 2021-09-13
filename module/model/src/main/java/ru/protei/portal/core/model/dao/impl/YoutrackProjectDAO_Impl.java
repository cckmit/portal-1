package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.YoutrackProjectDAO;
import ru.protei.portal.core.model.ent.YoutrackProject;

import static ru.protei.portal.core.model.ent.YoutrackProject.Fields.YOUTRACK_ID;

public class YoutrackProjectDAO_Impl extends PortalBaseJdbcDAO<YoutrackProject> implements YoutrackProjectDAO {
    @Override
    public YoutrackProject getByYoutrackId(String youtrackId) {
        return getByCondition(YOUTRACK_ID + " = ?", youtrackId);
    }
}
