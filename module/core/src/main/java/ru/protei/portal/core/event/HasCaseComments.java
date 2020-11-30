package ru.protei.portal.core.event;

import ru.protei.portal.core.model.ent.CaseComment;

import java.util.List;

public interface HasCaseComments {
    List<CaseComment> getAddedCaseComments();
    List<CaseComment> getChangedCaseComments();
    List<CaseComment> getRemovedCaseComments();
}
