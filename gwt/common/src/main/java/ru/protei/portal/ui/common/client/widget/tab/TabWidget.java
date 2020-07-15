package ru.protei.portal.ui.common.client.widget.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.tab.navitem.TabWidgetNavItem;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;
import ru.protei.portal.ui.common.client.widget.wizard.pane.WizardWidgetPane;

import java.util.*;

public class TabWidget extends Composite implements HasWidgets, TabWidgetHandler {

    public TabWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        popup.setSearchVisible(false);
        popup.setAddButton(false);
    }

    @Override
    public void add(Widget widget) {
        tabContent.add(widget);
        if (widget instanceof TabWidgetPane) {
            addTab((TabWidgetPane) widget);
        }
    }

    @Override
    public void clear() {
        tabContent.clear();
        tabNameToPane.clear();
        clearTabs();
    }

    @Override
    public Iterator<Widget> iterator() {
        return tabContent.iterator();
    }

    @Override
    public boolean remove(Widget widget) {
        if (widget instanceof TabWidgetPane) {
            TabWidgetPane pane = (TabWidgetPane) widget;
            tabNameToPane.remove(pane.getTabName());
            tabNameToNavItem.remove(pane.getTabName());
        }
        return tabContent.remove(widget);
    }

    public void selectFirstTab() {
        findFirstPane().ifPresent(p -> onTabSelected(p.getTabName()));
    }

    public void setTabNameActiveByDefault(String tabNameActiveByDefault) {
        this.tabNameActiveByDefault = tabNameActiveByDefault;
        onTabSelected(tabNameActiveByDefault);
    }

    public void setTabNameDebugId(String tabName, String debugId) {
        TabWidgetNavItem navItem = tabNameToNavItem.get(tabName);
        if (navItem == null) return;
        navItem.setTabNameDebugId(debugId);
    }

    private void addTab(TabWidgetPane pane) {

        TabWidgetNavItem navItem = makeNavItem(pane);
        SelectorItem selectorItem = makeSelectorItem(pane);

        popup.getChildContainer().add(selectorItem);
        navTabs.add(navItem);
        tabNameToNavItem.put(pane.getTabName(), navItem);
        tabNameToPane.put(pane.getTabName(), pane);

        selectTabIfNeeded(pane);
    }

    private TabWidgetNavItem makeNavItem(TabWidgetPane pane) {
        TabWidgetNavItem navItem = new TabWidgetNavItem();
        navItem.setTabName(pane.getTabName());
        navItem.setTabIcon(pane.getTabIcon());
        navItem.setActivity(this);
        navItem.setInActive();
        return navItem;
    }

    private SelectorItem makeSelectorItem(TabWidgetPane pane) {
        SelectorItem selectorItem = new SelectorItem();
        selectorItem.setName(pane.getTabName());
        selectorItem.addClickHandler(event -> {
            onTabSelected(pane.getTabName());
            popup.hide();
        });
        return selectorItem;
    }

    private void selectTabIfNeeded(TabWidgetPane pane) {
        boolean isFirstTab = tabNameActiveByDefault == null && currentTabName == null;
        boolean isDefaultTab = tabNameActiveByDefault != null && Objects.equals(tabNameActiveByDefault, pane.getTabName());
        if (isDefaultTab || isFirstTab) {
            currentTabName = pane.getTabName();
            onTabSelected(currentTabName);
        }
    }

    private void clearTabs() {
        navTabs.clear();
        tabNameToNavItem.clear();
        popup.getChildContainer().clear();
    }

    @Override
    public void onTabSelected(String tabName) {
        setNavItemSelected(tabName);
        setNavItemDropdownSelected(tabName);
        setPaneSelected(tabName);
    }

    private void setNavItemSelected(String tabName) {
        for (Map.Entry<String, TabWidgetNavItem> entry : tabNameToNavItem.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }
    }

    private void setNavItemDropdownSelected(String tabName) {
        navDropdownTabsSelected.setText(tabName);
    }

    private void setPaneSelected(String tabName) {
        for (Map.Entry<String, TabWidgetPane> entry : tabNameToPane.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }
    }

    private Optional<TabWidgetPane> findFirstPane() {
        return tabNameToPane.values().stream().findFirst();
    }

    @UiHandler("navDropdownTabsSelected")
    public void navDropdownTabsSelectedClick(ClickEvent event) {
        event.preventDefault();
        popup.showNear(navDropdownTabsSelected);
    }

    @UiField
    HTMLPanel navTabs;
    @UiField
    InlineLabel navDropdownTabsSelected;
    @UiField
    HTMLPanel tabContent;

    private SelectorPopup popup = new SelectorPopup();
    private String currentTabName = null;
    private String tabNameActiveByDefault = null;
    private Map<String, TabWidgetNavItem> tabNameToNavItem = new HashMap<>();
    private Map<String, TabWidgetPane> tabNameToPane = new HashMap<>();

    interface TabWidgetUiBinder extends UiBinder<HTMLPanel, TabWidget> {}
    private static TabWidgetUiBinder ourUiBinder = GWT.create(TabWidgetUiBinder.class);
}
