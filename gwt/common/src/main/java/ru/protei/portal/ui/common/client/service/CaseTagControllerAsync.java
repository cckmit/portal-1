package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;

import java.util.List;

public interface CaseTagControllerAsync {

    void saveTag(CaseTag caseTag, AsyncCallback<Void> async);

    void removeTag(CaseTag caseTag, AsyncCallback<Void> async);

    void getCaseTagsForCaseType(En_CaseType caseType, AsyncCallback<List<CaseTag>> async);
}
