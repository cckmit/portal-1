package ru.protei.portal.ui.common.client.view.ytwork.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.ytwork.dialog.AbstractYoutrackWorkDictionaryDialogView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork.YoutrackProjectMultiSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import java.util.Set;

public class YoutrackWorkDictionaryView extends Composite implements AbstractYoutrackWorkDictionaryDialogView {

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
    public boolean isValidName() {
        return name.isValid();
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
        name.ensureDebugId(DebugIds.YOUTRACK_WORK.DIALOG.NAME);
        projects.ensureDebugId(DebugIds.YOUTRACK_WORK.DIALOG.PROJECTS);
        projects.setAddEnsureDebugId(DebugIds.YOUTRACK_WORK.DIALOG.PROJECTS_ADD);
        projects.setClearEnsureDebugId(DebugIds.YOUTRACK_WORK.DIALOG.PROJECTS_CLEAR);
        projects.setItemContainerEnsureDebugId(DebugIds.YOUTRACK_WORK.DIALOG.PROJECTS_ITEM_CONTAINER);
    }

    @UiField
    ValidableTextBox name;

    @Inject
    @UiField(provided = true)
    YoutrackProjectMultiSelector projects;

    @Inject
    @UiField
    Lang lang;

    private static YoutrackWorkDictionaryViewUiBinder ourUiBinder = GWT.create(YoutrackWorkDictionaryViewUiBinder.class);
    interface YoutrackWorkDictionaryViewUiBinder extends UiBinder<HTMLPanel, YoutrackWorkDictionaryView> {}
}
