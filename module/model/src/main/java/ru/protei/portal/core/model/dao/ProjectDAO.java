package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dto.Project;

import java.util.Collection;

public interface ProjectDAO extends PortalBaseDAO<Project> {

    Collection<Project> selectScheduledPauseTime( long greaterThanTime );
}
