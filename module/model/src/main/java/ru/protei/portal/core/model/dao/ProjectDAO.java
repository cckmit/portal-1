package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Collection;
import java.util.List;

public interface ProjectDAO extends PortalBaseDAO<Project> {

    Collection<Project> selectScheduledPauseTime( long greaterThanTime );

    SearchResult<Project> getSearchResult(ProjectQuery query);

    int countByQuery(ProjectQuery query);

    List<Project> getProjects(ProjectQuery query);
}
