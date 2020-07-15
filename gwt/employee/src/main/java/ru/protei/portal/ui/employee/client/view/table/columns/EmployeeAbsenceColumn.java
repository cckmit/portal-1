package ru.protei.portal.ui.employee.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.LabelValuePairBuilder;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;

public class EmployeeAbsenceColumn extends ClickColumn<EmployeeShortView> {

    @Inject
    public EmployeeAbsenceColumn(En_AbsenceReasonLang reasonLang, PolicyService policyService) {
        this.reasonLang = reasonLang;
        this.policyService = policyService;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {}

    @Override
    protected void fillColumnValue(Element cell, EmployeeShortView value) {
        if (value.getCurrentAbsence() == null || !policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW)) return;

        com.google.gwt.dom.client.Element employeeAbsence = DOM.createDiv();

        if (value.isFired()) {
            employeeAbsence.addClassName("fired");
        }

        com.google.gwt.dom.client.Element reason = LabelValuePairBuilder.make()
                .addIconPair(reasonLang.getIcon(value.getCurrentAbsence().getReason()), "absence-reason")
                .toElement();
        reason.setTitle(reasonLang.getName(value.getCurrentAbsence().getReason()));

        employeeAbsence.appendChild(reason);
        cell.appendChild(employeeAbsence);
    }

    En_AbsenceReasonLang reasonLang;
    PolicyService policyService;
}
