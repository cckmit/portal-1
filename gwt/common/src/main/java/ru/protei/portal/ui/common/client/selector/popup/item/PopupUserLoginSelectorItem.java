package ru.protei.portal.ui.common.client.selector.popup.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.util.AvatarUtils;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class PopupUserLoginSelectorItem<T> extends Composite implements SelectorItem<T> {
    public PopupUserLoginSelectorItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler<T> selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    @Override
    public HandlerRegistration addKeyUpHandler( KeyUpHandler keyUpHandler) {
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

    public void setImage( String src ) {
        if (AvatarUtils.isDefaultAvatar(src)) {
            image.addClassName("default-image");
        }
        image.setSrc( src );
    }

    @UiHandler( "anchor" )
    public void onAnchorClicked(ClickEvent event) {
        event.preventDefault();

        if (selectorItemHandler != null) {
            selectorItemHandler.onSelectorItemClicked(this);
        }
    }

    @UiField
    HTMLPanel root;

    @UiField
    ImageElement image;

    @UiField
    SpanElement login;

    @UiField
    SpanElement userName;

    private SelectorItemHandler<T> selectorItemHandler;
    private T value;

    interface PopupUserLoginSelectorItemUiBinder extends UiBinder<HTMLPanel, PopupUserLoginSelectorItem> {}
    private static PopupUserLoginSelectorItemUiBinder ourUiBinder = GWT.create(PopupUserLoginSelectorItemUiBinder.class);
}
