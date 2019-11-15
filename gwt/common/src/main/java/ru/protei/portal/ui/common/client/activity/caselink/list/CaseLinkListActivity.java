package ru.protei.portal.ui.common.client.activity.caselink.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
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

/**
 * Активность списка линков
 */
public abstract class CaseLinkListActivity
        implements Activity,
        AbstractCaseLinkListActivity, AbstractCaseLinkItemActivity {

    @Inject
    public void onInit() {
        view.setActivity( this );

        controller.getLinkMap(new FluentCallback<Map<En_CaseLink, String>>()
                .withSuccess(map -> linkUrlMap = map));
    }

    @Event
    public void onShow(CaseLinkEvents.Show event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        controller.getCaseLinks(event.caseId, new FluentCallback<List<CaseLink>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(this::fillView)
        );
    }

    @Override
    public void onRemoveClicked(AbstractCaseLinkItemView itemView) {
        if (itemView == null) {
            return;
        }

        controller.removeLink(itemView.getValue(), new FluentCallback<Void>()
                .withSuccess(res -> itemView.asWidget().removeFromParent()));
//        toggleLinksVisibility(links, linksPanel);
    }

    @Override
    public void onAddLinkClicked(CaseLink value) {
        if (value == null) {
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

    private void fillView(List<CaseLink> links) {
        view.getLinksContainer().clear();
        view.setLinksContainerVisible(Boolean.parseBoolean(storage.get(UiConstants.LINKS_PANEL_VISIBILITY)));

//        toggleLinksVisibility(links, linksPanel);
        if (CollectionUtils.isEmpty(links)) {
            return;
        }

        links.forEach(this::makeCaseLinkViewAndAddToParent);
    }

    private void makeCaseLinkViewAndAddToParent(CaseLink value) {
        String linkId = isCrmLink(value) ? value.getCaseInfo().getCaseNumber().toString() : value.getRemoteId();
        value.setLink(caseLinkProvider.getLink(value.getType(), linkId));

        AbstractCaseLinkItemView itemWidget = itemViewProvider.get();
        itemWidget.setActivity(this);
//        itemWidget.setEnabled(enabled);
        itemWidget.setValue(value);

//        linkToViewModel.put(value, itemWidget);
        view.getLinksContainer().add(itemWidget.asWidget());
    }

    private void addYtLink( CaseLink caseLink ) {
        caseLinkProvider.checkExistYtLink( caseLink.getRemoteId(), new FluentCallback<YouTrackIssueInfo>()
                .withError( throwable -> fireEvent(new NotifyEvents.Show(lang.issueLinkIncorrectYouTrackCaseNotFound(caseLink.getRemoteId()), NotifyEvents.NotifyType.ERROR)))
                .withSuccess( youTrackIssueInfo -> {
                    if (youTrackIssueInfo == null) {
                        fireEvent(new NotifyEvents.Show(lang.issueLinkIncorrectYouTrackCaseNotFound(caseLink.getRemoteId()), NotifyEvents.NotifyType.ERROR))
                        return;
                    }

                    caseLink.setYouTrackIssueInfo(youTrackIssueInfo);
                    caseLink.setLink(caseLinkProvider.getLink(caseLink.getType(), caseLink.getRemoteId()));

                    addCaseLinkToList(caseLink);
                } )
        );
    }

    private void addCrmLink( CaseLink caseLink ) {
        Long crmRemoteId;
        try {
            crmRemoteId = Long.parseLong(caseLink.getRemoteId());
        } catch (NumberFormatException ex) {
            showError(lang.issueLinkIncorrectCrmNumberFormat());
            return;
        }

        caseLinkProvider.checkExistCrmLink(crmRemoteId, new FluentCallback<CaseInfo>()
                .withError(throwable -> {
                    showError(lang.issueLinkIncorrectCrmCaseNotFound(crmRemoteId));
                })
                .withSuccess(caseInfo -> {
                    if (caseInfo == null) {
                        showError(lang.issueLinkIncorrectCrmCaseNotFound(crmRemoteId));
                        return;
                    }

                    caseLink.setRemoteId(caseInfo.getId().toString());
                    caseLink.setCaseInfo(caseInfo);
                    caseLink.setLink(caseLinkProvider.getLink(caseLink.getType(), crmRemoteId.toString()));

                    addCaseLinkToList(caseLink);
                })
        );
    }

    private void addCaseLinkToList(CaseLink item) {
        makeCaseLinkViewAndAddToParent(item);

        controller.createLink(item, new FluentCallback<Long>()
                .withSuccess(item::setCaseId));
    }

    private boolean isCrmLink(CaseLink item) {
        return En_CaseLink.CRM.equals(item.getType());
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


    private Map<En_CaseLink, String> linkUrlMap = null;
}
