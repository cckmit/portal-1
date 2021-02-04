package ru.protei.portal.ui.common.client.widget.tab.navitem.multi;

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
import ru.protei.portal.ui.common.client.widget.tab.base.multi.MultiTabHandler;

public class MultiTabWidgetNavItem<T> extends Composite {
    public MultiTabWidgetNavItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(MultiTabHandler<T> activity) {
        this.activity = activity;
    }

    public void setActive() {
        anchor.addStyleName("active show");
    }

    public void setInactive() {
        anchor.removeStyleName("active show");
    }

    public void setTab(T tab, String tabName) {
        this.tab = tab;
        text.setInnerText(tabName);
    }

    public void setTabNameDebugId(String debugId) {
        anchor.ensureDebugId(debugId);
    }

    @UiHandler("anchor")
    public void anchorClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onTabClicked(tab);
        }
    }

    @UiField
    HTMLPanel root;
    @UiField
    Anchor anchor;
    @UiField
    SpanElement text;
    @UiField
    SpanElement badge;

    private T tab;
    private MultiTabHandler<T> activity;

    interface MultiTabWidgetNavItemUiBinder extends UiBinder<HTMLPanel, MultiTabWidgetNavItem<?>> {}
    private static MultiTabWidgetNavItemUiBinder ourUiBinder = GWT.create(MultiTabWidgetNavItemUiBinder.class);
}
