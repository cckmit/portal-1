package ru.protei.portal.ui.common.client.widget.tab.base;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.tab.navitem.TabWidgetNavItem;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public abstract class Tab extends Composite implements HasWidgets, TabHandler {

    @Override
    public void add(Widget widget) {
        getContainer().add(widget);
        if (widget instanceof TabWidgetPane) {
            addTab((TabWidgetPane) widget);
        }
    }

    @Override
    public void clear() {
        clearTab();
    }

    @Override
    public Iterator<Widget> iterator() {
        return getContainer().iterator();
    }

    @Override
    public boolean remove(Widget widget) {
        if (widget instanceof TabWidgetPane) {
            TabWidgetPane pane = (TabWidgetPane) widget;
            tabNameToPane.remove(pane.getTabName());
            tabNameToNavItem.remove(pane.getTabName());
        }
        return getContainer().remove(widget);
    }

    @Override
    public void onTabClicked(String tabName) {
        onTabSelected(tabName);
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

    public HasVisibility tabVisibility(String tabName) {
        return new HasVisibility() {
            public boolean isVisible() { return isTabVisible(tabName); }
            public void setVisible(boolean visible) { setTabVisible(tabName, visible); }
        };
    }

    public void setTabVisible(String tabName, boolean isVisible) {
        TabWidgetNavItem navItem = tabNameToNavItem.get(tabName);
        if (navItem != null) {
            navItem.setVisible(isVisible);
        }

        TabWidgetPane pane = tabNameToPane.get(tabName);
        if (pane != null) {
            pane.setVisible(isVisible);
        }

        if (!isVisible && Objects.equals(selectedTabName, tabName)) {
            selectFirstTab();
        }
    }

    public boolean isTabVisible(String tabName) {
        TabWidgetPane pane = tabNameToPane.get(tabName);
        return pane != null && pane.isVisible();
    }

    public void selectFirstTab() {
        findFirstPane().ifPresent(p -> onTabSelected(p.getTabName()));
    }

    protected abstract HTMLPanel getContainer();

    protected abstract void addTab(TabWidgetPane widget);

    protected abstract void clearTabs();

    protected void selectTabIfNeeded(TabWidgetPane pane) {
        boolean isFirstTab = tabNameActiveByDefault == null && currentTabName == null;
        boolean isDefaultTab = tabNameActiveByDefault != null && Objects.equals(tabNameActiveByDefault, pane.getTabName());
        if (isDefaultTab || isFirstTab) {
            currentTabName = pane.getTabName();
            onTabSelected(currentTabName);
        }
    }

    protected void onTabSelected(String tabName) {
        selectedTabName = tabName;
        setNavItemSelected(tabName);
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

    private void setPaneSelected(String tabName) {
        for (Map.Entry<String, TabWidgetPane> entry : tabNameToPane.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }
    }

    private Optional<TabWidgetPane> findFirstPane() {
        return stream(tabNameToPane.values())
                .filter(UIObject::isVisible)
                .findFirst();
    }

    private void clearTab() {
        tabNameToPane.clear();
        tabNameToNavItem.clear();
        selectedTabName = null;
        currentTabName = null;
        clearTabs();
    }

    protected Map<String, TabWidgetNavItem> tabNameToNavItem = new HashMap<>();
    protected Map<String, TabWidgetPane> tabNameToPane = new HashMap<>();
    private String selectedTabName = null;
    private String currentTabName = null;
    private String tabNameActiveByDefault = null;
}
