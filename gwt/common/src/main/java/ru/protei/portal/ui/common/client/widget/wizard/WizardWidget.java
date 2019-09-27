package ru.protei.portal.ui.common.client.widget.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.wizard.navitem.WizardWidgetNavItem;
import ru.protei.portal.ui.common.client.widget.wizard.pane.WizardWidgetPane;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.findNextElement;
import static ru.protei.portal.core.model.helper.CollectionUtils.findPreviousElement;

public class WizardWidget extends Composite implements HasWidgets, WizardWidgetHandler {

    public WizardWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        popup.setSearchVisible(false);
        popup.setAddButton(false);
    }

    public void setActivity(WizardWidgetActivity activity) {
        this.activity = activity;
    }

    @Override
    public void add(Widget widget) {
        tabContent.add(widget);
        if (widget instanceof WizardWidgetPane) {
            addTab((WizardWidgetPane) widget);
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
        if (widget instanceof WizardWidgetPane) {
            WizardWidgetPane pane = (WizardWidgetPane) widget;
            tabNameToPane.remove(pane.getTabName());
            tabNameToNavItem.remove(pane.getTabName());
            tabNames.remove(pane.getTabName());
        }
        return tabContent.remove(widget);
    }

    public void setWithCustomSelect(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public void setTabNameDebugId(String tabName, String debugId) {
        WizardWidgetNavItem navItem = tabNameToNavItem.get(tabName);
        if (navItem == null) return;
        navItem.setTabNameDebugId(debugId);
    }

    public void setButtonPreviousDebugId(String debugId) {
        btnPrevious.ensureDebugId(debugId);
    }

    public void setButtonNextDebugId(String debugId) {
        btnNext.ensureDebugId(debugId);
    }

    private void addTab(WizardWidgetPane pane) {

        WizardWidgetNavItem navItem = makeNavItem(pane);
        SelectorItem selectorItem = makeSelectorItem(pane);

        popup.getChildContainer().add(selectorItem);
        navTabs.add(navItem);
        tabNameToNavItem.put(pane.getTabName(), navItem);
        tabNameToPane.put(pane.getTabName(), pane);
        tabNames.add(pane.getTabName());

        selectFirstTab();
    }

    private WizardWidgetNavItem makeNavItem(WizardWidgetPane pane) {
        WizardWidgetNavItem navItem = new WizardWidgetNavItem();
        navItem.setTabName(pane.getTabName());
        navItem.setTabIcon(pane.getTabIcon());
        navItem.setSelectable(isSelectable);
        navItem.setHandler(this);
        navItem.setInActive();
        return navItem;
    }

    private SelectorItem makeSelectorItem(WizardWidgetPane pane) {
        SelectorItem selectorItem = new SelectorItem();
        selectorItem.setName(pane.getTabName());
        selectorItem.addClickHandler(event -> {
            if (!isSelectable) return;
            onTabSelected(pane.getTabName());
            popup.hide();
        });
        return selectorItem;
    }

    private void clearTabs() {
        navTabs.clear();
        tabNameToNavItem.clear();
        tabNames.clear();
        popup.getChildContainer().clear();
    }

    public void selectFirstTab() {
        findFirstPane().ifPresent(p -> onTabSelected(p.getTabName()));
    }

    @Override
    public void onTabSelected(String tabName) {

        if (!canLeaveTab(tabName)) return;

        WizardWidgetPane pane = tabNameToPane.get(tabName);

        setCurrentTabName(tabName);
        setNavItemSelected(tabName);
        setNavItemDropdownSelected(tabName);
        setPaneSelected(tabName);
        setBtnPrevious(pane);
        setBtnNext(pane);
    }

    private boolean canLeaveTab(String tabName) {
        if (currentTabName != null && activity != null) {
            return activity.canLeaveTab(currentTabName, tabName);
        }
        return true;
    }

    private void setCurrentTabName(String tabName) {
        currentTabName = tabName;
    }

    private void setNavItemSelected(String tabName) {
        for (Map.Entry<String, WizardWidgetNavItem> entry : tabNameToNavItem.entrySet()) {
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
        for (Map.Entry<String, WizardWidgetPane> entry : tabNameToPane.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }
    }

    private void setBtnPrevious(WizardWidgetPane pane) {

        String btnPreviousLang = pane != null ? pane.getButtonBack() : null;

        if (btnPreviousHandlerRegistration != null) btnPreviousHandlerRegistration.removeHandler();
        btnPreviousContainer.setVisible(false);

        if (btnPreviousLang == null) {
            return;
        }

        String prevTabName = findPreviousElement(tabNames, pane.getTabName());

        btnPreviousContainer.setVisible(true);
        btnPreviousText.setInnerText(btnPreviousLang);
        btnPreviousHandlerRegistration = btnPrevious.addClickHandler(event -> {
            if (prevTabName == null) {
                if (activity != null) activity.onClose();
            } else {
                boolean canGoBack = activity == null || activity.canGoBack(pane.getTabName());
                if (canGoBack) {
                    onTabSelected(prevTabName);
                }
            }
        });
    }

    private void setBtnNext(WizardWidgetPane pane) {

        String btnNextLang = pane != null ? pane.getButtonForward() : null;

        if (btnNextHandlerRegistration != null) {
            btnNextHandlerRegistration.removeHandler();
        }
        btnNextContainer.setVisible(false);

        if (btnNextLang == null) {
            return;
        }

        String nextTabName = findNextElement(tabNames, pane.getTabName());

        btnNextContainer.setVisible(true);
        btnNextText.setInnerText(btnNextLang);
        btnNextHandlerRegistration = btnNext.addClickHandler(event -> {
            if (nextTabName == null) {
                if (activity != null) activity.onDone();
            } else {
                boolean canGoNext = activity == null || activity.canGoNext(pane.getTabName());
                if (canGoNext) {
                    onTabSelected(nextTabName);
                }
            }
        });
    }

    @UiHandler("navDropdownTabsSelected")
    public void navDropdownTabsSelectedClick(ClickEvent event) {
        event.preventDefault();
        if (!isSelectable) return;
        popup.showNear(navDropdownTabsSelected);
    }

    private Optional<WizardWidgetPane> findFirstPane() {
        return tabNameToPane.values().stream().findFirst();
    }

    @UiField
    HTMLPanel navTabs;
    @UiField
    InlineLabel navDropdownTabsSelected;
    @UiField
    HTMLPanel tabContent;
    @UiField
    HTMLPanel buttons;
    @UiField
    HTMLPanel btnPreviousContainer;
    @UiField
    Button btnPrevious;
    @UiField
    SpanElement btnPreviousText;
    @UiField
    HTMLPanel btnNextContainer;
    @UiField
    Button btnNext;
    @UiField
    SpanElement btnNextText;

    private WizardWidgetActivity activity;
    private Map<String, WizardWidgetNavItem> tabNameToNavItem = new HashMap<>();
    private Map<String, WizardWidgetPane> tabNameToPane = new HashMap<>();
    private Set<String> tabNames = new LinkedHashSet<>();
    private String currentTabName = null;
    private SelectorPopup popup = new SelectorPopup();
    private boolean isSelectable = true;
    private HandlerRegistration btnPreviousHandlerRegistration;
    private HandlerRegistration btnNextHandlerRegistration;

    interface WizardWidgetUiBinder extends UiBinder<HTMLPanel, WizardWidget> {}
    private static WizardWidgetUiBinder ourUiBinder = GWT.create(WizardWidgetUiBinder.class);
}
