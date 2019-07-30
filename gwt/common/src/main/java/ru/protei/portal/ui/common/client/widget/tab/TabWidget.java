package ru.protei.portal.ui.common.client.widget.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.tab.navitem.TabWidgetNavItem;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;

import java.util.*;

public class TabWidget extends Composite implements HasWidgets, TabWidgetHandler {

    public TabWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void add(Widget widget) {
        tabContent.add(widget);
        if (widget instanceof TabWidgetPane) {
            TabWidgetPane pane = (TabWidgetPane) widget;
            pane.setInActive();
            tabNameToPane.put(pane.getTabName(), pane);
        }
    }

    @Override
    public void clear() {
        tabContent.clear();
        tabNameToPane.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return tabContent.iterator();
    }

    @Override
    public boolean remove(Widget widget) {
        return tabContent.remove(widget);
    }

    public void setTabNames(String tabNames) {
        String[] tabNamesArray = StringUtils.emptyIfNull(tabNames).split("\\|+");
        setTabNames(tabNamesArray);
    }

    public void setTabNames(String...tabNames) {
        setTabNames(Arrays.asList(tabNames));
    }

    public void setTabNames(List<String> tabNames) {

        clearTabs();

        String selectedTab = tabNameActiveByDefault;

        popup = new SelectorPopup();
        popup.setSearchVisible(false);
        popup.setAddButton(false);

        for (String tabName : tabNames) {

            if (StringUtils.isEmpty(tabName)) {
                continue;
            }

            if (selectedTab == null) {
                selectedTab = tabName;
            }

            TabWidgetNavItem navItem = new TabWidgetNavItem();
            navItem.setTabName(tabName);
            navItem.setActivity(this);
            navItem.setInActive();
            navTabs.add(navItem);
            tabNameToNavItem.put(tabName, navItem);

            SelectorItem selectorItem = new SelectorItem();
            selectorItem.setName(tabName);
            selectorItem.addClickHandler(event -> {
                onTabSelected(tabName);
                popup.hide();
            });
            popup.getChildContainer().add(selectorItem);
        }

        if (selectedTab != null) {
            onTabSelected(selectedTab);
        }
    }

    public void setTabNameActiveByDefault(String tabNameActiveByDefault) {
        this.tabNameActiveByDefault = tabNameActiveByDefault;
        onTabSelected(tabNameActiveByDefault);
    }

    private void clearTabs() {
        navTabs.clear();
        tabNameToNavItem.clear();
    }

    @Override
    public void onTabSelected(String tabName) {

        for (Map.Entry<String, TabWidgetNavItem> entry : tabNameToNavItem.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }

        for (Map.Entry<String, TabWidgetPane> entry : tabNameToPane.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }

        navDropdownTabsSelected.setText(tabName);
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

    private SelectorPopup popup;
    private String tabNameActiveByDefault;
    private Map<String, TabWidgetNavItem> tabNameToNavItem = new HashMap<>();
    private Map<String, TabWidgetPane> tabNameToPane = new HashMap<>();

    interface TabWidgetUiBinder extends UiBinder<HTMLPanel, TabWidget> {}
    private static TabWidgetUiBinder ourUiBinder = GWT.create(TabWidgetUiBinder.class);
}
