package ru.protei.portal.redmine.factories;

import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.core.model.ent.CaseObject;

public interface MergeHandlerFactory {
    CaseObject mergeWithCaseObject(Issue issue, CaseObject object);
}
