package ru.protei.portal.ui.common.client.widget.tab.multi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.widget.tab.base.multi.MultiTabHandler;
import ru.protei.portal.ui.common.client.widget.tab.navitem.multi.MultiTabWidgetNavItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static ru.protei.portal.test.client.DebugIds.ISSUE.TABS_CONTAINER;

public class MultiTabWidget<T> extends Composite implements MultiTabHandler<T> {
    public MultiTabWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void onTabClicked(T tabName) {
        selectTab(tabName);
        selectedTabNamesHandler.accept(selectedTabs);
    }

    public void setOnTabClickHandler(Consumer<List<T>> selectedTabNamesHandler) {
        this.selectedTabNamesHandler = selectedTabNamesHandler;
    }

    public HTMLPanel getContainer() {
        return tabContent;
    }

    public void setTabToNameRenderer(Function<T, String> tabToNameRenderer) {
        this.tabToNameRenderer = tabToNameRenderer;
    }

    public void addTabs(List<T> tabs) {
        tabs.forEach(tab -> {
            MultiTabWidgetNavItem<T> navItem = makeNavItem(tab);

            tabNameToNavItem.put(tab, navItem);
            navTabs.add(navItem);
        });
    }

    public void selectTabs(List<T> tabs) {
        if (CollectionUtils.isEmpty(tabs)) {
            return;
        }

        deselectAllTabs();
        tabs.forEach(this::selectTab);
    }

    public List<T> getSelectedTabs() {
        return selectedTabs;
    }

    private void deselectAllTabs() {
        selectedTabs.clear();
        tabNameToNavItem.values().forEach(MultiTabWidgetNavItem::setInactive);
    }

    private void selectTab(T tab) {
        if (selectedTabs.size() == 1 && selectedTabs.contains(tab)) {
            return;
        }

        if (selectedTabs.contains(tab)) {
            selectedTabs.remove(tab);
            tabNameToNavItem.get(tab).setInactive();
        } else {
            selectedTabs.add(tab);
            tabNameToNavItem.get(tab).setActive();
        }
    }

    private MultiTabWidgetNavItem<T> makeNavItem(T tab) {
        MultiTabWidgetNavItem<T> navItem = new MultiTabWidgetNavItem<>();
        navItem.setTab(tab, tabToNameRenderer.apply(tab));
        navItem.setActivity(this);
        navItem.setInactive();
        navItem.setTabNameDebugId(TABS_CONTAINER + "-" + tabToNameRenderer.apply(tab));
        return navItem;
    }

    @UiField
    HTMLPanel navTabs;
    @UiField
    HTMLPanel tabContent;

    private final List<T> selectedTabs = new ArrayList<>();
    private final Map<T, MultiTabWidgetNavItem<T>> tabNameToNavItem = new HashMap<>();

    private Consumer<List<T>> selectedTabNamesHandler;
    private Function<T, String> tabToNameRenderer;

    interface MultiTabWidgetUiBinder extends UiBinder<HTMLPanel, MultiTabWidget<?>> {}
    private static MultiTabWidgetUiBinder ourUiBinder = GWT.create(MultiTabWidgetUiBinder.class);
}
