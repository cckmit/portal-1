package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/CaseTagController" )
public interface CaseTagController extends RemoteService {

    void createTag(CaseTag caseTag) throws RequestFailedException;

    List<CaseTag> getCaseTagsForCaseType(En_CaseType caseType) throws RequestFailedException;
}
