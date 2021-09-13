package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.YoutrackProject;

public interface YoutrackProjectDAO extends PortalBaseDAO<YoutrackProject> {
    YoutrackProject getByYoutrackId(String youtrackId);
}