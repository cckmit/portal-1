package ru.protei.portal.ui.delivery.client.activity.rfidlabels.table.filter;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;

public interface AbstractRFIDLabelParamWidget extends IsWidget, FilterParamView<RFIDLabelQuery> {}