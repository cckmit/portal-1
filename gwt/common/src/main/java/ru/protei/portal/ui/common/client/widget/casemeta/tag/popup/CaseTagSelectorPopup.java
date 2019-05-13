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
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseTagController;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.client.widget.casemeta.tag.item.CaseTagView;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.popup.PopupRightAligned;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Set;

public class CaseTagSelectorPopup extends PopupRightAligned implements HasValueChangeHandlers<CaseTag>, HasAddHandlers {

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
    protected Panel getRoot() {
        return root;
    }

    public void init(En_CaseType caseType) {
        resetSearchFilter();
        caseTagController.getCaseTagsForCaseType(caseType, new FluentCallback<List<CaseTag>>()
                .withSuccess((tags, m) -> {
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

    private void resetSearchFilter() {
        searchNameFilter = "";
        search.setValue(searchNameFilter);
        search.setFocus(true);
    }

    private void displayTags() {
        clearTagsListView();
        caseTags.stream()
                .filter(caseTag -> containsIgnoreCase(caseTag.getName(), searchNameFilter))
                .forEach(this::addTagToListView);
    }

    private void clearTagsListView() {
        childContainer.clear();
    }

    private void addTagToListView(CaseTag caseTag) {
        CaseTagView caseTagView = caseTagViewProvider.get();
        caseTagView.setEnabled(false);
        caseTagView.setValue(caseTag);
        caseTagView.addAddHandler(event -> {
            onTagSelected(caseTag);
        });
        childContainer.add(caseTagView);
    }

    private void onTagSelected(CaseTag caseTag) {
        ValueChangeEvent.fire(this, caseTag);
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
    Provider<CaseTagView> caseTagViewProvider;

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

    private String searchNameFilter = "";
    private List<CaseTag> caseTags;

    interface CaseTagSelectorPopupUiBinder extends UiBinder<HTMLPanel, CaseTagSelectorPopup> {}
    private static CaseTagSelectorPopupUiBinder ourUiBinder = GWT.create(CaseTagSelectorPopupUiBinder.class);
}
