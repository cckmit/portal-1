package ru.protei.portal.ui.crm.client.view.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppActivity;
import ru.protei.portal.ui.crm.client.activity.app.AbstractAppView;

/**
 * Вид основной формы приложения
 */
public class AppView extends Composite implements AbstractAppView {
    public AppView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity( AbstractAppActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setUsername(String username) {
        this.user.setText( username );
    }

    @Override
    public HasWidgets getDetailsContainer() {
        return container;
    }

    @UiHandler("user")
    public void onUserClicked( ClickEvent event ) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onUserClicked();
        }
    }

    @UiHandler("logout")
    public void onButtonClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onLogoutClicked();
        }
    }

    @UiField
    Anchor user;
    @UiField
    HTMLPanel container;
    @UiField
    Button logout;
    @UiField
    HTMLPanel footer;
    @UiField
    HTMLPanel sidebar;

    AbstractAppActivity activity;

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {}
    private static  AppViewUiBinder ourUiBinder = GWT.create(  AppViewUiBinder.class );
}