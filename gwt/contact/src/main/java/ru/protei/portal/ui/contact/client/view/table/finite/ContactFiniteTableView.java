package ru.protei.portal.ui.contact.client.view.table.finite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.common.ContactColumnBuilder;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contact.client.activity.table.finite.AbstractContactFiniteTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.finite.AbstractContactFiniteTableView;

import java.util.ArrayList;
import java.util.List;

public class ContactFiniteTableView extends Composite implements AbstractContactFiniteTableView {

    @Inject
    public void onInit(EditClickColumn<Person> editClickColumn, RemoveClickColumn<Person> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractContactFiniteTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);
        removeClickColumn.setPrivilege(En_Privilege.CONTACT_REMOVE);

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void setData(List<Person> persons) {
        for (Person person : persons) {
            table.addRow(person);
        }
    }

    private void initTable() {

        editClickColumn.setPrivilege(En_Privilege.CONTACT_EDIT);

        ClickColumn<Person> displayName = new ClickColumn<Person>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.contactFullName());
            }

            @Override
            public void fillColumnValue(Element cell, Person value) {
                Element root = DOM.createDiv();
                cell.appendChild(root);

                Element fioElement = DOM.createDiv();
                fioElement.setInnerHTML("<b>" + value.getDisplayName() + "<b>");
                root.appendChild(fioElement);

                if (value.isFired() || value.isDeleted()) {
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
                    Element stateElement = DOM.createDiv();
                    stateElement.setInnerHTML(sb.toString());
                    root.appendChild(stateElement);
                }

                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());
                root.appendChild(ContactColumnBuilder.make().add("ion-android-call", infoFacade.getWorkPhone())
                        .add("ion-android-call", infoFacade.getMobilePhone())
                        .add("ion-android-phone-portrait", infoFacade.getHomePhone())
                        .toElement());

                root.appendChild(ContactColumnBuilder.make().add("ion-android-mail", infoFacade.getEmail())
                        .add("ion-android-mail", infoFacade.getEmail_own())
                        .toElement());
            }
        };
        columns.add(displayName);

        ClickColumn<Person> company = new ClickColumn<Person>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText(lang.company());
                element.addClassName("company");
            }

            @Override
            public void fillColumnValue(Element cell, Person value) {
                Element root = DOM.createDiv();
                cell.appendChild(root);

                Element fioElement = DOM.createDiv();
                fioElement.setInnerHTML("<b>" + value.getCompany().getCname() + "<b>");
                root.appendChild(fioElement);

                Element posElement = DOM.createDiv();
                posElement.addClassName("contact-position");
                posElement.setInnerHTML(value.getPosition());
                root.appendChild(posElement);
            }
        };
        columns.add(company);

        table.addColumn(company.header, company.values);
        table.addColumn(displayName.header, displayName.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    @UiField
    TableWidget<Person> table;

    @UiField
    HTMLPanel tableContainer;

    @Inject
    @UiField
    Lang lang;

    ClickColumnProvider<Person> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Person > editClickColumn;
    RemoveClickColumn<Person> removeClickColumn;
    List<ClickColumn> columns = new ArrayList<>();

    AbstractContactFiniteTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder<HTMLPanel, ContactFiniteTableView> {}
}
