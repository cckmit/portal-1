package ru.protei.portal.ui.employee.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewActivity;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewView;

/**
 * Представление превью сотрудника
 */
public class EmployeePreviewView extends Composite implements AbstractEmployeePreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractEmployeePreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setID( String value ) {
        this.id.setInnerText( value );
    }

    @Override
    public void setIP( String ip ) {
        this.ip.setInnerText( ip );
    }

    @Override
    public HasWidgets getPositionsContainer() {
        return positionsContainer;
    }

    @Override
    public HasWidgets getInfoContainer() {
        return infoContainer;
    }

    @Override
    public void setPhotoUrl(String url) {
        photo.setUrl(url);
    }

    @Override
    public void showFullScreen(boolean isFullScreen) {
        backButtonPanel.setVisible(isFullScreen);
        rootWrapper.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @UiHandler("backButton")
    public void onBackButtonClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onBackButtonClicked();
        }
    }

    @UiField
    HTMLPanel rootWrapper;

    @UiField
    SpanElement id;

    @UiField
    HTMLPanel positionsContainer;

    @UiField
    HTMLPanel infoContainer;

    @UiField
    SpanElement ip;

    @UiField
    Image photo;

    @UiField
    HTMLPanel backButtonPanel;

    @UiField
    Button backButton;

    AbstractEmployeePreviewActivity activity;

    private static EmployeePreviewViewUiBinder ourUiBinder = GWT.create( EmployeePreviewViewUiBinder.class );
    interface EmployeePreviewViewUiBinder extends UiBinder< HTMLPanel, EmployeePreviewView > {}
}