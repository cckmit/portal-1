package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.struct.ProjectEntity;

import java.util.Collection;

public interface ProjectEntityDAO extends PortalBaseDAO<ProjectEntity> {

    Collection<ProjectEntity> selectScheduledPauseTime( long greaterThanTime );
}
