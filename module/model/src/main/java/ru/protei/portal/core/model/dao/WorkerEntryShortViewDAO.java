package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.view.WorkerEntryShortView;

import java.util.List;

public interface WorkerEntryShortViewDAO extends PortalBaseDAO<WorkerEntryShortView> {
    List<WorkerEntryShortView> listByPersonIds(List<Long> personIds);
}
