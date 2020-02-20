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
public abstract class   CaseTagListActivity
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
        view.setTagsAddButtonEnabled(event.isEditTagEnabled);
        view.setTagsEditButtonEnabled(event.isEditTagEnabled);
        view.setType(show.caseType);
        hideOrShowIfNoTags();

        if (isCaseCreationMode()) {
            return;
        }

        refreshTagList();
    }

    @Event
    public void onTagChanged(CaseTagEvents.ChangeModel event) {
        refreshTagSelector();
        refreshTagList();
    }

    @Event
    public void onShowTagSelector(CaseTagEvents.ShowTagSelector event) {
        if (show.isReadOnly) {
            return;
        }
        view.showSelector(event.target);
    }

    @Event
    public void onRemoveTag(CaseTagEvents.Remove event) {
        if ( event.caseTag == null ) {
            return;
        }

        if (isCaseCreationMode()) {
            fireEvent(new CaseTagEvents.Detach(show.caseId, event.caseTag.getId()));
            return;
        }

        refreshTagList();
        refreshTagSelector();
    }

    @Override
    public void onAddClicked() {
        CaseTag caseTag = new CaseTag();
        caseTag.setCaseType(show.caseType);

        fireEvent(new CaseTagEvents.Edit(caseTag));
    }

    @Override
    public void onEditClicked(CaseTag caseTag, boolean isReadOnly) {
        fireEvent(new CaseTagEvents.Edit(caseTag));
    }

    @Override
    public void onDetachClicked(AbstractCaseTagItemView itemView) {
        if (itemView == null || show.isReadOnly) {
            return;
        }

        if ( isCaseCreationMode() ) {
            fireEvent(new CaseTagEvents.Detach(show.caseId, itemView.getModelId()));
            itemView.asWidget().removeFromParent();
            hideOrShowIfNoTags();
            return;
        }

        controller.detachTag(show.caseId, itemView.getModelId(), new FluentCallback<Void>()
                .withSuccess(res -> {
                    itemView.asWidget().removeFromParent();
                    hideOrShowIfNoTags();
                }));
    }

    @Override
    public void onAttachTagClicked(CaseTag value) {
        if (value == null || show.isReadOnly) {
            return;
        }

        if ( isCaseCreationMode() ) {
            fireEvent(new CaseTagEvents.Attach(show.caseId, value));
            makeCaseTagViewAndAddToParent(value);
            hideOrShowIfNoTags();
            return;
        }

        controller.attachTag(show.caseId, value.getId(), new FluentCallback<Void>()
                .withSuccess(id -> {
                    makeCaseTagViewAndAddToParent(value);
                    hideOrShowIfNoTags();
                }));
    }

    private void fillView(List<CaseTag> links) {
        view.getTagsContainer().clear();
        hideOrShowIfNoTags();
        if (CollectionUtils.isEmpty(links)) {
            return;
        }
        links.forEach(this::makeCaseTagViewAndAddToParent);
        hideOrShowIfNoTags();
    }

    private void makeCaseTagViewAndAddToParent(CaseTag value) {
        AbstractCaseTagItemView itemWidget = itemViewProvider.get();
        itemWidget.setActivity(this);
        itemWidget.setNameAndColor(value.getName(), value.getColor());
        itemWidget.setEnabled(!show.isReadOnly);
        itemWidget.setModelId(value.getId());
        view.getTagsContainer().add(itemWidget.asWidget());
    }

    private void refreshTagList() {
        CaseTagQuery query = new CaseTagQuery();
        query.setCaseType(En_CaseType.CRM_SUPPORT);
        query.setCaseId(show.caseId);
        controller.getTags(query, new FluentCallback<List<CaseTag>>()
                .withSuccess(this::fillView)
        );
    }

    private boolean isCaseCreationMode() {
        return show.caseId == null;
    }

    private void hideOrShowIfNoTags() {
        boolean isEmpty = !view.getTagsContainer().iterator().hasNext();
        view.getTagsContainerVisibility().setVisible(!isEmpty);
    }

    private void refreshTagSelector() {
        if(!view.isAttached()) return;
        view.setType(show.caseType);
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
