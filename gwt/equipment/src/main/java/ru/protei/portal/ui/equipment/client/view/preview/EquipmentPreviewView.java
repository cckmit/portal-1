package ru.protei.portal.ui.equipment.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.equipment.client.activity.preview.AbstractEquipmentPreviewActivity;
import ru.protei.portal.ui.equipment.client.activity.preview.AbstractEquipmentPreviewView;

/**
 * Вид превью контакта
 */
public class EquipmentPreviewView extends Composite implements AbstractEquipmentPreviewView {

    public EquipmentPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity( AbstractEquipmentPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setHeader( String value ) {
        this.header.setText( value );
    }

    @Override
    public void setHeaderHref(String link) {
        this.header.setHref(link);
    }

    @Override
    public void setNameBySldWrks( String value ) {
        this.nameBySldWrks.setInnerText( value );
    }

    @Override
    public void setComment( String value ) {
        this.comment.setText( value );
    }

    @Override
    public void setType( String value ) {
        this.typeImage.setSrc( value );
    }

    @Override
    public void setLinkedEquipment( String value ) {
        this.primaryUse.setText( value );
    }

    @Override
    public void setDecimalNumbers( String value ) {
        this.number.setInnerText( value );
    }

    @Override
    public void setProject( String value ) {
        project.setInnerText( value );
    }

    @Override
    public void setManager( String value ) {
        manager.setInnerText( value );
    }

    @Override
    public void setCreatedBy(String value) {
        createdBy.setInnerHTML(value);
    }

    @Override
    public HasWidgets documents() {
        return documents;
    }

    @Override
    public void setLinkedEquipmentExternalLink(String href) {
        primaryUse.setHref(href);
    }

    @Override
    public void isFullScreen(boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @Override
    public void setCopyBtnEnabledStyle( boolean isEnabled ){
        copy.setStyleName("link-disabled", !isEnabled);
    }

    @Override
    public void setRemoveBtnEnabledStyle( boolean isEnabled ){
        remove.setStyleName("link-disabled", !isEnabled);
    }

    @UiHandler( "copy" )
    public void onCopyClicked( ClickEvent event ) {
        if( activity != null ) {
            activity.onCopyClicked();
        }
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        if( activity != null ) {
            activity.onRemoveClicked();
        }
    }

    @UiHandler( "header" )
    public void onFullScreenClicked( ClickEvent event ) {
        event.preventDefault();
        if( activity != null ) {
            activity.onFullScreenClicked();
        }
    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    Label comment;
    @UiField
    Anchor header;
    @UiField
    DivElement number;
    @UiField
    SpanElement nameBySldWrks;
    @UiField
    Anchor primaryUse;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement project;
    @UiField
    Button remove;
    @UiField
    Button copy;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel documents;
    @UiField
    ImageElement typeImage;
    @UiField
    HTMLPanel previewWrapperContainer;

    AbstractEquipmentPreviewActivity activity;

    interface EquipmentPreviewViewUiBinder extends UiBinder<HTMLPanel, EquipmentPreviewView > { }
    private static EquipmentPreviewViewUiBinder ourUiBinder = GWT.create(EquipmentPreviewViewUiBinder.class);
}