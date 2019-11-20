package ru.protei.portal.ui.common.client.widget.casemeta;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.notify.NotifyActivity;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.caselink.popup.CreateCaseLinkPopup;
import ru.protei.portal.ui.common.client.widget.casemeta.model.CaseMeta;
import ru.protei.portal.ui.common.client.widget.casemeta.tag.item.CaseTagView;
import ru.protei.portal.ui.common.client.widget.casemeta.tag.popup.CaseTagSelectorPopup;

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
        tagsCreateHandlerRegistration = caseTagSelectorPopup.addEditHandler(event -> {
            activity.fireEvent(event.isReadOnly ? new CaseTagEvents.Readonly(event.caseTag) : new CaseTagEvents.Update(event.caseTag, true));
        });
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

    public void setTagsAddButtonEnabled(boolean enabled) {
        caseTagSelectorPopup.setAddTagsEnabled(enabled);
    }

    public void setTagsEditButtonEnabled(boolean enabled) {
        caseTagSelectorPopup.setEditTagsEnabled(enabled);
    }

    public void setEnsureDebugTagId(String debugId) {
        addTagButton.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdTagLabel(String debugId) {
        tagsLabel.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }


    public void setEnsureDebugIdTagContainer(String debugId) {
        tagsContainer.ensureDebugId(debugId);
    }

    private void makeCaseTagViewAndAddToParent(CaseTag item) {
        CaseTagView caseTagView = caseTagViewProvider.get();
        caseTagView.setEnabled(enabled);
        caseTagView.setValue(item);
        caseTagView.addCloseHandler(event -> removeCaseTag(event.getTarget()));

        tagToViewModel.put(item, caseTagView);
        tagsContainer.add(caseTagView);
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

    private void toggleTagsVisibility(Set set, HTMLPanel tagsSection, LabelElement tagsLabel) {
        if (CollectionUtils.isEmpty(set)) {
            tagsSection.addStyleName( HIDE );
        } else {
            tagsSection.removeStyleName( HIDE );

            if (showLabel) {
                tagsLabel.removeClassName( HIDE );
            } else {
                tagsLabel.addClassName( HIDE );
            }
        }
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

    private boolean enabled = true;
    private boolean tagsEnabled = true;
    private boolean showLabel = true;
    private Set<CaseTag> tags = null;
    private Map<CaseTag, CaseTagView> tagToViewModel = new HashMap<>();
    private En_CaseType tagCaseType;
    private HandlerRegistration tagsPopupHandlerRegistration;
    private HandlerRegistration tagsCreateHandlerRegistration;

    public static final String HIDE = "hide";

    interface CaseMetaViewUiBinder extends UiBinder<HTMLPanel, CaseMetaView> {}
    private static CaseMetaViewUiBinder ourUiBinder = GWT.create(CaseMetaViewUiBinder.class);
}
