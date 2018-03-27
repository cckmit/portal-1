package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.IssueFilter;

import java.util.List;

public interface IssueFilterDAO extends PortalBaseDAO<IssueFilter> {

    List<IssueFilter> getFiltersByUser(Long loginId);
}
