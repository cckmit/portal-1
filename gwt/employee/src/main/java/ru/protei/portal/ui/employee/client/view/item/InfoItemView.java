package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.employee.client.activity.item.AbstractInfoItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractInfoItemView;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewActivity;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewView;

/**
 * Представление превью сотрудника
 */
public class InfoItemView extends Composite implements AbstractInfoItemView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity(AbstractInfoItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String name, String link) {
        this.employeeName.setInnerText(name);
        this.employeeName.setHref(link);
    }

    @Override
    public void setNameIcon(String icon) {
        this.nameIcon.setClassName(icon);
    }

    @Override
    public void setBirthday(String birthday) {
        this.birthday.setInnerText(birthday);
    }

    @Override
    public void setPhones(String phones) {
        this.phones.setInnerText(phones);
    }

    @Override
    public void setEmail(String email) {
        this.email.setInnerHTML(email);
    }

    @Override
    public HasVisibility birthdayContainerVisibility() {
        return birthdayContainer;
    }

    @Override
    public HasVisibility phonesContainerVisibility() {
        return phonesContainer;
    }

    @Override
    public HasVisibility emailContainerVisibility() {
        return emailContainer;
    }

    @UiField
    AnchorElement employeeName;

    @UiField
    SpanElement birthday;

    @UiField
    SpanElement phones;

    @UiField
    SpanElement email;

    @UiField
    Element nameIcon;

    @UiField
    HTMLPanel birthdayContainer;

    @UiField
    HTMLPanel phonesContainer;

    @UiField
    HTMLPanel emailContainer;

    AbstractInfoItemActivity activity;

    private static InfoItemViewUiBinder ourUiBinder = GWT.create( InfoItemViewUiBinder.class );
    interface InfoItemViewUiBinder extends UiBinder< HTMLPanel, InfoItemView> {}
}