package ru.protei.portal.ui.project.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.project.client.activity.list.AbstractProjectDocumentsListActivity;
import ru.protei.portal.ui.project.client.activity.list.AbstractProjectDocumentsListView;

public class ProjectDocumentsListView extends Composite implements AbstractProjectDocumentsListView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractProjectDocumentsListActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets documentsContainer() {
        return documentsContainer;
    }

    @UiField
    HTMLPanel documentsContainer;

    private AbstractProjectDocumentsListActivity activity;

    interface ProjectDocumentsListUiBinder extends UiBinder<HTMLPanel, ProjectDocumentsListView> {}
    private static ProjectDocumentsListUiBinder ourUiBinder = GWT.create(ProjectDocumentsListUiBinder.class);
}
