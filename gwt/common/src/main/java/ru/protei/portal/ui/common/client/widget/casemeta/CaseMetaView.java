package ru.protei.portal.ui.common.client.widget.casemeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselinkprovider.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.casemeta.link.item.CaseLinkView;
import ru.protei.portal.ui.common.client.widget.casemeta.link.popup.CreateCaseLinkPopup;
import ru.protei.portal.ui.common.client.widget.casemeta.model.CaseMeta;
import ru.protei.portal.ui.common.client.widget.casemeta.tag.item.CaseTagView;
import ru.protei.portal.ui.common.client.widget.casemeta.tag.popup.CaseTagSelectorPopup;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;

public class CaseMetaView extends Composite implements HasValueChangeHandlers<CaseMeta>, HasEnabled {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        controlsSection.setVisible(enabled);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseMeta> handler) {
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

    @UiHandler("addTagButton")
    public void addTagButtonClick(ClickEvent e) {
        if (!enabled || !tagsEnabled) {
            return;
        }
        caseTagSelectorPopup.showNear(addTagButton);
        caseTagSelectorPopup.init(tagCaseType);
        if (tagsPopupHandlerRegistration != null) {
            tagsPopupHandlerRegistration.removeHandler();
        }
        tagsPopupHandlerRegistration = caseTagSelectorPopup.addValueChangeHandler(event -> addCaseTag(event.getValue()));
        if (tagsCreateHandlerRegistration != null) {
            tagsCreateHandlerRegistration.removeHandler();
        }
        tagsCreateHandlerRegistration = caseTagSelectorPopup.addAddHandler(event -> {
            CaseTag caseTag = new CaseTag();
            caseTag.setCaseType(tagCaseType);
            activity.fireEvent(new CaseTagEvents.Update(caseTag, true));
        });
        tagsCreateHandlerRegistration = caseTagSelectorPopup.addEditHandler(event -> activity.fireEvent(event.isReadOnly ? new CaseTagEvents.Readonly(event.caseTag) : new CaseTagEvents.Update(event.caseTag, true)));
    }

    @UiHandler("collapse")
    public void onChangeFormStateClicked(ClickEvent event) {
        event.preventDefault();
        boolean isVisible = !linkCollapsiblePanel.getStyleName().contains("expanded");
        localStorageService.set(UiConstants.LINKS_PANEL_VISIBILITY, String.valueOf(isVisible));
        setContainerState(isVisible);
    }

    public Set<CaseLink> getLinks() {
        return links;
    }

    public void setLinks(Set<CaseLink> value) {
        links = value;

        linkToViewModel.clear();
        linksPanel.clear();
        boolean initialState = Boolean.parseBoolean(localStorageService.get(UiConstants.LINKS_PANEL_VISIBILITY));
        setContainerState(initialState);

        toggleLinksVisibility(links, linkCollapsiblePanel);

        if (CollectionUtils.isEmpty(links)) {
            return;
        }
        links.forEach(this::makeCaseLinkViewAndAddToParent);
    }

    public Set<CaseTag> getTags() {
        return tags;
    }

    public void setTags(Set<CaseTag> value) {
        tags = value;

        tagToViewModel.clear();
        tagsContainer.clear();

        toggleTagsVisibility(tags, tagsSection, tagsLabel);

        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        tags.forEach(this::makeCaseTagViewAndAddToParent);
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }

    public void setTagCaseType(En_CaseType tagCaseType) {
        this.tagCaseType = tagCaseType;
    }

    public void setLinksEnabled(boolean enabled) {
        this.linksEnabled = enabled;
        addLinkButton.setVisible(enabled);
    }

    public void setTagsEnabled(boolean enabled) {
        this.tagsEnabled = enabled;
        addTagButton.setVisible(enabled);
    }

    public void setTagsAddButtonEnabled(boolean enabled) {
        caseTagSelectorPopup.setAddTagsEnabled(enabled);
    }

    public void setTagsEditButtonEnabled(boolean enabled) {
        caseTagSelectorPopup.setEditTagsEnabled(enabled);
    }

    public void showError(String error) {
        activity.fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }


    public void setEnsureDebugLinkId(String debugId) {
        addLinkButton.ensureDebugId(debugId);
    }

    public void setEnsureDebugTagId(String debugId) {
        addTagButton.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdLinkLabel(String debugId) {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        headerLabel.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    public void setEnsureDebugIdTagLabel(String debugId) {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        tagsLabel.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    public void setEnsureDebugIdLinkContainer(String debugId) {
        linksPanel.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdTagContainer(String debugId) {
        tagsContainer.ensureDebugId(debugId);
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

    private void setContainerState(boolean isVisible) {
        if (isVisible) {
            linkCollapsiblePanel.getElement().replaceClassName("collapsed", "expanded");
        } else {
            linkCollapsiblePanel.getElement().replaceClassName( "expanded", "collapsed");
        }
    }

    private void makeCaseLinkViewAndAddToParent(CaseLink item) {
        String linkId = isCrmLink(item) ? item.getCaseInfo().getCaseNumber().toString() : item.getRemoteId();
        item.setLink(caseLinkProvider.getLink(item.getType(), linkId));

        CaseLinkView caseLinkView = caseLinkViewProvider.get();
        caseLinkView.setEnabled(enabled);
        caseLinkView.setValue(item);
        caseLinkView.addCloseHandler(event -> removeCaseLink(event.getTarget()));

        linkToViewModel.put(item, caseLinkView);
        linksPanel.add(caseLinkView);
    }

    private void makeCaseTagViewAndAddToParent(CaseTag item) {
        CaseTagView caseTagView = caseTagViewProvider.get();
        caseTagView.setEnabled(enabled);
        caseTagView.setValue(item);
        caseTagView.addCloseHandler(event -> removeCaseTag(event.getTarget()));

        tagToViewModel.put(item, caseTagView);
        tagsContainer.add(caseTagView);
    }

    private void removeCaseLink(CaseLink item) {
        if (item == null) {
            return;
        }

        links.remove(item);
        CaseLinkView itemView = linkToViewModel.get(item);
        if (itemView != null) {
            linksPanel.remove(itemView);
        }

        toggleLinksVisibility(links, linkCollapsiblePanel);
    }

    private void removeCaseTag(CaseTag item) {
        if (item == null) {
            return;
        }

        tags.remove(item);
        CaseTagView itemView = tagToViewModel.get(item);
        if (itemView != null) {
            tagsContainer.remove(itemView);
        }

        toggleTagsVisibility(tags, tagsSection, tagsLabel);
    }

    private boolean isCrmLink(CaseLink item) {
        return En_CaseLink.CRM.equals(item.getType());
    }

    private void toggleLinksVisibility(Set set, HTMLPanel linksPanel) {
        linksPanel.setVisible(CollectionUtils.isNotEmpty(set));
    }

    private void toggleTagsVisibility(Set set, HTMLPanel tagsSection, LabelElement tagsLabel) {
        if (CollectionUtils.isEmpty(set)) {
            tagsSection.addStyleName( UiConstants.Styles.HIDE );
        } else {
            tagsSection.removeStyleName( UiConstants.Styles.HIDE );

            if (showLabel) {
                tagsLabel.removeClassName( UiConstants.Styles.HIDE );
            } else {
                tagsLabel.addClassName( UiConstants.Styles.HIDE );
            }
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
            case CRM_OLD:
                addCaseLinkToList(caseLink);
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
        toggleLinksVisibility(links, linkCollapsiblePanel);

        ValueChangeEvent.fire(CaseMetaView.this, new CaseMeta(links, null));
    }

    private void addCaseTag(CaseTag item) {
        if (tags == null) {
            tags = new HashSet<>();
        }

        if (tags.stream().anyMatch(cl -> Objects.equals(cl, item))) {
            return;
        }

        tags.add(item);
        makeCaseTagViewAndAddToParent(item);
        toggleTagsVisibility(tags, tagsSection, tagsLabel);

        ValueChangeEvent.fire(CaseMetaView.this, new CaseMeta(null, tags));
    }

    @Inject
    CaseLinkProvider caseLinkProvider;
    @Inject
    Provider<CaseLinkView> caseLinkViewProvider;
    @Inject
    Provider<CaseTagView> caseTagViewProvider;
    @Inject
    CreateCaseLinkPopup createCaseLinkPopup;
    @Inject
    CaseTagSelectorPopup caseTagSelectorPopup;
    @Inject
    LocalStorageService localStorageService;

    @Inject
    NotifyActivity activity;
    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel tagsSection;
    @UiField
    LabelElement tagsLabel;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel controlsSection;
    @UiField
    Button addTagButton;
    @UiField
    Button addLinkButton;
    @UiField
    HTMLPanel linksPanel;
    @UiField
    HTMLPanel linkCollapsiblePanel;
    @UiField
    LabelElement headerLabel;

    private boolean enabled = true;
    private boolean linksEnabled = true;
    private boolean tagsEnabled = true;
    private boolean showLabel = true;
    private Set<CaseLink> links = null;
    private Set<CaseTag> tags = null;
    private Map<CaseLink, CaseLinkView> linkToViewModel = new HashMap<>();
    private Map<CaseTag, CaseTagView> tagToViewModel = new HashMap<>();
    private En_CaseType tagCaseType;
    private HandlerRegistration linksPopupHandlerRegistration;
    private HandlerRegistration tagsPopupHandlerRegistration;
    private HandlerRegistration tagsCreateHandlerRegistration;

    interface CaseMetaViewUiBinder extends UiBinder<HTMLPanel, CaseMetaView> {}
    private static CaseMetaViewUiBinder ourUiBinder = GWT.create(CaseMetaViewUiBinder.class);
}
