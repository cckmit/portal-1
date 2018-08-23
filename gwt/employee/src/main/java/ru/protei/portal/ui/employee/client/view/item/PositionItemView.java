package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;

/**
 * Представление должности
 */
public class PositionItemView extends Composite implements AbstractPositionItemView {

    public PositionItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractPositionItemActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setCompany( String value ) {
        this.company.setInnerText( value );
    }

    @Override
    public void setDepartment( String value ) {
        this.department.setInnerText( value );
    }

    @Override
    public void setPosition( String value ) {
        this.position.setInnerText( value );
    }

    @Override
    public void showMainInfo( boolean isMain ) {
        mainInfoContainer.setVisible( isMain );
    }

    @UiField
    DivElement company;

    @UiField
    DivElement department;

    @UiField
    SpanElement position;

    @UiField
    HTMLPanel mainInfoContainer;

    AbstractPositionItemActivity activity;

    private static PositionItemViewUiBinder ourUiBinder = GWT.create( PositionItemViewUiBinder.class );
    interface PositionItemViewUiBinder extends UiBinder< HTMLPanel, PositionItemView > {}
}