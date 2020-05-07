package ru.protei.portal.ui.common.client.view.casetag.taglist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListView;

public class CaseTagListView extends Composite implements AbstractCaseTagListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCaseTagListActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public HasVisibility getTagsContainerVisibility() {
        return tagsContainer;
    }

    private void ensureDebugIds() {
        tagsContainer.ensureDebugId(DebugIds.ISSUE.TAGS_CONTAINER);
    }

    @UiField
    HTMLPanel tagsContainer;

    private AbstractCaseTagListActivity activity;

    private static CaseTagListUiBinder ourUiBinder = GWT.create(CaseTagListUiBinder.class);
    interface CaseTagListUiBinder extends UiBinder<HTMLPanel, CaseTagListView> {}
}