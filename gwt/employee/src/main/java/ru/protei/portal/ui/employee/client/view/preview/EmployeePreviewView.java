package ru.protei.portal.ui.employee.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewActivity;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewView;

/**
 * Представление превью сотрудника
 */
public class EmployeePreviewView extends Composite implements AbstractEmployeePreviewView {

    public EmployeePreviewView() {
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
    public void setIP( String value ) {
        this.ip.setInnerText( value );
    }

    @Override
    public HasWidgets getPositionsContainer() {
        return positionsContainer;
    }

    @UiField
    SpanElement id;

    @UiField
    SpanElement ip;

    @UiField
    HTMLPanel positionsContainer;

    @Inject
    @UiField
    Lang lang;

    AbstractEmployeePreviewActivity activity;

    private static EmployeePreviewViewUiBinder ourUiBinder = GWT.create( EmployeePreviewViewUiBinder.class );
    interface EmployeePreviewViewUiBinder extends UiBinder< HTMLPanel, EmployeePreviewView > {}
}