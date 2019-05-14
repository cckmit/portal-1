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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselinkprovider.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
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

    public Set<CaseLink> getLinks() {
        return links;
    }

    public void setLinks(Set<CaseLink> value) {
        links = value;

        linkToViewModel.clear();
        linksContainer.clear();

        toggleLinksVisibility();

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

        toggleTagsVisibility();

        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        tags.forEach(this::makeCaseTagViewAndAddToParent);
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

    private void makeCaseLinkViewAndAddToParent(CaseLink item) {
        String linkId = isCrmLink(item) ? item.getCaseInfo().getCaseNumber().toString() : item.getRemoteId();
        item.setLink(caseLinkProvider.getLink(item.getType(), linkId));

        CaseLinkView caseLinkView = caseLinkViewProvider.get();
        caseLinkView.setEnabled(enabled);
        caseLinkView.setValue(item);
        caseLinkView.addCloseHandler(event -> removeCaseLink(event.getTarget()));

        linkToViewModel.put(item, caseLinkView);
        linksContainer.add(caseLinkView);
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
            linksContainer.remove(itemView);
        }

        toggleLinksVisibility();
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

        toggleTagsVisibility();
    }

    private boolean isCrmLink(CaseLink item) {
        return En_CaseLink.CRM.equals(item.getType());
    }

    private void toggleLinksVisibility() {
        toggleVisibility(links, linksSection, linksLabel);
    }

    private void toggleTagsVisibility() {
        toggleVisibility(tags, tagsSection, tagsLabel);
    }

    private void toggleVisibility(Set set, HTMLPanel container, LabelElement label) {
        if (CollectionUtils.isEmpty(set)) {
            container.addStyleName("hide");
        } else {
            container.removeStyleName("hide");
            if (showLabel) {
                label.removeClassName("hide");
            } else {
                label.addClassName("hide");
            }
        }
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
            activity.fireEvent(new CaseTagEvents.Create(tagCaseType));
        });
    }

    private void addCaseLink(CaseLink item) {

        if (item == null) {
            return;
        }

        if (!isCrmLink(item)) {
            addCaseLinkToList(item);
            return;
        }

        Long crmRemoteId;
        try {
            crmRemoteId = Long.parseLong(item.getRemoteId());
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

                    item.setRemoteId(caseInfo.getId().toString());
                    item.setCaseInfo(caseInfo);
                    item.setLink(caseLinkProvider.getLink(item.getType(), crmRemoteId.toString()));

                    addCaseLinkToList(item);
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
        toggleLinksVisibility();

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
        toggleTagsVisibility();

        ValueChangeEvent.fire(CaseMetaView.this, new CaseMeta(null, tags));
    }

    public void showError(String error) {
        activity.fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseMeta> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }


    public void setEnsureDebugId(String debugId) {
        addLinkButton.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdLabel(String debugId) {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        linksLabel.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    public void setEnsureDebugIdContainer(String debugId) {
        linksContainer.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdSelector(String debugId) {
        createCaseLinkPopup.setEnsureDebugIdSelector(debugId);
    }

    public void setEnsureDebugIdTextBox(String debugId) {
        createCaseLinkPopup.setEnsureDebugIdTextBox(debugId);
    }

    public void setEnsureDebugIdApply(String debugId) {
        createCaseLinkPopup.setEnsureDebugIdApply(debugId);
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
    NotifyActivity activity;

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel linksSection;
    @UiField
    LabelElement linksLabel;
    @UiField
    HTMLPanel linksContainer;
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
