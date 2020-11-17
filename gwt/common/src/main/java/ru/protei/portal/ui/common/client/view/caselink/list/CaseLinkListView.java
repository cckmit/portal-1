package ru.protei.portal.ui.common.client.view.caselink.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselink.list.AbstractCaseLinkListActivity;
import ru.protei.portal.ui.common.client.activity.caselink.list.AbstractCaseLinkListView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.widget.accordion.AccordionWidget;
import ru.protei.portal.ui.common.client.widget.caselink.popup.CreateCaseLinkPopup;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;

public class CaseLinkListView
        extends Composite
        implements AbstractCaseLinkListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        createCaseLinkPopup.addValueChangeHandler(event -> activity.onAddLinkClicked(event.getValue()));

        accordionWidget.setLocalStorageKey(UiConstants.LINKS_PANEL_VISIBILITY);
        accordionWidget.setMaxHeight(UiConstants.Accordion.LINKS_MAX_HEIGHT);

        initDebugIds();
    }

    @Override
    public void setActivity(AbstractCaseLinkListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void showSelector(En_CaseType caseType, IsWidget target) {
        createCaseLinkPopup.resetValueAndShow(caseType, target.asWidget());
    }

    @Override
    public HasVisibility getContainerVisibility() {
        return root;
    }

    @Override
    public void setCountOfLinks(String tabName, String count) {
        accordionWidget.setBadge(tabName, count);
    }

    @Override
    public void addTabWidgetPane(TabWidgetPane tabWidgetPane) {
        accordionWidget.add(tabWidgetPane);
    }

    @Override
    public void tabVisibility(String tabName, boolean isVisible) {
        accordionWidget.tabVisibility(tabName).setVisible(isVisible);
    }

    @Override
    public void resetTabs() {
        accordionWidget.selectFirstTab();
    }

    @Override
    public void setTabNameDebugId(String tabName, String debugId) {
        accordionWidget.setTabNameDebugId(tabName, debugId);
    }

    private void initDebugIds() {
        accordionWidget.setCollapseButtonDebugId(DebugIds.ISSUE.LINKS_COLLAPSE_BUTTON);
        createCaseLinkPopup.setEnsureDebugIdSelector(DebugIds.ISSUE.LINKS_TYPE_SELECTOR);
        createCaseLinkPopup.setEnsureDebugIdTextBox(DebugIds.ISSUE.LINKS_INPUT);
        createCaseLinkPopup.setEnsureDebugIdApply(DebugIds.ISSUE.LINKS_APPLY_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    AccordionWidget accordionWidget;
    @Inject
    CreateCaseLinkPopup createCaseLinkPopup;

    private AbstractCaseLinkListActivity activity;

    private static CaseLinkListUiBinder ourUiBinder = GWT.create(CaseLinkListUiBinder.class);
    interface CaseLinkListUiBinder extends UiBinder<HTMLPanel, CaseLinkListView> {}
}
