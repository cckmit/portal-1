package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/PersonCaseFilterController" )
public interface PersonCaseFilterController extends RemoteService {

    List<CaseFilterShortView> getCaseFilterByPersonId(Long personId) throws RequestFailedException;

    void changePersonToCaseFilter(Long personId, Long oldCaseFilterId, Long newCaseFilterId) throws RequestFailedException;
}
