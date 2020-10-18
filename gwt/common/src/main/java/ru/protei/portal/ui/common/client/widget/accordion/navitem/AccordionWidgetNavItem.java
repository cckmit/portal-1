package ru.protei.portal.ui.common.client.widget.accordion.navitem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.widget.tab.base.AbstractNavItem;
import ru.protei.portal.ui.common.client.widget.tab.base.TabHandler;

public class AccordionWidgetNavItem extends Composite implements AbstractNavItem {

    public AccordionWidgetNavItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(TabHandler activity) {
        this.activity = activity;
    }

    public void setActive() {
        anchor.addStyleName("active show");
    }

    public void setInActive() {
        anchor.removeStyleName("active show");
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
        text.setInnerText(tabName);
    }

    public void setTabNameDebugId(String debugId) {
        anchor.ensureDebugId(debugId);
    }

    public void setBadge(String badge) {
        this.badge.setInnerText(badge);
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
    LabelElement text;
    @UiField
    LabelElement badge;

    private String tabName;
    private TabHandler activity;
    
    interface AccordionWidgetNavItemUiBinder extends UiBinder<HTMLPanel, AccordionWidgetNavItem> {}
    private static AccordionWidgetNavItemUiBinder ourUiBinder = GWT.create(AccordionWidgetNavItemUiBinder.class);
}