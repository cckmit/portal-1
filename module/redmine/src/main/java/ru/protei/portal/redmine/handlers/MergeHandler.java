package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.core.model.ent.CaseObject;

public interface MergeHandler {
    void merge(Issue issue, CaseObject object);
}
