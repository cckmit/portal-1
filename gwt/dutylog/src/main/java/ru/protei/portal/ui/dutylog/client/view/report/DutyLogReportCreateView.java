package ru.protei.portal.ui.dutylog.client.view.report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.dutylog.client.activity.report.AbstractDutyLogReportCreateView;
import ru.protei.portal.ui.dutylog.client.widget.filter.DutyLogFilterWidget;
import ru.protei.portal.ui.dutylog.client.widget.filter.DutyLogFilterWidgetModel;
import ru.protei.portal.ui.dutylog.client.widget.filter.paramview.DutyLogFilterParamWidget;

public class DutyLogReportCreateView extends Composite implements AbstractDutyLogReportCreateView {

    @Inject
    public void onInit(DutyLogFilterWidgetModel model) {
        filterWidget.onInit(model);
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public DutyLogFilterParamWidget getFilterParams() {
        return filterWidget.getFilterParamView();
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        filterWidget.resetFilter();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        dutyLogReportTitleLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DUTY_LOG.REPORT.NAME_LABEL);
        name.ensureDebugId(DebugIds.DUTY_LOG.REPORT.NAME_INPUT);
    }

    @UiField
    LabelElement dutyLogReportTitleLabel;

    @Inject
    @UiField(provided = true)
    DutyLogFilterWidget filterWidget;

    @UiField
    TextBox name;

    @Inject
    @UiField
    Lang lang;

    private static DutyLogReportCreateViewUiBinder ourUiBinder = GWT.create(DutyLogReportCreateViewUiBinder.class);
    interface DutyLogReportCreateViewUiBinder extends UiBinder<HTMLPanel, DutyLogReportCreateView> {}
}