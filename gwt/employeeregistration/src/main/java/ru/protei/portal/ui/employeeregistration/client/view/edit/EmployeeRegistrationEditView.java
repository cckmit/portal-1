package ru.protei.portal.ui.employeeregistration.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.AbstractEmployeeRegistrationEditActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.AbstractEmployeeRegistrationEditView;

import java.util.Date;
import java.util.Set;

public class EmployeeRegistrationEditView extends Composite implements AbstractEmployeeRegistrationEditView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEmployeeRegistrationEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<Date> employmentDate() {
        return employmentDate;
    }

    @Override
    public HasValue<Set<PersonShortView>> curators() {
        return curators;
    }

    @Override
    public void setCuratorsFilter(Selector.SelectorFilter<PersonShortView> curatorsFilter) {
        curators.setFilter(curatorsFilter);
    }

    @Inject
    @UiField(provided = true)
    SinglePicker employmentDate;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector curators;

    private AbstractEmployeeRegistrationEditActivity activity;

    private static EmployeeRegistrationEditViewUiBinder ourUiBinder = GWT.create(EmployeeRegistrationEditViewUiBinder.class);
    interface EmployeeRegistrationEditViewUiBinder extends UiBinder<HTMLPanel, EmployeeRegistrationEditView> {}
}
