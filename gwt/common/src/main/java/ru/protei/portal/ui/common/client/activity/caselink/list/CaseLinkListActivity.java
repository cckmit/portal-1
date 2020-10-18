package ru.protei.portal.ui.common.client.activity.caselink.list;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_BundleType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemActivity;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;
import ru.protei.portal.ui.common.client.events.CaseLinkEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_BundleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseLinkControllerAsync;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        initTabs();
    }

    @Event
    public void onShow(CaseLinkEvents.Show event) {
        this.show = event;
        caseId = event.caseId;
        caseType = event.caseType;

        event.parent.clear();
        event.parent.add(view.asWidget());

        bundleTypeToCaseLink.clear();
        bundleTypeToPanel.values().forEach(Panel::clear);

        hideOrShowIfNoLinks();
        resetLinksContainerStateByLinksCount();

        boolean isCaseLinksDefined = event.links != null;
        if (isCaseLinksDefined) {
            fillView(CollectionUtils.emptyIfNull(event.links));
            return;
        }

        if (isCaseCreationMode()) {
            return;
        }

        controller.getCaseLinks(caseId, new FluentCallback<List<CaseLink>>()
                .withError(this::showErrorFromServer)
                .withSuccess(this::fillView)
        );
    }

    @Event
    public void onShowLinkSelector(CaseLinkEvents.ShowLinkSelector event) {
        if (!show.isEnabled) {
            return;
        }

        this.caseType = event.caseType;

        view.showSelector(event.target);
    }

    @Override
    public void onRemoveClicked(AbstractCaseLinkItemView itemView) {
        if (itemView == null || !show.isEnabled) {
            return;
        }

        if (isCaseCreationMode()) {
            fireEvent(new CaseLinkEvents.Removed(show.caseId, itemView.getModel(), caseType));
            removeLinkViewFromParentAndModifyLinksCount(itemView);
            hideOrShowIfNoLinks();
            return;
        }

        controller.deleteLinkWithPublish(itemView.getModel().getId(), caseType, new FluentCallback<Void>()
                .withSuccess(res -> {
                    removeLinkViewFromParentAndModifyLinksCount(itemView);
                    hideOrShowIfNoLinks();
                    fireEvent(new NotifyEvents.Show(lang.caseLinkSuccessfulRemoved(), NotifyEvents.NotifyType.SUCCESS));
                })
        );
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

    private void initTabs() {
        Arrays.asList(En_BundleType.values()).forEach(bundleType -> {
            TabWidgetPane tabWidgetPane = tabWidgetPaneProvider.get();
            tabWidgetPane.setTabName(bundleTypeLang.getName(bundleType));
            HTMLPanel panel = new HTMLPanel("ul","");
            panel.addStyleName("case-links");
            tabWidgetPane.add(panel);
            view.addTabWidgetPane(tabWidgetPane);
            bundleTypeToPanel.put(bundleType, panel);
        });
    }

    private void fillView(List<CaseLink> links) {
        if (CollectionUtils.isEmpty(links) || !Objects.equals(links.iterator().next().getCaseId(), caseId)) {
            return;
        }

        bundleTypeToCaseLink = links.stream().collect(Collectors.groupingBy(CaseLink::getBundleType));
        bundleTypeToCaseLink.forEach((bundleType, caseLinks) -> {
            caseLinks.forEach(caseLink -> makeCaseLinkViewAndAddToParent(caseLink, bundleTypeToPanel.get(bundleType)));
            resetLinksContainerStateByLinksCount();
        });

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
        //@ToDo check this
        if (bundleTypeToCaseLink.values().contains(value)) {
            fireEvent(new NotifyEvents.Show(lang.errCaseLinkAlreadyAdded(), NotifyEvents.NotifyType.ERROR));
            return;
        }
        if (isCaseCreationMode()) {
            fireEvent(new CaseLinkEvents.Added(show.caseId, value, caseType));
            addLinkToParentAndModifyLinksCount(value);
            hideOrShowIfNoLinks();
            return;
        }

        controller.createLinkWithPublish(value, caseType, new FluentCallback<CaseLink>()
                .withError(this::showErrorFromServer)
                .withSuccess(caseLink -> {
                    value.setId(caseLink.getId());
                    value.setCaseInfo(caseLink.getCaseInfo());
                    addLinkToParentAndModifyLinksCount(value);
                    hideOrShowIfNoLinks();
                    fireEvent(new NotifyEvents.Show(lang.caseLinkSuccessfulCreated(), NotifyEvents.NotifyType.SUCCESS));
                })
        );
    }

    private void makeCaseLinkViewAndAddToParent(CaseLink value, HTMLPanel panel) {
        AbstractCaseLinkItemView itemWidget = itemViewProvider.get();
        itemWidget.setActivity(this);
        itemWidget.setEnabled(show.isEnabled);
        itemWidget.setModel(value);

        String linkId = null;
        if (Objects.equals(value.getType(), CRM) && value.getCaseInfo() != null) {
            linkId = String.valueOf(value.getCaseInfo().getCaseNumber());
            itemWidget.setNumber(lang.crmPrefix() + linkId);
            itemWidget.setName(value.getCaseInfo().getName());
            itemWidget.setState(value.getCaseInfo().getState());
        } else if (Objects.equals(value.getType(), YT) && value.getYouTrackInfo() != null) {
            linkId = value.getRemoteId();
            itemWidget.setNumber(linkId);
            itemWidget.setName(value.getYouTrackInfo().getSummary());
            itemWidget.setState(value.getYouTrackInfo().getState());
        } else {
            itemWidget.setName(value.getRemoteId());
            itemWidget.setNumber(lang.errCaseLinkNotFound());
        }

        itemWidget.setHref(caseLinkProvider.getLink(value.getType(), linkId));
        panel.add(itemWidget.asWidget());
    }

    private void showErrorFromServer(Throwable throwable) {
        defaultErrorHandler.accept(throwable);
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private void removeLinkViewFromParentAndModifyLinksCount(AbstractCaseLinkItemView itemView) {
        itemView.asWidget().removeFromParent();
        CaseLink caseLink = itemView.getModel();
        En_BundleType bundleType = caseLink.getBundleType();
        bundleTypeToCaseLink.get(bundleType).remove(caseLink);
        bundleTypeToPanel.get(bundleType).remove(itemView);
        resetLinksContainerStateByLinksCount();
    }

    private void addLinkToParentAndModifyLinksCount(CaseLink value) {
        bundleTypeToCaseLink.get(value.getBundleType()).add(value);
        makeCaseLinkViewAndAddToParent(value, bundleTypeToPanel.get(value.getBundleType()));
        resetLinksContainerStateByLinksCount();
    }

    //@ToDo set badge
    private void resetLinksContainerStateByLinksCount() {
        //view.setHeader(lang.linkedWith() + (linksSet.size() == 0 ? "" : " (" + linksSet.size() + ")"));
    }

    private boolean isCaseCreationMode() {
        return show.caseId == null;
    }

    private void hideOrShowIfNoLinks() {
        boolean isPresent = bundleTypeToPanel.values().stream().filter(panel -> panel.iterator().hasNext()).findAny().isPresent();
        view.getContainerVisibility().setVisible(isPresent);
        bundleTypeToPanel.forEach((bundleType, panel) -> {
            view.tabVisibility(bundleTypeLang.getName(bundleType), panel.iterator().hasNext());
        });
    }

    @Inject
    private Lang lang;
    @Inject
    private CaseLinkControllerAsync controller;
    @Inject
    private AbstractCaseLinkListView view;
    @Inject
    private Provider<AbstractCaseLinkItemView> itemViewProvider;
    @Inject
    private CaseLinkProvider caseLinkProvider;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    Provider<TabWidgetPane> tabWidgetPaneProvider;
    @Inject
    En_BundleTypeLang bundleTypeLang;

    private En_CaseType caseType;
    Map<En_BundleType, List<CaseLink>> bundleTypeToCaseLink = new HashMap<>();
    Map<En_BundleType, HTMLPanel> bundleTypeToPanel = new HashMap<>();
    private CaseLinkEvents.Show show;
    private Long caseId;
}
