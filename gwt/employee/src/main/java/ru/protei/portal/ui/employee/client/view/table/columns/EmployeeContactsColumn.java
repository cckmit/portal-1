package ru.protei.portal.ui.employee.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.common.LabelValuePairBuilder;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class EmployeeContactsColumn extends ClickColumn<EmployeeShortView> {

    @Inject
    public EmployeeContactsColumn(Lang lang, En_AbsenceReasonLang reasonLang) {
        this.lang = lang;
        this.reasonLang = reasonLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("employee-contacts");
        columnHeader.setInnerHTML(lang.employeeContactInfo());
    }

    @Override
    protected void fillColumnValue(Element cell, EmployeeShortView value) {

        com.google.gwt.dom.client.Element employeeContacts = DOM.createDiv();

        if (value.isFired()) {
            employeeContacts.addClassName("fired");
        }

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());
        String phones = infoFacade.publicPhonesAsFormattedString(true);

        if (!phones.isEmpty()) {
            employeeContacts.appendChild(LabelValuePairBuilder.make()
                    .addIconValuePair(null, phones, "contacts")
                    .toElement());
        }

        if (!infoFacade.publicEmailsAsString().isEmpty()) {
            employeeContacts.appendChild(EmailRender
                    .renderToElement(null, infoFacade.publicEmailsStream(), "contacts", false)
            );
        }

        cell.appendChild(employeeContacts);
    }

    En_AbsenceReasonLang reasonLang;

    Lang lang;
}
