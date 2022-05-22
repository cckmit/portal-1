package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.common.ClickHTMLPanel;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;

import static ru.protei.portal.core.model.util.CrmConstants.Style.HIDE;

/**
 * Представление сотрудника
 */
public class EmployeeItemView extends Composite implements AbstractEmployeeItemView {

    public EmployeeItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity( AbstractEmployeeItemActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public void setName( String name ) {
        this.name.setInnerText(name);
    }

    @Override
    public void setBirthday( String value ) {
        birthday.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPhone( String value ) {
        phone.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setEmail( String value ) {
        emails.setInnerHTML(value);
    }

    @Override
    public void setGroupOrDepartment(String value) {
        department.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setPosition( String value ) {
        position.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setCompany( String value ) {
        company.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setIP(String value) {
        ip.setInnerText( value == null ? "" : value );
    }

    @Override
    public void setFireDate(String value) {
        employeeContainer.addClassName("fired");
        firedDate.setInnerText(value);

        // show fired icon placed on absence icon position
        absenceReason.removeClassName(HIDE);
        absenceIcon.addClassName("text-danger fa-light fa-ban");
        name.addClassName("text-danger");
    }

    @Override
    public void setPhoto( String url ) {
        photo.setUrl( url );
    }

    @Override
    public HasVisibility editVisibility() {
        return editAnchor;
    }

    @Override
    public void setCurrentAbsence(PersonAbsence absence) {
        if (absence == null) {
            absenceReason.addClassName(HIDE);
            absenceReason.setTitle(StringUtils.EMPTY);
        } else {
            absenceReason.removeClassName(HIDE);
            absenceReason.setTitle(reasonLang.getName(absence.getReason()) +
                    "\n" +
                    DateFormatter.formatDateTime(absence.getFromTime()) +
                    " - " +
                    DateFormatter.formatDateTime(absence.getTillTime()));
            absenceIcon.addClassName(reasonLang.getIcon(absence.getReason()));
        }
    }

    @UiHandler("rootContainer")
    public void onItemSelected(ClickEvent event) {
        activity.onEmployeePreviewClicked(id);
    }

    @UiHandler("editAnchor")
    public void onItemEditSelected(ClickEvent event) {
        event.preventDefault();
        activity.onEmployeeEditClicked(id);
    }
    @UiField
    Anchor editAnchor;

    @UiField
    DivElement name;
    @UiField
    SpanElement birthday;
    @UiField
    SpanElement phone;
    @UiField
    Image photo;
    @UiField
    DivElement department;
    @UiField
    Element position;
    @UiField
    DivElement company;
    @UiField
    SpanElement ip;
    @UiField
    DivElement employeeContainer;
    @UiField
    SpanElement emails;
    @UiField
    Element absenceIcon;
    @UiField
    DivElement absenceReason;
    @UiField
    DivElement firedDate;
    @UiField
    ClickHTMLPanel rootContainer;

    @Inject
    En_AbsenceReasonLang reasonLang;

    private Long id;
    private AbstractEmployeeItemActivity activity;

    private static EmployeeItemViewUiBinder ourUiBinder = GWT.create( EmployeeItemViewUiBinder.class );
    interface EmployeeItemViewUiBinder extends UiBinder< HTMLPanel, EmployeeItemView > {}
}