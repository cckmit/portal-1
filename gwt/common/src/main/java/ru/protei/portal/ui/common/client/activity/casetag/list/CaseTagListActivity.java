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

import java.util.*;
import java.util.logging.Logger;

import static java.util.Collections.replaceAll;

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

        tags = new ArrayList<>();
        if (isCaseCreationMode()) {
            return;
        }

        requestsTags(show.caseId);
    }

    @Event
    public void onTagCreated( CaseTagEvents.Created event) {
        refreshTagSelector();
    }

    @Event
    public void onTagChanged( CaseTagEvents.Changed event) {
        refreshTagSelector();
        if ( replaceAll( tags, event.caseTag, event.caseTag ) ) {
            fillView( tags );
        }
    }

    @Event
    public void onShowTagSelector(CaseTagEvents.ShowTagSelector event) {
        if (show.isReadOnly) {
            return;
        }
        view.showSelector(event.target);
    }

    @Event
    public void onRemoveTag( CaseTagEvents.Removed event) {
        if ( event.caseTag == null ) {
            return;
        }

        if (isCaseCreationMode()) {
            fireEvent(new CaseTagEvents.Detach(show.caseId, event.caseTag.getId()));
        }

        if(tags.remove(event.caseTag )) {
            fillView( tags );
        }
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
            fireEvent(new CaseTagEvents.Detach(show.caseId, itemView.getCaseTag().getId()));
            itemView.asWidget().removeFromParent();
            if(tags.remove( itemView.getCaseTag() )) {
                fillView( tags );
            }
            return;
        }

        final CaseTag forRemove =  itemView.getCaseTag();
        if(forRemove==null) return;
        controller.detachTag(show.caseId,forRemove.getId(), new FluentCallback<Long>()
                .withSuccess(removedId -> {
                    if(!Objects.equals( removedId, forRemove.getId())) return;
                    itemView.asWidget().removeFromParent();
                    if(tags.remove( forRemove )) {
                        fillView( tags );
                    }
                }));
    }

    @Override
    public void onAttachTagClicked(CaseTag value) {
        if (value == null || show.isReadOnly) {
            return;
        }

        if ( isCaseCreationMode() ) {
            fireEvent(new CaseTagEvents.Attach(show.caseId, value));
            if(tags.contains( value )) return;
            tags.add(value);
            fillView( tags );
            return;
        }

        controller.attachTag(show.caseId, value.getId(), new FluentCallback<Void>()
                .withSuccess(id -> {
                    tags.add(value);
                    fillView( tags );
                }));
    }

    private void fillView(List<CaseTag> links) {
        log.info( "fillView(): " + CollectionUtils.toList( links, caseTag -> caseTag.getId() + "-" + caseTag.getName() ));
        view.getTagsContainer().clear();
        hideOrShowIfNoTags();
        if (CollectionUtils.isEmpty(links)) {
            return;
        }
        links.forEach(this::makeCaseTagViewAndAddToParent);
        hideOrShowIfNoTags();
    }

    private void makeCaseTagViewAndAddToParent(CaseTag caseTag) {
        AbstractCaseTagItemView itemWidget = itemViewProvider.get();
        itemWidget.setActivity(this);
        itemWidget.setNameAndColor(caseTag.getName(), caseTag.getColor());
        itemWidget.setEnabled(!show.isReadOnly);
        itemWidget.setCaseTag(caseTag);
        view.getTagsContainer().add(itemWidget.asWidget());
    }

    private void requestsTags(Long caseId) {
        CaseTagQuery query = new CaseTagQuery();
        query.setCaseType(En_CaseType.CRM_SUPPORT);
        query.setCaseId(caseId);
        controller.getTags(query, new FluentCallback<List<CaseTag>>()
                .withSuccess( links -> {
                    tags = links;
                    fillView( tags );
                } )
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

    private static final Logger log = Logger.getLogger( CaseTagListActivity.class.getName() );

    @Inject
    private Lang lang;
    @Inject
    private CaseTagControllerAsync controller;
    @Inject
    private AbstractCaseTagListView view;
    @Inject
    private Provider<AbstractCaseTagItemView> itemViewProvider;

    private CaseTagEvents.Show show;
    private List<CaseTag> tags = new ArrayList<>(  );
}
