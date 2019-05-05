package ru.protei.portal.ui.contact.client.view.table;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.ContactColumnBuilder;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class ContactTableViewBase extends Composite {

    protected ClickColumn<Person> getDisplayNameColumn(Lang lang) {
        return new ClickColumn<Person>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.contactFullName());
            }

            @Override
            public void fillColumnValue(Element cell, Person value) {
                Element root = DOM.createDiv();
                cell.appendChild(root);

                Element fioElement = DOM.createDiv();
                fioElement.setInnerHTML(value.getDisplayName());
                root.appendChild(fioElement);

                if (value.isFired() || value.isDeleted()) {
                    Element stateElement = DOM.createDiv();
                    stateElement.setInnerHTML( makeFiredOrDeleted(value, lang));
                    root.appendChild(stateElement);
                }
            }
        };
    }

    protected ClickColumn<Person> getContactColumn(Lang lang) {
        return new ClickColumn<Person>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.contactContactInfoTitle());
                element.getStyle().setWidth(40, Style.Unit.PCT );
            }

            @Override
            public void fillColumnValue(Element cell, Person value) {
                Element root = DOM.createDiv();
                cell.appendChild(root);

                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

                String phones = infoFacade.allPhonesAsString();
                if (StringUtils.isNotBlank(phones)) {
                    root.appendChild(ContactColumnBuilder.make().add("ion ion-md-call m-r-5", phones)
                            .toElement());
                }


                String emails = infoFacade.allEmailsAsString();
                if (StringUtils.isNotBlank(emails)) {
                    root.appendChild(ContactColumnBuilder.make().add("ion ion-md-mail m-r-5", emails)
                            .toElement());
                }
            }
        };
    }

    public static String makeFiredOrDeleted(Person value, Lang lang) {
        StringBuilder sb = new StringBuilder();
        sb.append("<i class='fa fa-info-circle'></i> <b>");
        if (value.isFired()) {
            sb.append(lang.contactFiredShort());
            if (value.isDeleted()) {
                sb.append(", ");
            }
        }
        if (value.isDeleted()) {
            sb.append(value.isFired() ? lang.contactDeletedShort().toLowerCase() : lang.contactDeletedShort());
        }
        sb.append("</b>");
        return sb.toString();
    }

    protected ClickColumn<Person> getCompanyColumn(Lang lang) {
        return new ClickColumn<Person>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.company());
                element.addClassName("company");
            }

            @Override
            public void fillColumnValue(Element cell, Person value) {
                String html = "<div><div>" +  value.getCompany().getCname() + "<div>";
                if ( value.getPosition() != null ) {
                    html += "<small><i>" + value.getPosition() + "</i></small>";
                }
                html += "</div>";
                cell.setInnerHTML(html);
            }
        };
    }
}
