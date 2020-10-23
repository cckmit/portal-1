package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.WorkerPosition;

import java.util.List;

/**
 * Created by turik on 18.08.16.
 */
public interface WorkerPositionDAO extends PortalBaseDAO<WorkerPosition> {
    WorkerPosition getByName(String name, Long companyId);
    boolean checkExistsByName(String name, Long companyId);
    boolean checkExistsByName(String name, Long companyId, Long excludeId);
    List<WorkerPosition> getListByCompanyId(Long companyId);
}
