package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/CaseTagController" )
public interface CaseTagController extends RemoteService {

    Long saveTag( CaseTag caseTag) throws RequestFailedException;

    void removeTag(CaseTag caseTag) throws RequestFailedException;

    List<CaseTag> getTags(CaseTagQuery query) throws RequestFailedException;

    void attachTag(Long caseId, Long tagId) throws RequestFailedException;

    void detachTag(Long caseId, Long tagId) throws RequestFailedException;
}
