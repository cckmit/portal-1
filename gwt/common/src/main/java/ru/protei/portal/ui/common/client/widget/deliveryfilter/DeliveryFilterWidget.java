package ru.protei.portal.ui.common.client.widget.deliveryfilter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.widget.deliveryfilter.param.DeliveryFilterParamWidget;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidget;
import ru.protei.portal.ui.common.client.widget.selector.delivery.filter.DeliveryFilterSelector;

public class DeliveryFilterWidget extends FilterWidget<CaseFilterDto<DeliveryQuery>, DeliveryQuery, FilterShortView> {
    @Inject
    public DeliveryFilterWidget(DeliveryFilterParamWidget deliveryFilterParamWidget,
                                DeliveryFilterSelector projectFilterSelector) {
        filterParamView = deliveryFilterParamWidget;
        filterSelector = projectFilterSelector;
        this.deliveryFilterParamWidget = deliveryFilterParamWidget;
    }

    @Override
    public DeliveryFilterParamWidget getFilterParamView() {
        return deliveryFilterParamWidget;
    }

    @Override
    protected CaseFilterDto<DeliveryQuery> fillUserFilter() {
        CaseFilterDto<DeliveryQuery> caseFilterDto = new CaseFilterDto<>();
        CaseFilter caseFilter = new CaseFilter();
        caseFilter.setType(En_CaseFilterType.DELIVERY);
        caseFilter.setName(filterName.getValue());

        caseFilterDto.setQuery(filterParamView.getQuery());
        caseFilterDto.setCaseFilter(caseFilter);

        return caseFilterDto;
    }

    private final DeliveryFilterParamWidget deliveryFilterParamWidget;
}
