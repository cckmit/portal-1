package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.view.WorkerEntryShortView;

import java.util.List;
import java.util.Set;

public interface WorkerEntryShortViewDAO extends PortalBaseDAO<WorkerEntryShortView> {
    List<WorkerEntryShortView> listByPersonIds(Set<Long> personIds);
}
