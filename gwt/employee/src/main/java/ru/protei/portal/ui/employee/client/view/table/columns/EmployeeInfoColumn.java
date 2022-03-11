package ru.protei.portal.ui.employee.client.view.table.columns;

import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LabelValuePairBuilder;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.client.common.UiConstants.DateTime.MSK_TIME_ZONE_OFFSET_IN_MINUTES;

public class EmployeeInfoColumn extends ClickColumn<EmployeeShortView> {

    @Inject
    public EmployeeInfoColumn(Lang lang, En_AbsenceReasonLang reasonLang) {
        this.lang = lang;
        this.reasonLang = reasonLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("employee-info");
        columnHeader.setInnerHTML(lang.employeeEmployeeFullName());
    }

    @Override
    protected void fillColumnValue(Element cell, EmployeeShortView value) {

        com.google.gwt.dom.client.Element employeeInfo = DOM.createDiv();

        if (value.isFired()) {
            employeeInfo.addClassName("fired");

            employeeInfo.appendChild(LabelValuePairBuilder.make()
                    .addIconValuePair("fa fa-ban text-danger", value.getDisplayName(), "contacts")
                    .toElement());
        } else {
            employeeInfo.appendChild(LabelValuePairBuilder.make()
                    .addIconValuePair(null, value.getDisplayName(), "contacts bold")
                    .toElement());
        }
        employeeInfo.appendChild(LabelValuePairBuilder.make()
                .addIconValuePair("fa fa-birthday-cake", DateFormatter.formatDateMonth(value.getBirthday(), TimeZone.createTimeZone(MSK_TIME_ZONE_OFFSET_IN_MINUTES)), "contacts")
                .toElement());

        cell.appendChild(employeeInfo);
    }

    En_AbsenceReasonLang reasonLang;

    Lang lang;
}
