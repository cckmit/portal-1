package ru.protei.portal.ui.common.client.activity.casetag.tagselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.client.widget.casetag.popup.CaseTagSelector;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class CaseTagSelectorActivity implements Activity, AbstractCaseTagSelectorActivity {

    @Inject
    public void init() {
        caseTagSelector.addAddHandler(e -> onAddNewTag());
        caseTagSelector.addEditHandler(e -> onEditTag(e.caseTag));
        caseTagSelector.addValueChangeHandler(e -> onSelectTag(e.getValue()));
    }

    @Event
    public void onShow(CaseTagEvents.ShowSelector event) {
        caseType = event.caseType;
        tagListActivity = event.tagListActivity;
        refreshTagSelector();
        caseTagSelector.setAddTagsEnabled(event.isEditTagEnabled);
        caseTagSelector.setEditTagsEnabled(event.isEditTagEnabled);
        caseTagSelector.showNear(event.relative);
    }

    @Event
    public void onTagCreated(CaseTagEvents.Created event) {
        refreshTagSelector();
    }

    @Event
    public void onTagChanged(CaseTagEvents.Changed event) {
        refreshTagSelector();
    }

    @Event
    public void onTagRemoved(CaseTagEvents.Removed event) {
        refreshTagSelector();
    }

    private void onAddNewTag() {
        CaseTag caseTag = new CaseTag();
        caseTag.setCaseType(caseType);
        fireEvent(new CaseTagEvents.ShowEdit(caseTag));
    }

    private void onEditTag(CaseTag caseTag) {
        fireEvent(new CaseTagEvents.ShowEdit(caseTag));
    }

    private void onSelectTag(CaseTag caseTag) {
        if (tagListActivity != null) {
            tagListActivity.onTagAttach(caseTag);
        }
    }

    private void refreshTagSelector() {
        CaseTagQuery query = new CaseTagQuery();
        query.setCaseType(caseType);
        controller.getTags(query, new FluentCallback<List<CaseTag>>()
                .withSuccess(tags -> caseTagSelector.setTags(tags)));
    }

    @Inject
    CaseTagControllerAsync controller;
    @Inject
    CaseTagSelector caseTagSelector;

    private En_CaseType caseType;
    private AbstractCaseTagListActivity tagListActivity;
}
