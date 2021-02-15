package ru.protei.portal.ui.common.client.widget.tab.navitem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.widget.tab.base.TabHandler;

public class TabWidgetNavItem extends Composite {

    public TabWidgetNavItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(TabHandler activity) {
        this.activity = activity;
    }

    public void setActive() {
        anchor.addStyleName("active show");
    }

    public void setInactive() {
        anchor.removeStyleName("active show");
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
        text.setInnerText(tabName);
    }

    public void setTabIcon(String tabIcon) {
        if (tabIcon != null) {
            icon.removeClassName("hide");
            icon.addClassName(tabIcon);
        }
    }

    public void setBadge(String badge) {
        this.badge.setInnerText("(" + badge + ")");
    }

    public void setTabNameDebugId(String debugId) {
        anchor.ensureDebugId(debugId);
    }

    @UiHandler("anchor")
    public void anchorClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onTabClicked(tabName);
        }
    }

    @UiField
    HTMLPanel root;
    @UiField
    Anchor anchor;
    @UiField
    Element icon;
    @UiField
    SpanElement text;
    @UiField
    SpanElement badge;

    private String tabName;
    private TabHandler activity;

    interface TabWidgetNavItemUiBinder extends UiBinder<HTMLPanel, TabWidgetNavItem> {}
    private static TabWidgetNavItemUiBinder ourUiBinder = GWT.create(TabWidgetNavItemUiBinder.class);
}
