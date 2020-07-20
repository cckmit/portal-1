package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.struct.Project;

import java.util.Collection;

public interface ProjectDAO extends PortalBaseDAO<Project> {

    Collection<Project> selectScheduledPauseTime( long greaterThanTime );
}
