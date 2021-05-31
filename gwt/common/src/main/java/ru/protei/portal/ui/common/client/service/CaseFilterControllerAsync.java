package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.FilterShortView;

import java.util.List;

public interface CaseFilterControllerAsync {
    /**
     * Получение списка сокращенного представления CaseFilter
     */
    void getCaseFilterShortViewList(En_CaseFilterType filterType, AsyncCallback< List<FilterShortView> > async );

    <T extends HasFilterQueryIds> void getCaseFilter(Long id, AsyncCallback<CaseFilterDto<T>> async );

    void getSelectorsParams(HasFilterQueryIds filterEntityIds, AsyncCallback<SelectorsParams> async);

    void removeCaseFilter(Long id, AsyncCallback< Long > async );

    void saveProjectFilter(CaseFilterDto<ProjectQuery> caseFilterDto, AsyncCallback<CaseFilterDto<ProjectQuery>> async);

    void saveIssueFilter(CaseFilterDto<CaseQuery> caseFilterDto, AsyncCallback<CaseFilterDto<CaseQuery>> async);

    void saveDeliveryFilter(CaseFilterDto<DeliveryQuery> caseFilterDto, AsyncCallback<CaseFilterDto<DeliveryQuery>> async);
}
