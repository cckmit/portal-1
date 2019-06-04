package ru.protei.portal.ui.common.client.widget.tab.navitem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.widget.tab.TabWidgetHandler;

public class TabWidgetNavItem extends Composite {

    public TabWidgetNavItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(TabWidgetHandler activity) {
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
        anchor.setText(tabName);
    }

    @UiHandler("anchor")
    public void anchorClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onTabSelected(tabName);
        }
    }

    @UiField
    HTMLPanel root;
    @UiField
    Anchor anchor;

    private String tabName;
    private TabWidgetHandler activity;

    interface TabWidgetNavItemUiBinder extends UiBinder<HTMLPanel, TabWidgetNavItem> {}
    private static TabWidgetNavItemUiBinder ourUiBinder = GWT.create(TabWidgetNavItemUiBinder.class);
}
