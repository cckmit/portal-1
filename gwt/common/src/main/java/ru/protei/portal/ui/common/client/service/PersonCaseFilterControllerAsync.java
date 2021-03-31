package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.view.FilterShortView;

import java.util.List;

public interface PersonCaseFilterControllerAsync {
   void getCaseFilterByPersonId(Long personId, AsyncCallback< List< FilterShortView > > async );

   void addPersonToCaseFilter(Long personId, Long caseFilterId, AsyncCallback< Boolean > async);
   void removePersonToCaseFilter(Long personId, Long caseFilterId, AsyncCallback< Long > async);
   void changePersonToCaseFilter(Long personId, Long oldCaseFilterId, Long newCaseFilterId, AsyncCallback< Boolean > async);
}
