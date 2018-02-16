package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.struct.AuditableObject;

public interface MergeHandler <T extends AuditableObject> {
    void merge(Issue issue, T object);
}
