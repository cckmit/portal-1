package ru.protei.portal.ui.employee.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LabelValuePairBuilder;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class EmployeeDepartmentColumn extends ClickColumn<EmployeeShortView> {

    @Inject
    public EmployeeDepartmentColumn(Lang lang, En_AbsenceReasonLang reasonLang) {
        this.lang = lang;
        this.reasonLang = reasonLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("employee-department");
        columnHeader.setInnerHTML(lang.employeePosition());
    }

    @Override
    protected void fillColumnValue(Element cell, EmployeeShortView value) {

        if (value.getCurrentAbsence() != null) {
            cell.addClassName(reasonLang.getStyle(value.getCurrentAbsence().getReason()));
            cell.setTitle(reasonLang.getName(value.getCurrentAbsence().getReason()));
        }

        com.google.gwt.dom.client.Element employeeDepartment = DOM.createDiv();

        if (value.isFired()) {
            employeeDepartment.addClassName("fired");
        }

        employeeDepartment.addClassName("department");
        com.google.gwt.dom.client.Element department;
        com.google.gwt.dom.client.Element departmentParent;
        com.google.gwt.dom.client.Element position;
        com.google.gwt.dom.client.Element company;

        WorkerEntryFacade entryFacade = new WorkerEntryFacade(value.getWorkerEntries());
        WorkerEntryShortView mainEntry = entryFacade.getMainEntry();

        if (mainEntry != null) {
            company = LabelValuePairBuilder.make()
                    .addIconValuePair(null, mainEntry.getCompanyName(), "contacts")
                    .toElement();
            employeeDepartment.appendChild(company);

            if (mainEntry.getDepartmentParentName() == null) {
                department = LabelValuePairBuilder.make()
                        .addIconValuePair(null, mainEntry.getDepartmentName(), "contacts")
                        .toElement();

                employeeDepartment.appendChild(department);
            } else {
                departmentParent = LabelValuePairBuilder.make()
                        .addIconValuePair(null, mainEntry.getDepartmentParentName(), "contacts")
                        .toElement();

                department = LabelValuePairBuilder.make()
                        .addIconValuePair(null, mainEntry.getDepartmentName(), "contacts")
                        .toElement();

                employeeDepartment.appendChild(departmentParent);
                employeeDepartment.appendChild(department);
            }

            if (mainEntry.getPositionName() != null){
                position = LabelValuePairBuilder.make()
                        .addIconValuePair(null, mainEntry.getPositionName(), "contacts")
                        .toElement();

                employeeDepartment.appendChild(position);
            }
        } else if (value.isFired()) {
            department = LabelValuePairBuilder.make()
                    .addIconValuePair(null, lang.employeeFired() + (value.getFireDate() == null ? "" : " " + DateFormatter.formatDateOnly(value.getFireDate())), "contacts")
                    .toElement();
            employeeDepartment.appendChild(department);
        }

        cell.appendChild(employeeDepartment);
    }

    En_AbsenceReasonLang reasonLang;

    Lang lang;
}
