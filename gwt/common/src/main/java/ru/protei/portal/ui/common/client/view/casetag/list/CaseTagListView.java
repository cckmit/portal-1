package ru.protei.portal.ui.common.client.view.casetag.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casetag.list.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.casetag.list.AbstractCaseTagListView;
import ru.protei.portal.ui.common.client.widget.casetag.popup.CaseTagSelector;

import java.util.List;

public class CaseTagListView
        extends Composite
        implements AbstractCaseTagListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initDebugIds();

        caseTagSelector.addValueChangeHandler(event -> activity.onAttachTagClicked(event.getValue()));
        caseTagSelector.addAddHandler(addEvent -> activity.onAddClicked());
        caseTagSelector.addEditHandler(editEvent -> activity.onEditClicked(editEvent.caseTag, editEvent.isReadOnly));
    }

    @Override
    public void setActivity(AbstractCaseTagListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void showSelector(IsWidget target) {
        caseTagSelector.showUnderLeft( (UIObject) target, null );
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public HasVisibility getTagsContainerVisibility() {
        return tagsContainer;
    }

    @Override
    public void setTags(List<CaseTag> tags) {
        caseTagSelector.setTags(tags);
    }

    @Override
    public void setTagsAddButtonEnabled(boolean enabled) {
        caseTagSelector.setAddTagsEnabled(enabled);
    }

    @Override
    public void setTagsEditButtonEnabled(boolean enabled) {
        caseTagSelector.setEditTagsEnabled(enabled);
    }

    private void initDebugIds() {
        tagsContainer.ensureDebugId(DebugIds.ISSUE.TAGS_CONTAINER);
    }

    @UiField
    HTMLPanel tagsContainer;

    @Inject
    private CaseTagSelector caseTagSelector;

    private AbstractCaseTagListActivity activity;

    private static CaseTagListUiBinder ourUiBinder = GWT.create(CaseTagListUiBinder.class);
    interface CaseTagListUiBinder extends UiBinder<HTMLPanel, CaseTagListView> {}
}