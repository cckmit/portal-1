package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;

/**
 * Представление сотрудника
 */
public class EmployeeItemView extends Composite implements AbstractEmployeeItemView {

    public EmployeeItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setActivity( AbstractEmployeeItemActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setName( String name, String link ) {
        this.name.setText( name );
        this.name.setTitle( name );
        this.name.setHref( link );
    }

    @Override
    public void setBirthday( String value ) {
        birthdayContainer.setVisible( value != null && !value.isEmpty() );
        birthday.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPhone( String value ) {
        phoneContainer.setVisible( value != null && !value.isEmpty() );
        phone.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setEmail( String value ) {
        emailContainer.setVisible(!value.isEmpty());
        emails.setInnerHTML(value);
    }

    @Override
    public void setDepartmentParent(String value) {
        departmentParentContainer.setVisible( value != null && !value.isEmpty() );
        departmentParent.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setDepartment( String value ) {
        departmentContainer.setVisible( value != null && !value.isEmpty() );
        department.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPosition( String value ) {
        positionContainer.setVisible( value != null && !value.isEmpty() );
        position.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setCompany( String value ) {
        companyContainer.setVisible( value != null && !value.isEmpty() );
        company.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setIP(String value) {
        ipContainer.setVisible( value != null && !value.isEmpty() );
        ip.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setFireDate(String value) {
        employeeContainer.addClassName("fired");
        name.setHTML("<i class='fa fa-ban text-danger'></i> " + this.name.getHTML());
        fireDateContainer.setVisible(true);
        fireDate.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPhoto( String url ) {
        photo.setUrl( url );
    }

    public void setAbsenceReason(En_AbsenceReason reason) {
        if (reason != null) {
            addStyleName(reasonLang.getStyle(reason));
            setTitle(reasonLang.getName(reason));
        }
    }

    @UiField
    Anchor name;

    @UiField
    HTMLPanel birthdayContainer;

    @UiField
    HTMLPanel fireDateContainer;

    @UiField
    HTMLPanel phoneContainer;

    @UiField
    HTMLPanel emailContainer;

    @UiField
    HTMLPanel departmentParentContainer;

    @UiField
    HTMLPanel departmentContainer;

    @UiField
    HTMLPanel positionContainer;

    @UiField
    HTMLPanel companyContainer;

    @UiField
    HTMLPanel ipContainer;

    @UiField
    SpanElement birthday;

    @UiField
    SpanElement fireDate;

    @UiField
    SpanElement phone;

    @UiField
    Image photo;

    @UiField
    SpanElement department;

    @UiField
    SpanElement departmentParent;

    @UiField
    SpanElement position;

    @UiField
    SpanElement company;

    @UiField
    SpanElement ip;

    @UiField
    DivElement employeeContainer;

    @UiField
    SpanElement emails;

    @Inject
    En_AbsenceReasonLang reasonLang;

    AbstractEmployeeItemActivity activity;

    private static EmployeeItemViewUiBinder ourUiBinder = GWT.create( EmployeeItemViewUiBinder.class );
    interface EmployeeItemViewUiBinder extends UiBinder< HTMLPanel, EmployeeItemView > {}
}