package ru.protei.portal.ui.equipment.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
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
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setHeader( String value ) {
        this.header.setInnerText( value );
    }

    @Override
    public void setName( String value ) {
        this.name.setInnerText( value );
    }

    @Override
    public void setNameBySldWrks( String value ) {
        this.nameBySldWrks.setInnerText( value );
    }

    @Override
    public void setComment( String value ) {
        this.comment.setInnerText( value );
    }

    @Override
    public void setType( String value ) {
        this.type.setInnerText( value );
    }

    @Override
    public void setLinkedEquipment( String value ) {
        this.primaryUse.setInnerText( value );
    }

    @Override
    public void setStage( String value, String styleNamePrefix ) {
        this.stage.setInnerText( value );
        this.stage.addClassName( "label label-" + styleNamePrefix );
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

    @Inject
    @UiField
    Lang lang;
    @UiField
    SpanElement comment;
    @UiField
    LegendElement header;
    @UiField
    DivElement number;
    @UiField
    SpanElement name;
    @UiField
    SpanElement nameBySldWrks;
    @UiField
    SpanElement type;
    @UiField
    SpanElement stage;
    @UiField
    SpanElement primaryUse;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement project;
    @UiField
    Button remove;
    @UiField
    Button copy;

    @Inject
    FixedPositioner positioner;

    AbstractEquipmentPreviewActivity activity;

    interface EquipmentPreviewViewUiBinder extends UiBinder<HTMLPanel, EquipmentPreviewView > { }
    private static EquipmentPreviewViewUiBinder ourUiBinder = GWT.create(EquipmentPreviewViewUiBinder.class);
}