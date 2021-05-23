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
import ru.protei.portal.core.model.ent.UitsIssueInfo;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemActivity;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;
import ru.protei.portal.ui.common.client.events.CaseLinkEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_BundleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseLinkControllerAsync;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dict.En_CaseLink.*;

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
                .withSuccess(this::fillView)
        );
    }

    @Event
    public void onShowLinkSelector(CaseLinkEvents.ShowLinkSelector event) {
        if (!show.isEnabled) {
            return;
        }

        this.caseType = event.caseType;

        view.showSelector(event.caseType, event.target);
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

        controller.deleteLinkWithPublish(itemView.getModel().getId(), caseType, new FluentCallback<CaseLink>()
                .withSuccess(caseLink -> {
                    removeLinkViewFromParentAndModifyLinksCount(itemView);
                    hideOrShowIfNoLinks();
                    fireEvent(new NotifyEvents.Show(lang.caseLinkSuccessfulRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CaseLinkEvents.Changed(caseLink, caseType));
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
                break;
            case UITS:
                addUitsLink( caseLink );
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

            String bundleTypePrefix = getBundleTypePrefix(bundleType);
            tabWidgetPane.setTabDebugId(DebugIds.ISSUE.LINKS_CONTAINER + bundleTypePrefix);
            view.setTabNameDebugId(tabWidgetPane.getTabName(), DebugIds.ISSUE.LABEL.LINKS + bundleTypePrefix);
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
        view.selectFirstTab();
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

    private void addUitsLink( CaseLink caseLink ) {
        Long uitsRemoteId = NumberUtils.parseLong(caseLink.getRemoteId());
        if (uitsRemoteId == null) {
            showError(lang.issueLinkIncorrectUitsNumberFormat());
            return;
        }

        caseLinkProvider.getUitsLinkInfo(uitsRemoteId, new FluentCallback<UitsIssueInfo>()
                .withError(throwable -> showError(lang.issueLinkIncorrectUitsCaseNotFound(uitsRemoteId)))
                .withSuccess(requestInfo -> {
                    if (requestInfo == null) {
                        showError(lang.issueLinkIncorrectUitsCaseNotFound(uitsRemoteId));
                        return;
                    }

                    caseLink.setUitsIssueInfo(requestInfo);
                    createLinkAndAddToParent(caseLink);
                })
        );
    }

    private void createLinkAndAddToParent(CaseLink value) {
        if (bundleTypeToCaseLink.containsValue(value)) {
            fireEvent(new NotifyEvents.Show(lang.errCaseLinkAlreadyAdded(), NotifyEvents.NotifyType.ERROR));
            return;
        }
        if (isCaseCreationMode()) {
            fireEvent(new CaseLinkEvents.Added(show.caseId, value, caseType));
            addLinkToParentAndModifyLinksCount(value);
            hideOrShowIfNoLinks();
            view.selectTab(bundleTypeLang.getName(value.getBundleType()));
            return;
        }

        controller.createLinkWithPublish(value, caseType, new FluentCallback<CaseLink>()
                .withSuccess(caseLink -> {
                    value.setId(caseLink.getId());
                    value.setCaseInfo(caseLink.getCaseInfo());
                    addLinkToParentAndModifyLinksCount(value);
                    hideOrShowIfNoLinks();
                    view.selectTab(bundleTypeLang.getName(value.getBundleType()));
                    fireEvent(new NotifyEvents.Show(lang.caseLinkSuccessfulCreated(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CaseLinkEvents.Changed(value, caseType));
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
        } else if (Objects.equals(value.getType(), UITS) && value.getUitsIssueInfo() != null) {
            linkId = value.getRemoteId();
            itemWidget.setNumber(lang.uitsPrefix() + linkId);
            itemWidget.setName(value.getUitsIssueInfo().getSummary());
            itemWidget.setState(value.getUitsIssueInfo().getState());
        } else {
            itemWidget.setName(value.getRemoteId());
            itemWidget.setNumber(lang.errCaseLinkNotFound());
        }

        itemWidget.setHref(caseLinkProvider.getLink(value.getType(), linkId));
        panel.add(itemWidget.asWidget());
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
        bundleTypeToCaseLink.computeIfAbsent(value.getBundleType(), bundleType -> new ArrayList<>()).add(value);
        makeCaseLinkViewAndAddToParent(value, bundleTypeToPanel.get(value.getBundleType()));
        resetLinksContainerStateByLinksCount();
    }

    private void resetLinksContainerStateByLinksCount() {
        bundleTypeToCaseLink.forEach((bundleType, links) ->
            view.setCountOfLinks(bundleTypeLang.getName(bundleType), String.valueOf(links.size())));
    }

    private boolean isCaseCreationMode() {
        return show.caseId == null;
    }

    private void hideOrShowIfNoLinks() {
        boolean isPresent = bundleTypeToPanel.values().stream().anyMatch(panel -> panel.iterator().hasNext());
        view.getContainerVisibility().setVisible(isPresent);
        bundleTypeToPanel.forEach((bundleType, panel) -> {
            view.tabVisibility(bundleTypeLang.getName(bundleType), panel.iterator().hasNext());
        });
    }

    private String getBundleTypePrefix(En_BundleType bundleType) {
        return "-" + bundleType.name().replace("_", "-").toLowerCase();
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
    Provider<TabWidgetPane> tabWidgetPaneProvider;
    @Inject
    En_BundleTypeLang bundleTypeLang;

    private En_CaseType caseType;
    Map<En_BundleType, List<CaseLink>> bundleTypeToCaseLink = new HashMap<>();
    Map<En_BundleType, HTMLPanel> bundleTypeToPanel = new HashMap<>();
    private CaseLinkEvents.Show show;
    private Long caseId;
}
