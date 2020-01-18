package ru.protei.portal.ui.common.client.activity.caselink.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemActivity;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.CaseLinkEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseLinkControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.dict.En_CaseLink.CRM;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;

/**
 * Активность списка линков
 */
public abstract class CaseLinkListActivity
        implements Activity,
        AbstractCaseLinkListActivity, AbstractCaseLinkItemActivity {

    @Inject
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow(CaseLinkEvents.Show event) {
        this.show = event;

        event.parent.clear();
        event.parent.add(view.asWidget());

        view.getLinksContainer().clear();
        view.setLinksContainerVisible(Boolean.parseBoolean(storage.get(UiConstants.LINKS_PANEL_VISIBILITY)));
        hideOrShowIfNoLinks();

        linksCount = 0;
        resetLinksContainerStateByLinksCount();

        if (isCaseCreationMode()) return;

        controller.getCaseLinks(event.caseId, new FluentCallback<List<CaseLink>>()
                .withError(throwable -> showError(lang.errGetList()))
                .withSuccess(this::fillView)
        );
    }

    @Event
    public void onShowTagSelector(CaseLinkEvents.ShowLinkSelector event) {
        if (!show.isEnabled) {
            return;
        }

        this.pageId = event.pageId;
        this.createCrossLinks = event.createCrossLinks;

        view.showSelector(event.target);
    }

    @Override
    public void onRemoveClicked(AbstractCaseLinkItemView itemView) {
        if (itemView == null || !show.isEnabled) {
            return;
        }

        if (isCaseCreationMode()) {
            fireEvent(new CaseLinkEvents.Removed(show.caseId, itemView.getModel(), pageId));
            removeLinkViewFromParentAndModifyLinksCount(itemView);
            hideOrShowIfNoLinks();
            return;
        }

        controller.removeLink(itemView.getModel().getId(), new FluentCallback<Void>()
                .withSuccess(res -> {
                    removeLinkViewFromParentAndModifyLinksCount(itemView);
                    hideOrShowIfNoLinks();
                    fireEvent(new NotifyEvents.Show(lang.caseLinkSuccessfulRemoved(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }

    @Override
    public void onAddLinkClicked(CaseLink caseLink) {
        if (caseLink == null || !show.isEnabled) {
            return;
        }

        caseLink.setCaseId(show.caseId);
        switch (caseLink.getType()) {
            case CRM:
                addCrmLink( caseLink );
                break;
            case YT:
                addYtLink( caseLink );
        }
    }

    @Override
    public void onLinksContainerStateChanged(boolean isVisible) {
        storage.set(UiConstants.LINKS_PANEL_VISIBILITY, String.valueOf(isVisible));
        view.setLinksContainerVisible(isVisible);
    }

    private void fillView(List<CaseLink> links) {
        view.getLinksContainer().clear();

        if (CollectionUtils.isEmpty(links)) {
            return;
        }
        linksCount = links.size();
        resetLinksContainerStateByLinksCount();
        links.forEach(this::makeCaseLinkViewAndAddToParent);
        hideOrShowIfNoLinks();
    }

    private void addYtLink( CaseLink caseLink ) {
        caseLinkProvider.getYTLinkInfo( caseLink.getRemoteId(), new FluentCallback<YouTrackIssueInfo>()
                .withError( throwable -> fireEvent(new NotifyEvents.Show(lang.issueLinkIncorrectYouTrackCaseNotFound(caseLink.getRemoteId()), NotifyEvents.NotifyType.ERROR)))
                .withSuccess( youTrackIssueInfo -> {
                    if (youTrackIssueInfo == null) {
                        showError(lang.issueLinkIncorrectYouTrackCaseNotFound(caseLink.getRemoteId()));
                        return;
                    }

                    caseLink.setYouTrackIssueInfo(youTrackIssueInfo);
                    createLinkAndAddToParent(caseLink);
                })
        );
    }

    private void addCrmLink( CaseLink caseLink ) {
        Long crmRemoteId = NumberUtils.parseLong(caseLink.getRemoteId());
        if (crmRemoteId == null) {
            showError(lang.issueLinkIncorrectCrmNumberFormat());
            return;
        }

        caseLinkProvider.getCrmLinkInfo(crmRemoteId, new FluentCallback<CaseInfo>()
                .withError(throwable -> showError(lang.issueLinkIncorrectCrmCaseNotFound(crmRemoteId)))
                .withSuccess(caseInfo -> {
                    if (caseInfo == null) {
                        showError(lang.issueLinkIncorrectCrmCaseNotFound(crmRemoteId));
                        return;
                    }

                    if (Objects.equals(caseLink.getCaseId(), caseInfo.getId())) {
                        showError(lang.errUnableLinkIssueToItself());
                        return;
                    }

                    // для CRM-линков remoteId заполняется идентификатором кейса
                    caseLink.setRemoteId(caseInfo.getId().toString());
                    caseLink.setCaseInfo(caseInfo);
                    createLinkAndAddToParent(caseLink);
                })
        );
    }

    private void createLinkAndAddToParent(CaseLink value) {
        if (isCaseCreationMode()) {
            fireEvent(new CaseLinkEvents.Added(show.caseId, value, pageId));
            addLinkToParentAndModifyLinksCount(value);
            hideOrShowIfNoLinks();
            return;
        }

        controller.createLink(value, createCrossLinks, new FluentCallback<Long>()
                .withError(throwable -> showError(lang.errInternalError()))
                .withSuccess(id -> {
                    value.setId(id);
                    addLinkToParentAndModifyLinksCount(value);
                    hideOrShowIfNoLinks();
                    fireEvent(new NotifyEvents.Show(lang.caseLinkSuccessfulCreated(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }

    private void makeCaseLinkViewAndAddToParent(CaseLink value) {
        AbstractCaseLinkItemView itemWidget = itemViewProvider.get();
        itemWidget.setActivity(this);
        itemWidget.setEnabled(show.isEnabled);
        itemWidget.setModel(value);

        String linkId = null;
        if ( Objects.equals(value.getType(), CRM) && value.getCaseInfo() != null) {
            linkId = String.valueOf(value.getCaseInfo().getCaseNumber());
            itemWidget.setNumber(lang.crmPrefix() + linkId);
            itemWidget.setName(value.getCaseInfo().getName());
            itemWidget.setState(En_CaseState.getById(value.getCaseInfo().getStateId()));
        } else if ( Objects.equals(value.getType(), YT) && value.getYouTrackInfo() != null) {
            linkId = value.getRemoteId();
            itemWidget.setNumber(linkId);
            itemWidget.setName(value.getYouTrackInfo().getSummary());
            itemWidget.setState(value.getYouTrackInfo().getCaseState());
        }

        itemWidget.setHref(caseLinkProvider.getLink(value.getType(), linkId));
        view.getLinksContainer().add(itemWidget.asWidget());
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private void removeLinkViewFromParentAndModifyLinksCount(AbstractCaseLinkItemView itemView) {
        itemView.asWidget().removeFromParent();
        linksCount--;
        resetLinksContainerStateByLinksCount();
    }

    private void addLinkToParentAndModifyLinksCount(CaseLink value) {
        linksCount++;
        makeCaseLinkViewAndAddToParent(value);
        resetLinksContainerStateByLinksCount();
    }

    private void resetLinksContainerStateByLinksCount() {
        view.setLinksContainerVisible(linksCount > 0);
        view.setHeader(lang.linkedWith() + (linksCount == 0 ? "" : " (" + linksCount + ")"));
    }
    private boolean isCaseCreationMode() {
        return show.caseId == null;
    }

    private void hideOrShowIfNoLinks() {
        boolean isEmpty = !view.getLinksContainer().iterator().hasNext();
        view.getContainerVisibility().setVisible(!isEmpty);
    }

    @Inject
    private Lang lang;
    @Inject
    private LocalStorageService storage;
    @Inject
    private CaseLinkControllerAsync controller;
    @Inject
    private AbstractCaseLinkListView view;
    @Inject
    private Provider<AbstractCaseLinkItemView> itemViewProvider;
    @Inject
    private CaseLinkProvider caseLinkProvider;

    private int linksCount = 0;
    private CaseLinkEvents.Show show;
    private String pageId;
    private boolean createCrossLinks;
}
