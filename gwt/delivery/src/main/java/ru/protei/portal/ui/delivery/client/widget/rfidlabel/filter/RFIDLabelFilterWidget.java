package ru.protei.portal.ui.delivery.client.widget.rfidlabel.filter;

import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.ui.common.client.widget.filterwidget.simplefilterwidget.SimpleFilterWidget;

import javax.inject.Inject;

public class RFIDLabelFilterWidget extends SimpleFilterWidget<RFIDLabelQuery> {

    @Inject
    public RFIDLabelFilterWidget(RFIDLabelParamWidget filterParamView) {
        this.filterParamView = filterParamView;
    }

    @Override
    public RFIDLabelParamWidget getFilterParamView() {
        return (RFIDLabelParamWidget)this.filterParamView;
    }
}
