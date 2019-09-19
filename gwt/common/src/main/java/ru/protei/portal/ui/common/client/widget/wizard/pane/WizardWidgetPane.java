package ru.protei.portal.ui.common.client.widget.wizard.pane;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

public class WizardWidgetPane extends Composite implements HasWidgets {

    public WizardWidgetPane() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void add(Widget widget) {
        root.add(widget);
    }

    @Override
    public void clear() {
        root.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return root.iterator();
    }

    @Override
    public boolean remove(Widget widget) {
        return root.remove(widget);
    }

    public void setActive() {
        root.addStyleName("active show");
    }

    public void setInActive() {
        root.removeStyleName("active show");
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabIcon(String tabIcon) {
        this.tabIcon = tabIcon;
    }

    public String getTabIcon() {
        return tabIcon;
    }

    public void setButtonBack(String buttonBack) {
        this.buttonBack = buttonBack;
    }

    public String getButtonBack() {
        return buttonBack;
    }

    public void setButtonForward(String buttonForward) {
        this.buttonForward = buttonForward;
    }

    public String getButtonForward() {
        return buttonForward;
    }

    @UiField
    HTMLPanel root;

    private String tabName;
    private String tabIcon;
    private String buttonBack;
    private String buttonForward;

    interface TabWidgetContentUiBinder extends UiBinder<HTMLPanel, WizardWidgetPane> {}
    private static TabWidgetContentUiBinder ourUiBinder = GWT.create(TabWidgetContentUiBinder.class);
}
