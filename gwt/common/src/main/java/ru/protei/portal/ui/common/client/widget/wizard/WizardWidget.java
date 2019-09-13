package ru.protei.portal.ui.common.client.widget.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.StringUtils;
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
    }

    public void setActivity(WizardWidgetActivity activity) {
        this.activity = activity;
    }

    @Override
    public void add(Widget widget) {
        tabContent.add(widget);
        if (widget instanceof WizardWidgetPane) {
            WizardWidgetPane pane = (WizardWidgetPane) widget;
            pane.setInActive();
            tabNameToPane.put(pane.getTabName(), pane);
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

    public void setTabNames(String tabNames) {
        String[] tabNamesArray = StringUtils.emptyIfNull(tabNames).split("\\|+");
        setTabNames(tabNamesArray);
    }

    public void setTabNames(String...tabNames) {
        setTabNames(Arrays.asList(tabNames));
    }

    public void setTabNames(List<String> tabNames) {

        clearTabs();

        String selectedTab = null;

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

            WizardWidgetPane pane = tabNameToPane.get(tabName);

            WizardWidgetNavItem navItem = new WizardWidgetNavItem();
            navItem.setTabName(tabName);
            navItem.setTabIcon(pane.getTabIcon());
            navItem.setSelectable(isSelectable);
            navItem.setHandler(this);
            navItem.setInActive();
            navTabs.add(navItem);
            tabNameToNavItem.put(tabName, navItem);

            SelectorItem selectorItem = new SelectorItem();
            selectorItem.setName(tabName);
            if (isSelectable) {
                selectorItem.addClickHandler(event -> {
                    onTabSelected(tabName);
                    popup.hide();
                });
            }
            popup.getChildContainer().add(selectorItem);

            this.tabNames.add(tabName);
        }

        if (selectedTab != null) {
            onTabSelected(selectedTab);
        }
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

    private void clearTabs() {
        navTabs.clear();
        tabNameToNavItem.clear();
        tabNames.clear();
        if (popup != null) popup.getChildContainer().clear();
    }

    @Override
    public void onTabSelected(String tabName) {

        if (currentTabName != null && activity != null) {
            if (!activity.canLeaveTab(currentTabName, tabName)) {
                return;
            }
        }

        currentTabName = tabName;

        for (Map.Entry<String, WizardWidgetNavItem> entry : tabNameToNavItem.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }

        for (Map.Entry<String, WizardWidgetPane> entry : tabNameToPane.entrySet()) {
            entry.getValue().setInActive();
            if (Objects.equals(tabName, entry.getKey())) {
                entry.getValue().setActive();
            }
        }

        navDropdownTabsSelected.setText(tabName);

        WizardWidgetPane pane = tabNameToPane.get(tabName);
        String btnPreviousLang = pane != null ? pane.getButtonBack() : null;
        String btnNextLang = pane != null ? pane.getButtonForward() : null;

        setBtnPrevious(tabName, btnPreviousLang);
        setBtnNext(tabName, btnNextLang);
    }

    private void setBtnPrevious(String tabName, String btnPreviousLang) {

        if (btnPreviousHandlerRegistration != null) {
            btnPreviousHandlerRegistration.removeHandler();
        }
        btnPreviousContainer.setVisible(false);

        if (btnPreviousLang == null) {
            return;
        }

        String prevTabName = findPreviousElement(tabNames, tabName);

        btnPreviousContainer.setVisible(true);
        btnPreviousText.setInnerText(btnPreviousLang);
        btnPreviousHandlerRegistration = btnPrevious.addClickHandler(event -> {
            if (prevTabName == null) {
                if (activity != null) {
                    activity.onClose();
                }
            } else {
                if (activity != null && !activity.canGoBack(tabName)) {
                    return;
                }
                onTabSelected(prevTabName);
            }
        });
    }

    private void setBtnNext(String tabName, String btnNextLang) {

        if (btnNextHandlerRegistration != null) {
            btnNextHandlerRegistration.removeHandler();
        }
        btnNextContainer.setVisible(false);

        if (btnNextLang == null) {
            return;
        }

        String nextTabName = findNextElement(tabNames, tabName);

        btnNextContainer.setVisible(true);
        btnNextText.setInnerText(btnNextLang);
        btnNextHandlerRegistration = btnNext.addClickHandler(event -> {
            if (nextTabName == null) {
                if (activity != null) {
                    activity.onDone();
                }
            } else {
                if (activity != null && !activity.canGoNext(tabName)) {
                    return;
                }
                onTabSelected(nextTabName);
            }
        });
    }

    @UiHandler("navDropdownTabsSelected")
    public void navDropdownTabsSelectedClick(ClickEvent event) {
        event.preventDefault();
        if (!isSelectable) return;
        popup.showNear(navDropdownTabsSelected);
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
    private String currentTabName;
    private SelectorPopup popup;
    private boolean isSelectable = true;
    private HandlerRegistration btnPreviousHandlerRegistration;
    private HandlerRegistration btnNextHandlerRegistration;

    interface WizardWidgetUiBinder extends UiBinder<HTMLPanel, WizardWidget> {}
    private static WizardWidgetUiBinder ourUiBinder = GWT.create(WizardWidgetUiBinder.class);
}
