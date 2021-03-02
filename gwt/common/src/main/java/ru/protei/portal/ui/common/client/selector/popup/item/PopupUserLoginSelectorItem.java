package ru.protei.portal.ui.common.client.selector.popup.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.selector.SelectorItem;


public class PopupUserLoginSelectorItem<T> extends Composite implements SelectorItem<T> {

    public PopupUserLoginSelectorItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.addLoadHandler(loadEvent -> {
            if (image.getOffsetWidth() == image.getOffsetHeight()) {
                image.addStyleName("default-icon");
            }
        });
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler<T> selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler keyUpHandler) {
        return addHandler( keyUpHandler, KeyUpEvent.getType() );
    }

    @Override
    public void setElementHtml(String elementHtml) {
        root.getElement().setInnerHTML(elementHtml);
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setLogin(String login) {
        this.login.setInnerText(login);
    }

    public void setUserName(String userName) {
        this.userName.setInnerText(userName);
    }

    public void setImage(String url) {
        image.setUrl(url);
    }

    @UiHandler( "panel" )
    public void onPanelClicked(ClickEvent event) {
        event.preventDefault();
        if (selectorItemHandler != null) {
            selectorItemHandler.onSelectorItemClicked(this);
        }
    }

    @UiHandler( "panel" )
    public void onPanelKeyDown(KeyDownEvent event) {
        event.preventDefault();
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            if (selectorItemHandler != null) {
                selectorItemHandler.onSelectorItemClicked(this);
            }
        }
    }

    @UiField
    HTMLPanel root;

    @UiField
    SpanElement login;

    @UiField
    SpanElement userName;

    @UiField
    Image image;

    private SelectorItemHandler<T> selectorItemHandler;
    private T value;

    interface PopupUserLoginSelectorItemUiBinder extends UiBinder<HTMLPanel, PopupUserLoginSelectorItem> {}
    private static PopupUserLoginSelectorItemUiBinder ourUiBinder = GWT.create(PopupUserLoginSelectorItemUiBinder.class);
}
