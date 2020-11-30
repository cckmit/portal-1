package ru.protei.portal.ui.common.client.widget.accordion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.widget.tab.base.Tab;
import ru.protei.portal.ui.common.client.widget.htmlpanel.ClickableHTMLPanel;
import ru.protei.portal.ui.common.client.widget.tab.navitem.TabWidgetNavItem;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;

import java.util.*;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.SHOW;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.STYLE_ATTRIBUTE;

public class AccordionWidget extends Tab {

    public AccordionWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setMaxHeightToElement(accordionCardBody, maxHeight);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        setMaxHeightToElement(accordionCardBody, maxHeight);
    }

    public void setLocalStorageKey(String localStorageKey) {
        this.localStorageKey = localStorageKey;
    }

    public void setCollapseButtonDebugId(String debugId) {
        collapseButton.setId(debugId);
    }

    public void setBadge(String tabName, String badge) {
        tabNameToNavItem.entrySet().forEach(entry -> {
            if (Objects.equals(tabName, entry.getKey())) {
                (entry.getValue()).setBadge(badge);
            }
        });
    }

    @Override
    protected void onLoad() {
        initCollapseState(localStorageKey);
    }

    @Override
    public void onTabClicked(String tabName) {
        super.onTabSelected(tabName);
        if (!accordionContainer.hasClassName(SHOW)) {
            expandBody();
        }
    }

    @Override
    protected HTMLPanel getContainer() {
        return bodyContainer;
    }

    @Override
    protected void addTab(TabWidgetPane pane) {

        TabWidgetNavItem navItem = makeNavItem(pane);

        navTabs.add(navItem);
        tabNameToNavItem.put(pane.getTabName(), navItem);
        tabNameToPane.put(pane.getTabName(), pane);

        selectTabIfNeeded(pane);
    }

    @Override
    protected void clearTabs() {
        bodyContainer.clear();
        navTabs.clear();
    }

    @UiHandler("headerContainer")
    public void onAttachmentsHeaderClicked(ClickEvent event) {
        if (accordionContainer.hasClassName(SHOW)) {
            collapseBody();
        } else {
            expandBody();
        }
    }

    private void initCollapseState(String localStorageKey) {
        if (localStorageKey == null) {
            collapseBody();
            return;
        }

        if (!localStorageService.getBooleanOrDefault(localStorageKey, false)) {
            collapseBody();
            return;
        }

        expandBody();
    }

    private void collapseBody() {
        accordionContainer.removeClassName(SHOW);
        setMaxHeightToElement(accordionBody, 0);
        setLocalStorageValue(localStorageKey, false);
    }

    private void expandBody() {
        accordionContainer.addClassName(SHOW);
        setMaxHeightToElement(accordionBody, maxHeight);
        setLocalStorageValue(localStorageKey, true);
    }

    private void setMaxHeightToElement(Element element, int maxHeight) {
        element.setAttribute(STYLE_ATTRIBUTE, makeMaxHeightStyle(maxHeight));
    }

    private String makeMaxHeightStyle(int maxHeight) {
        return "max-height: " + maxHeight + Style.Unit.PX.getType();
    }

    private void setLocalStorageValue(String key, boolean value) {
        if (key == null) {
            return;
        }

        localStorageService.set(key, String.valueOf(value));
    }

    private TabWidgetNavItem makeNavItem(TabWidgetPane pane) {
        TabWidgetNavItem navItem = new TabWidgetNavItem();
        navItem.setTabName(pane.getTabName());
        navItem.setActivity(this);
        navItem.setInActive();
        return navItem;
    }

    @UiField
    ClickableHTMLPanel headerContainer;
    @UiField
    HTMLPanel bodyContainer;
    @UiField
    DivElement accordionBody;
    @UiField
    DivElement accordionContainer;
    @UiField
    DivElement accordionCardBody;
    @UiField
    AnchorElement collapseButton;
    @UiField
    HTMLPanel navTabs;

    @Inject
    private LocalStorageService localStorageService;

    private int maxHeight = Integer.MAX_VALUE;
    private String localStorageKey;


    interface AccordionWidgetUiBinder extends UiBinder<HTMLPanel, AccordionWidget> {}
    private static AccordionWidgetUiBinder ourUiBinder = GWT.create(AccordionWidgetUiBinder.class);
}
