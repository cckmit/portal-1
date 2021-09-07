package ru.protei.portal.ui.common.client.view.ytwork.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.ytwork.dialog.AbstractYoutrackReportDictionaryDialogView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork.ReportYoutrackDictionarySelector;
import ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork.ReportYoutrackWorkYtProjectMultiSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import java.util.Set;

public class YoutrackReportDictionaryView extends Composite implements AbstractYoutrackReportDictionaryDialogView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<En_ReportYoutrackWorkType> type() {
        return type;
    }

    @Override
    public HasValue<Set<YoutrackProject>> projects() {
        return projects;
    }

    @Override
    public void refreshProjects() {
        projects.clean();
    }

    protected void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.ensureDebugId(DebugIds.YOUTRACK_WORK_REPORT.DIALOG.NAME);
        type.setEnsureDebugId(DebugIds.YOUTRACK_WORK_REPORT.DIALOG.TYPE);
        projects.ensureDebugId(DebugIds.YOUTRACK_WORK_REPORT.DIALOG.PROJECTS);
        projects.setAddEnsureDebugId(DebugIds.YOUTRACK_WORK_REPORT.DIALOG.PROJECTS_ADD);
        projects.setClearEnsureDebugId(DebugIds.YOUTRACK_WORK_REPORT.DIALOG.PROJECTS_CLEAR);
        projects.setItemContainerEnsureDebugId(DebugIds.YOUTRACK_WORK_REPORT.DIALOG.PROJECTS_ITEM_CONTAINER);
    }

    @UiField
    ValidableTextBox name;

    @Inject
    @UiField(provided = true)
    ReportYoutrackDictionarySelector type;

    @Inject
    @UiField(provided = true)
    ReportYoutrackWorkYtProjectMultiSelector projects;

    @Inject
    @UiField
    Lang lang;

    private static AbsenceCommonViewUiBinder ourUiBinder = GWT.create(AbsenceCommonViewUiBinder.class);
    interface AbsenceCommonViewUiBinder extends UiBinder<HTMLPanel, YoutrackReportDictionaryView> {}
}
