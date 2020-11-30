package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DutyLogFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/DutyLogFilterController" )
public interface DutyLogFilterController extends RemoteService {
    List<FilterShortView> getShortViewList() throws RequestFailedException;

    DutyLogFilter getFilter(Long id) throws RequestFailedException;

    SelectorsParams getSelectorsParams(DutyLogQuery query) throws RequestFailedException;

    DutyLogFilter saveFilter(DutyLogFilter filter) throws RequestFailedException;

    Long removeFilter(Long id) throws RequestFailedException;
}
