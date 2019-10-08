package ru.protei.portal.ui.common.client.widget.wizard.navitem;

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
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidgetHandler;

public class WizardWidgetNavItem extends Composite {

    public WizardWidgetNavItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setHandler(WizardWidgetHandler handler) {
        this.handler = handler;
    }

    public void setActive() {
        anchor.addStyleName("active show");
    }

    public void setInActive() {
        anchor.removeStyleName("active show");
    }

    public void setSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
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

    public void setTabNameDebugId(String debugId) {
        anchor.ensureDebugId(debugId);
    }

    @UiHandler("anchor")
    public void anchorClick(ClickEvent event) {
        event.preventDefault();
        if (!isSelectable) {
            return;
        }
        if (handler != null) {
            handler.onTabSelected(tabName);
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

    private boolean isSelectable;
    private String tabName;
    private WizardWidgetHandler handler;

    interface TabWidgetNavItemUiBinder extends UiBinder<HTMLPanel, WizardWidgetNavItem> {}
    private static TabWidgetNavItemUiBinder ourUiBinder = GWT.create(TabWidgetNavItemUiBinder.class);
}
