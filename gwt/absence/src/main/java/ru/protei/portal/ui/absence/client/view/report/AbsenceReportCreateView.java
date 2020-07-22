package ru.protei.portal.ui.absence.client.view.report;

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
import ru.protei.portal.ui.absence.client.activity.report.AbstractAbsenceReportCreateView;
import ru.protei.portal.ui.absence.client.view.report.paramview.AbsenceFilterParamView;
import ru.protei.portal.ui.absence.client.widget.AbsenceFilterWidget;
import ru.protei.portal.ui.absence.client.widget.AbsenceFilterWidgetModel;
import ru.protei.portal.ui.common.client.lang.Lang;

public class AbsenceReportCreateView extends Composite implements AbstractAbsenceReportCreateView {

    @Inject
    public void onInit(AbsenceFilterWidgetModel model) {
        filterWidget.onInit(model);
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public AbsenceFilterParamView getFilterParams() {
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
        absenceReportTitleLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE_REPORT.NAME_LABEL);
        name.ensureDebugId(DebugIds.ABSENCE_REPORT.NAME_INPUT);
    }

    @UiField
    LabelElement absenceReportTitleLabel;

    @Inject
    @UiField(provided = true)
    AbsenceFilterWidget filterWidget;

    @UiField
    TextBox name;

    @Inject
    @UiField
    Lang lang;

    private static AbsenceReportCreateViewUiBinder ourUiBinder = GWT.create(AbsenceReportCreateViewUiBinder.class);
    interface AbsenceReportCreateViewUiBinder extends UiBinder<HTMLPanel, AbsenceReportCreateView> {}
}