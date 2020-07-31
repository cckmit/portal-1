package ru.protei.portal.ui.common.client.activity.casetag.taglist;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.item.AbstractCaseTagItemView;
import ru.protei.portal.ui.common.client.events.CaseHistoryEvents;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static java.util.Collections.replaceAll;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

/**
 * Активность списка тегов
 */
public abstract class CaseTagListActivity implements Activity, AbstractCaseTagListActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
    }

    // not event listener
    public void onShow(CaseTagEvents.ShowList event) {
        this.caseId = event.caseId;
        this.isReadOnly = event.isReadOnly;

        showView(event.parent);

        boolean isCaseTagsDefined = event.caseTags != null;
        if (isCaseTagsDefined) {
            tags = event.caseTags;
            fillView(tags);
            return;
        }

        if (isCaseCreationMode()) {
            tags = new ArrayList<>();
            fillView(tags);
            return;
        }

        requestTags(caseId, caseTags -> {
            tags = caseTags;
            fillView(tags);
        });
    }

    @Event
    public void onTagChanged(CaseTagEvents.Changed event) {
        if (event.caseTag == null) {
            return;
        }
        if (replaceAll(tags, event.caseTag, event.caseTag)) {
            fillView(tags);
        }
    }

    @Event
    public void onTagRemoved(CaseTagEvents.Removed event) {
        if (event.caseTag == null) {
            return;
        }
        if (tags.remove(event.caseTag)) {
            if (isCaseCreationMode()) {
                fireEvent(new CaseTagEvents.Detach(caseId, event.caseTag.getId()));
            }
            fillView(tags);
        }
    }

    private void showView(HasWidgets parent) {
        parent.clear();
        parent.add(view.asWidget());
    }

    private void fillView(List<CaseTag> tags) {
        log.info("fillView(): " + CollectionUtils.toList(tags, caseTag -> caseTag.getId() + "-" + caseTag.getName()));
        view.getTagsContainer().clear();
        if (isEmpty(tags)) {
            view.getTagsContainerVisibility().setVisible(false);
            return;
        }
        tags.forEach(caseTag -> {
            AbstractCaseTagItemView item = makeItemView(caseTag);
            view.getTagsContainer().add(item.asWidget());
        });
        view.getTagsContainerVisibility().setVisible(true);
    }

    private AbstractCaseTagItemView makeItemView(CaseTag caseTag) {
        AbstractCaseTagItemView item = itemViewProvider.get();
        item.setName(caseTag.getName());
        item.setColor(caseTag.getColor());
        item.setEnabled(!isReadOnly);
        item.setActivity(() -> {
            onTagDetach(caseTag, () -> {
                item.asWidget().removeFromParent();
                if (tags.remove(caseTag)) {
                    fillView(tags);
                }
            });
        });
        return item;
    }

    private void requestTags(Long caseId, Consumer<List<CaseTag>> onRequested) {
        CaseTagQuery query = new CaseTagQuery();
        query.setCaseType(En_CaseType.CRM_SUPPORT);
        query.setCaseId(caseId);
        controller.getTags(query, new FluentCallback<List<CaseTag>>()
                .withSuccess(onRequested));
    }

    private void onTagDetach(CaseTag caseTag, Runnable onRemoveConfirmed) {
        if (caseTag == null || isReadOnly) {
            return;
        }

        if (isCaseCreationMode()) {
            fireEvent(new CaseTagEvents.Detach(caseId, caseTag.getId()));
            onRemoveConfirmed.run();
            return;
        }

        controller.detachTag(caseId, caseTag.getId(), new FluentCallback<Long>()
                .withSuccess(removedId -> {
                    if (!Objects.equals(removedId, caseTag.getId())) return;
                    onRemoveConfirmed.run();
                    fireEvent(new CaseHistoryEvents.Reload(caseId));
                }));
    }

    @Override
    public void onTagAttach(CaseTag caseTag) {
        if (caseTag == null || isReadOnly) {
            return;
        }

        if (isCaseCreationMode()) {
            fireEvent(new CaseTagEvents.Attach(caseId, caseTag));
            if (!tags.contains(caseTag)) {
                tags.add(caseTag);
                fillView(tags);
            }
            return;
        }

        controller.attachTag(caseId, caseTag.getId(), new FluentCallback<Void>()
                .withSuccess(id -> {
                    tags.add(caseTag);
                    fillView(tags);
                    fireEvent(new CaseHistoryEvents.Reload(caseId));
                }));
    }

    private boolean isCaseCreationMode() {
        return caseId == null;
    }

    @Inject
    private CaseTagControllerAsync controller;
    @Inject
    private AbstractCaseTagListView view;
    @Inject
    private Provider<AbstractCaseTagItemView> itemViewProvider;

    private Long caseId;
    private boolean isReadOnly = false;
    private List<CaseTag> tags = new ArrayList<>();
    private static final Logger log = Logger.getLogger(CaseTagListActivity.class.getName());
}
