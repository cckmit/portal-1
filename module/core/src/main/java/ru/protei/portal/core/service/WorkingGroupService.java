package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.WorkingGroup;

import java.util.List;

/**
 * Created by michael on 05.05.17.
 */
public interface WorkingGroupService {
    CoreResponse<List<WorkingGroup>> getAll ();
}
