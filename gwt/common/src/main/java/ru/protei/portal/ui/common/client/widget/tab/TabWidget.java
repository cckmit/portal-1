package ru.protei.portal.ui.common.client.widget.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable.ArrowSelectableSelectorPopup;
import ru.protei.portal.ui.common.client.widget.tab.base.Tab;
import ru.protei.portal.ui.common.client.widget.tab.navitem.TabWidgetNavItem;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;

import java.util.HashMap;
import java.util.Map;

public class TabWidget extends Tab {

    public TabWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        popup.setSearchHandler(null);
        popup.setAddButtonVisibility(false);
        navDropdownTabsSelectedContainer.add(popup);
    }

    public void setTabContentStyleName(String styleName) {
        tabContent.setStyleName(styleName);
    }

    @Override
    public void onTabClicked(String tabName) {
        onTabSelected(tabName);
    }

    @Override
    protected HTMLPanel getContainer() {
        return tabContent;
    }

    @Override
    public boolean remove(Widget widget) {
        if (widget instanceof TabWidgetPane) {
            TabWidgetPane pane = (TabWidgetPane) widget;
            tabNameToPane.remove(pane.getTabName());
            tabNameToNavItem.remove(pane.getTabName());
            popup.getContainer().remove(tabNameToNavSelectorItem.remove(pane.getTabName()));
        }
        return tabContent.remove(widget);
    }

    @Override
    protected void addTab(TabWidgetPane pane) {

        TabWidgetNavItem navItem = makeNavItem(pane);
        SelectorItem selectorItem = makeSelectorItem(pane);

        popup.getContainer().add(selectorItem.asWidget());
        tabNameToNavSelectorItem.put(pane.getTabName(), selectorItem);
        navTabs.add(navItem);
        tabNameToNavItem.put(pane.getTabName(), navItem);
        tabNameToPane.put(pane.getTabName(), pane);

        selectTabIfNeeded(pane);
    }

    @Override
    protected void clearTabs() {
        tabContent.clear();
        navTabs.clear();
        tabNameToNavSelectorItem.clear();
        popup.getContainer().clear();
    }

    @Override
    protected void onTabSelected(String tabName) {
        super.onTabSelected(tabName);
        setNavItemDropdownSelected(tabName);
    }

    @UiHandler("navDropdownTabsSelected")
    public void navDropdownTabsSelectedClick(ClickEvent event) {
        event.preventDefault();
        popup.showNear(navDropdownTabsSelected.getElement());
    }

    private TabWidgetNavItem makeNavItem(TabWidgetPane pane) {
        TabWidgetNavItem navItem = new TabWidgetNavItem();
        navItem.setTabName(pane.getTabName());
        navItem.setTabIcon(pane.getTabIcon());
        navItem.setActivity(this);
        navItem.setInactive();
        return navItem;
    }

    private SelectorItem makeSelectorItem(TabWidgetPane pane) {
        SelectorItem selectorItem = new SelectorItem();
        selectorItem.setName(pane.getTabName());
        selectorItem.addItemSelectedHandler(() -> {
            onTabSelected(pane.getTabName());
            popup.hide();
        });
        return selectorItem;
    }

    private void setNavItemDropdownSelected(String tabName) {
        navDropdownTabsSelected.setText(tabName);
    }

    @UiField
    HTMLPanel navTabs;
    @UiField
    HTMLPanel navDropdownTabsSelectedContainer;
    @UiField
    InlineLabel navDropdownTabsSelected;
    @UiField
    HTMLPanel tabContent;

    private final SelectorPopup popup = new ArrowSelectableSelectorPopup();
    private Map<String, SelectorItem> tabNameToNavSelectorItem = new HashMap<>();

    interface TabWidgetUiBinder extends UiBinder<HTMLPanel, TabWidget> {}
    private static TabWidgetUiBinder ourUiBinder = GWT.create(TabWidgetUiBinder.class);
}
