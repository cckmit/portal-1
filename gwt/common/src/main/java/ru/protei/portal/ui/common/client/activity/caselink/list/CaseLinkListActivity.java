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
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;

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
        isEnabled = event.isEnabled;

        event.parent.clear();
        event.parent.add(view.asWidget());

        view.getLinksContainer().clear();
        view.setLinksContainerVisible(Boolean.parseBoolean(storage.get(UiConstants.LINKS_PANEL_VISIBILITY)));
        view.addButtonEnabled().setEnabled(event.isEnabled);

        controller.getCaseLinks(event.caseId, new FluentCallback<List<CaseLink>>()
                .withError(throwable -> showError(lang.errGetList()))
                .withSuccess(this::fillView)
        );
    }

    @Override
    public void onRemoveClicked(AbstractCaseLinkItemView itemView) {
        if (itemView == null || !isEnabled) {
            return;
        }

        controller.removeLink(itemView.getModelId(), new FluentCallback<Void>()
                .withSuccess(res -> itemView.asWidget().removeFromParent()));
        toggleLinksVisibility();
    }

    @Override
    public void onAddLinkClicked(CaseLink value) {
        if (value == null || !isEnabled) {
            return;
        }

        switch (value.getType()) {
            case CRM:
                addCrmLink( value );
                break;
            case YT:
                addYtLink( value );
        }
    }

    @Override
    public void onLinksContainerStateChanged(boolean isVisible) {
        storage.set(UiConstants.LINKS_PANEL_VISIBILITY, String.valueOf(isVisible));
        view.setLinksContainerVisible(isVisible);
    }

    private void fillView(List<CaseLink> links) {
        this.linksCount = links == null ? 0 : links.size();
        view.setHeader(lang.linkedWith() + " (" + linksCount + ")");
        view.getLinksContainer().clear();

        toggleLinksVisibility();
        if (CollectionUtils.isEmpty(links)) {
            return;
        }
        links.forEach(this::makeCaseLinkViewAndAddToParent);
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
                    caseLink.setLink(caseLinkProvider.getLink(caseLink.getType(), caseLink.getRemoteId()));

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

                    caseLink.setCaseInfo(caseInfo);
                    caseLink.setLink(caseLinkProvider.getLink(caseLink.getType(), crmRemoteId.toString()));

                    createLinkAndAddToParent(caseLink);
                })
        );
    }

    private void createLinkAndAddToParent(CaseLink value) {
        controller.createLink(value, new FluentCallback<Long>()
                .withError(throwable -> showError(lang.errInternalError()))
                .withSuccess(id -> {
                    value.setCaseId(id);
                    linksCount++;
                    makeCaseLinkViewAndAddToParent(value);
                    toggleLinksVisibility();
                }));
    }

    private void makeCaseLinkViewAndAddToParent(CaseLink value) {
        AbstractCaseLinkItemView itemWidget = itemViewProvider.get();
        itemWidget.setActivity(this);
        itemWidget.setEnabled(isEnabled);
        itemWidget.setModelId(value.getId());
        itemWidget.setHref(value.getLink());

        String linkId = null;
        if ( Objects.equals(value.getType(), CRM) && value.getCaseInfo() != null) {
            linkId = String.valueOf(value.getCaseInfo().getCaseNumber());
            itemWidget.setNumber(lang.crmPrefix() + linkId);
            itemWidget.setName(value.getCaseInfo().getName());
            itemWidget.setState(En_CaseState.getById(value.getCaseInfo().getStateId()));
        }

        if ( Objects.equals(value.getType(), YT) && value.getYouTrackInfo() != null) {
            linkId = value.getRemoteId();
            itemWidget.setNumber(linkId);
            itemWidget.setName(value.getYouTrackInfo().getSummary());
            itemWidget.setState(value.getYouTrackInfo().getCaseState());
        }

        value.setLink(caseLinkProvider.getLink(value.getType(), linkId));
        view.getLinksContainer().add(itemWidget.asWidget());
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private void toggleLinksVisibility() {
        view.setLinksContainerVisible(linksCount > 0);
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

    private boolean isEnabled = true;
    private int linksCount = 0;

}
