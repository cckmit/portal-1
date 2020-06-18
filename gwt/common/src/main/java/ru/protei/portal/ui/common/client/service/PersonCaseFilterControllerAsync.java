package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

public interface PersonCaseFilterControllerAsync {
   void getCaseFilterByPersonId(Long personId, AsyncCallback< List< CaseFilterShortView > > async );

   void changePersonToCaseFilter(Long personId, Long oldCaseFilterId, Long newCaseFilterId, AsyncCallback< Void > async);
}
