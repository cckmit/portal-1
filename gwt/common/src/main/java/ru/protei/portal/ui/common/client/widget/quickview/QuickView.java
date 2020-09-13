package ru.protei.portal.ui.common.client.widget.quickview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.Iterator;

public class QuickView extends Composite implements HasWidgets {

    public QuickView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void add(Widget widget) {
        content.add(widget);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return content.iterator();
    }

    @Override
    public boolean remove(Widget widget) {
        return content.remove(widget);
    }

    @UiHandler("substrate")
    void substrateClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        hide();
    }

    public void setBelowHeader(boolean isBelow) {
        quickview.removeStyleName("quickview-wrapper-below-header");
        if (isBelow) {
            quickview.addStyleName("quickview-wrapper-below-header");
        }
    }

    public void setShow(boolean isShow) {
        show(isShow);
    }

    public boolean isShow() {
        return quickview.getElement().hasClassName("open");
    }

    public void show(boolean isShow) {
        quickview.removeStyleName("open");
        if (isShow) {
            quickview.addStyleName("open");
        }
    }

    public void hide() {
        show(false);
    }

    public void show() {
        show(true);
    }

    @UiField
    HTMLPanel quickview;
    @UiField
    HTMLPanel content;
    @UiField
    FocusPanel substrate;

    interface QuickViewUiBinder extends UiBinder<HTMLPanel, QuickView> {}
    private static final QuickViewUiBinder ourUiBinder = GWT.create(QuickViewUiBinder.class);
}
