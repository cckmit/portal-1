package ru.protei.portal.ui.common.client.activity.casetag.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.ui.common.client.activity.casetag.item.AbstractCaseTagItemActivity;
import ru.protei.portal.ui.common.client.activity.casetag.item.AbstractCaseTagItemView;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Активность списка тегов
 */
public abstract class CaseTagListActivity
        implements Activity,
        AbstractCaseTagListActivity, AbstractCaseTagItemActivity {

    @Inject
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow(CaseTagEvents.Show event) {
        this.show = event;

        event.parent.clear();
        event.parent.add(view.asWidget());

        view.getTagsContainer().clear();
        view.addButtonVisibility().setVisible(event.isEnabledAttachOptions);
        view.setTagsAddButtonEnabled(event.isAddNewTagEnabled);
        view.setTagsEditButtonEnabled(event.isEditTagEnabled);
        view.setType(show.caseType);

        CaseTagQuery query = new CaseTagQuery();
        query.setCaseType(En_CaseType.CRM_SUPPORT);
        query.setCaseId(event.caseId);
        controller.getTags(query, new FluentCallback<List<CaseTag>>()
                .withSuccess(this::fillView)
        );
    }

    @Event
    public void onRemoveTag(CaseTagEvents.Remove event) {
//        issue.getTags().remove(event.getCaseTag());
//        view.tags().setValue(issue.getTags());
    }

    @Override
    public void onAddClicked() {
        CaseTag caseTag = new CaseTag();
        caseTag.setCaseType(show.caseType);

        fireEvent(new CaseTagEvents.Update(caseTag, true));
    }

    @Override
    public void onEditClicked(CaseTag caseTag, boolean isReadOnly) {
        fireEvent(isReadOnly ? new CaseTagEvents.Readonly(caseTag) : new CaseTagEvents.Update(caseTag, true));
    }

    @Override
    public void onDetachClicked(AbstractCaseTagItemView itemView) {
        if (itemView == null || !show.isEnabledAttachOptions) {
            return;
        }
        controller.detachTag(show.caseId, itemView.getModelId(), new FluentCallback<Void>()
                .withSuccess(res -> itemView.asWidget().removeFromParent()));
    }

    @Override
    public void onAttachTagClicked(CaseTag value) {
        if (value == null || !show.isEnabledAttachOptions) {
            return;
        }
        controller.attachTag(show.caseId, value.getId(), new FluentCallback<Void>()
                .withSuccess(id -> makeCaseTagViewAndAddToParent(value)));
    }

    private void fillView(List<CaseTag> links) {
        view.getTagsContainer().clear();

        if (CollectionUtils.isEmpty(links)) {
            return;
        }
        links.forEach(this::makeCaseTagViewAndAddToParent);
    }

    private void makeCaseTagViewAndAddToParent(CaseTag value) {
        AbstractCaseTagItemView itemWidget = itemViewProvider.get();
        itemWidget.setActivity(this);
        itemWidget.setNameAndColor(value.getName(), value.getColor());
        itemWidget.setEnabled(show.isEnabledAttachOptions);
        itemWidget.setModelId(value.getId());

        view.getTagsContainer().add(itemWidget.asWidget());
    }

    @Inject
    private Lang lang;
    @Inject
    private CaseTagControllerAsync controller;
    @Inject
    private AbstractCaseTagListView view;
    @Inject
    private Provider<AbstractCaseTagItemView> itemViewProvider;

    private CaseTagEvents.Show show;
}
