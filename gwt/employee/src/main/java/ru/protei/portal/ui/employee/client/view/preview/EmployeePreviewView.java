package ru.protei.portal.ui.employee.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
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
    public void setName( String name ) {
        this.employeeName.setInnerText(name);
    }

    @Override
    public HasWidgets getPositionsContainer() {
        return positionsContainer;
    }

    @Override
    public Widget asWidget(boolean isForTableView) {
        if (isForTableView) {
            rootWrapper.addStyleName("preview-wrapper");
            employeeNameBlock.setVisible(true);
        } else {
            rootWrapper.removeStyleName("preview-wrapper");
        }
        return asWidget();
    }

    @UiField
    HTMLPanel rootWrapper;

    @UiField
    SpanElement id;

    @UiField
    HTMLPanel positionsContainer;

    @UiField
    HTMLPanel employeeNameBlock;

    @UiField
    SpanElement employeeName;

    @Inject
    @UiField
    Lang lang;


    AbstractEmployeePreviewActivity activity;

    private static EmployeePreviewViewUiBinder ourUiBinder = GWT.create( EmployeePreviewViewUiBinder.class );
    interface EmployeePreviewViewUiBinder extends UiBinder< HTMLPanel, EmployeePreviewView > {}
}