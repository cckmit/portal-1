package ru.protei.portal.ui.common.client.widget.caselink;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.caselink.item.CaseLinkItem;
import ru.protei.portal.ui.common.client.widget.caselink.popup.CreateCaseLinkPopup;
import ru.protei.portal.ui.common.client.widget.collapse.CollapsiblePanel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;

public class CaseLinkList extends Composite implements HasValue<Set<CaseLink>>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        linksPanel.addClickHandler(event -> localStorageService.set(LINKS_PANEL_BODY, String.valueOf(linksPanel.isPanelBodyVisible())));
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<CaseLink>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("addLinkButton")
    public void addLinkButtonClick(ClickEvent e) {
        if (!enabled || !linksEnabled) {
            return;
        }
        createCaseLinkPopup.showNear(addLinkButton);
        if (linksPopupHandlerRegistration != null) {
            linksPopupHandlerRegistration.removeHandler();
        }
        linksPopupHandlerRegistration = createCaseLinkPopup.addValueChangeHandler(event -> addCaseLink(event.getValue()));
    }

    @Override
    public Set<CaseLink> getValue() {
        return links;
    }

    @Override
    public void setValue(Set<CaseLink> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<CaseLink> value, boolean fireEvents) {
        links = value;

        linkToViewModel.clear();
        linksPanel.clear();
        linksPanel.setPanelBodyVisible(Boolean.parseBoolean(localStorageService.get(LINKS_PANEL_BODY)));

        toggleLinksVisibility(links, linksPanel);

        if (CollectionUtils.isEmpty(links)) {
            return;
        }
        links.forEach(this::makeCaseLinkViewAndAddToParent);

        if ( fireEvents ) {
            ValueChangeEvent.fire(this, links);
        }
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }

    public void setLinksEnabled(boolean enabled) {
        this.linksEnabled = enabled;
        addLinkButton.setVisible(enabled);
    }

    public void showError(String error) {
        activity.fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }


    public void setEnsureDebugLinkId(String debugId) {
        addLinkButton.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdLinkLabel(String debugId) {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        linksPanel.setLabelDebugId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    public void setEnsureDebugIdLinkContainer(String debugId) {
        linksPanel.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdLinkSelector(String debugId) {
        createCaseLinkPopup.setEnsureDebugIdSelector(debugId);
    }

    public void setEnsureDebugIdLinkTextBox(String debugId) {
        createCaseLinkPopup.setEnsureDebugIdTextBox(debugId);
    }

    public void setEnsureDebugIdLinkApply(String debugId) {
        createCaseLinkPopup.setEnsureDebugIdApply(debugId);
    }

    private void makeCaseLinkViewAndAddToParent(CaseLink value) {
        String linkId = isCrmLink(value) ? value.getCaseInfo().getCaseNumber().toString() : value.getRemoteId();
        value.setLink(caseLinkProvider.getLink(value.getType(), linkId));

        CaseLinkItem itemWidget = new CaseLinkItem();
        itemWidget.setEnabled(enabled);
        itemWidget.setValue(value);
        itemWidget.addCloseHandler(event -> removeCaseLink(event.getTarget()));

        linkToViewModel.put(value, itemWidget);
        linksPanel.add(itemWidget);
    }

    private void removeCaseLink(CaseLink item) {
        if (item == null) {
            return;
        }

        links.remove(item);
        CaseLinkItem itemView = linkToViewModel.get(item);
        if (itemView != null) {
            linksPanel.remove(itemView);
        }

        toggleLinksVisibility(links, linksPanel);

        ValueChangeEvent.fire(CaseLinkList.this, links);
    }

    private boolean isCrmLink(CaseLink item) {
        return En_CaseLink.CRM.equals(item.getType());
    }

    private void toggleLinksVisibility(Set set, CollapsiblePanel linksPanel) {
        if (CollectionUtils.isEmpty(set)) {
            linksPanel.addStyleName( UiConstants.Styles.HIDE );
        } else {
            linksPanel.removeStyleName( UiConstants.Styles.HIDE );
        }
    }

    private void addCaseLink(CaseLink caseLink) {
        if (caseLink == null) {
            return;
        }

        switch (caseLink.getType()) {
            case CRM:
                addCrmLink( caseLink );
                break;
            case YT:
                addYtLink( caseLink );
                break;
        }
    }

    private void addYtLink( CaseLink caseLink ) {
        caseLinkProvider.checkExistYtLink( caseLink.getRemoteId(), new FluentCallback<YouTrackIssueInfo>()
                .withError( throwable -> {
                    showError( lang.issueLinkIncorrectYouTrackCaseNotFound( caseLink.getRemoteId() ) );
                } )
                .withSuccess( youTrackIssueInfo -> {
                    if (youTrackIssueInfo == null) {
                        showError( lang.issueLinkIncorrectYouTrackCaseNotFound( caseLink.getRemoteId() ) );
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
        if (links == null) {
            links = new HashSet<>();
        }

        if (links.stream().anyMatch(cl ->
                Objects.equals(cl.getRemoteId(), item.getRemoteId()) &&
                Objects.equals(cl.getType(), item.getType()))
        ) {
            return;
        }

        links.add(item);
        makeCaseLinkViewAndAddToParent(item);
        toggleLinksVisibility(links, linksPanel);

        ValueChangeEvent.fire(CaseLinkList.this, links);
    }

    @Inject
    CaseLinkProvider caseLinkProvider;
    @Inject
    CreateCaseLinkPopup createCaseLinkPopup;
    @Inject
    LocalStorageService localStorageService;

    @Inject
    NotifyActivity activity;
    @Inject
    @UiField
    Lang lang;
    @UiField
    Button addLinkButton;
    @UiField
    CollapsiblePanel linksPanel;

    private boolean enabled = true;
    private boolean linksEnabled = true;
    private boolean showLabel = true;
    private Set<CaseLink> links = null;
    private Map<CaseLink, CaseLinkItem> linkToViewModel = new HashMap<>();
    private HandlerRegistration linksPopupHandlerRegistration;

    private static final String LINKS_PANEL_BODY = "case-link-panel-body";

    interface CaseMetaViewUiBinder extends UiBinder<HTMLPanel, CaseLinkList> {}
    private static CaseMetaViewUiBinder ourUiBinder = GWT.create(CaseMetaViewUiBinder.class);
}
