package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/AbsenceFilterController" )
public interface AbsenceFilterController extends RemoteService {
    List<FilterShortView> getShortViewList() throws RequestFailedException;

    AbsenceFilter getFilter(Long id) throws RequestFailedException;

    SelectorsParams getSelectorsParams(AbsenceQuery query) throws RequestFailedException;

    AbsenceFilter saveFilter(AbsenceFilter filter) throws RequestFailedException;

    Long removeFilter(Long id) throws RequestFailedException;
}
