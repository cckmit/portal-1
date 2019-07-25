package ru.protei.portal.ui.common.client.widget.casemeta.tag.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.client.widget.casemeta.tag.item.CaseTagPopupView;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.popup.PopupRightAligned;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Objects;

public class CaseTagSelectorPopup extends PopupRightAligned implements HasValueChangeHandlers<CaseTag>, HasAddHandlers, HasEditHandlers {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        super.init();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseTag> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @Override
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
    }

    @Override
    protected Panel getRoot() {
        return root;
    }

    public void init(En_CaseType caseType) {
        resetSearchFilter();
        caseTagController.getCaseTagsForCaseType(caseType, new FluentCallback<List<CaseTag>>()
                .withSuccess(tags -> {
                    caseTags = tags;
                    displayTags();
                })
        );
    }

    @UiHandler("search")
    public void onSearchChanged(ValueChangeEvent<String> event) {
        searchNameFilter = event.getValue();
        displayTags();
    }

    @UiHandler("addButton")
    public void addButtonClick(ClickEvent event) {
        AddEvent.fire(this);
        hide();
    }

    public void setAddTagsEnabled(boolean enabled) {
        if (enabled) {
            addButton.getElement().removeClassName("hide");
        } else {
            addButton.getElement().addClassName("hide");
        }
    }

    public void setEditTagsEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void resetSearchFilter() {
        searchNameFilter = "";
        search.setValue(searchNameFilter);
        search.setFocus(true);
    }

    private void displayTags() {
        boolean isGranted =  policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW);
        clearTagsListView();
        caseTags.stream()
                .filter(caseTag -> containsIgnoreCase(caseTag.getName(), searchNameFilter) || isGranted ? containsIgnoreCase(caseTag.getCompanyName(), searchNameFilter) : false)
                .forEach(this::addTagToListView);
    }

    private void clearTagsListView() {
        childContainer.clear();
    }

    private void addTagToListView(CaseTag caseTag) {
        CaseTagPopupView caseTagPopupView = caseTagViewProvider.get();
        caseTagPopupView.setValue(caseTag);
        caseTagPopupView.editIconVisibility().setVisible(enabled);
        caseTagPopupView.tagEditable(Objects.equals(policyService.getProfile().getId(), caseTag.getPersonId()));
        caseTagPopupView.addAddHandler(event -> {
            onTagSelected(caseTag);
        });
        caseTagPopupView.addEditHandler(event -> {
            onTagEdit(caseTag);
        });
        childContainer.add(caseTagPopupView);
    }

    private void onTagSelected(CaseTag caseTag) {
        ValueChangeEvent.fire(this, caseTag);
        hide();
    }

    private void onTagEdit(CaseTag caseTag) {
        EditEvent.fire(this, caseTag);
        hide();
    }

    private boolean containsIgnoreCase(String test, String sub) {
        if (StringUtils.isBlank(test) || sub == null) {
            return false;
        }
        return test.toLowerCase().contains(sub.toLowerCase());
    }

    @Inject
    CaseTagControllerAsync caseTagController;
    @Inject
    Provider<CaseTagPopupView> caseTagViewProvider;

    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField
    CleanableSearchBox search;
    @UiField
    Button addButton;
    @UiField
    HTMLPanel childContainer;

    @Inject
    PolicyService policyService;

    private String searchNameFilter = "";
    private List<CaseTag> caseTags;
    private boolean enabled;

    interface CaseTagSelectorPopupUiBinder extends UiBinder<HTMLPanel, CaseTagSelectorPopup> {}
    private static CaseTagSelectorPopupUiBinder ourUiBinder = GWT.create(CaseTagSelectorPopupUiBinder.class);
}
