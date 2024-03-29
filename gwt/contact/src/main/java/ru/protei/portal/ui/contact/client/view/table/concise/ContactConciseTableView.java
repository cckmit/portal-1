package ru.protei.portal.ui.contact.client.view.table.concise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contact.client.activity.table.concise.AbstractContactConciseTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.concise.AbstractContactConciseTableView;
import ru.protei.portal.ui.contact.client.view.table.ContactTableViewBase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContactConciseTableView extends Composite implements AbstractContactConciseTableView {

    @Inject
    public void onInit(EditClickColumn<Person> editClickColumn, RemoveClickColumn<Person> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractContactConciseTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);

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

    @Override
    public void showEditableColumns(boolean isVisible) {
        editColumn.setVisibility(isVisible);
        removeColumn.setVisibility(isVisible);
    }

    private void initTable() {

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CONTACT_EDIT) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.CONTACT_REMOVE) && !v.isDeleted() );

        columns.add(fio);
        columns.add(email);
        columns.add(phones);

        table.addColumn(fio.header, fio.values);
        table.addColumn(email.header, email.values);
        table.addColumn(phones.header, phones.values);
        editColumn = table.addColumn(editClickColumn.header, editClickColumn.values);
        removeColumn = table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    private void setTestAttributes() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONCISE_TABLE.CONTACT);
    }

    @UiField
    TableWidget<Person> table;

    @UiField
    HTMLPanel tableContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    PolicyService policyService;

    ClickColumn<Person> fio = new ClickColumn<Person>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contactFullName());
        }

        @Override
        public void fillColumnValue(Element cell, Person value) {
            Element fioElement = DOM.createDiv();
            fioElement.setInnerHTML(value.getDisplayName());
            cell.appendChild(fioElement);

            if ( value.getPosition() != null ) {
                Element posElement = DOM.createDiv();
                posElement.setInnerHTML("<small><i>" + value.getPosition() + "</i></small>");
                cell.appendChild(posElement);
            }

            if (value.isFired() || value.isDeleted()) {
                Element stateElement = DOM.createDiv();
                stateElement.setInnerHTML(ContactTableViewBase.makeFiredOrDeleted(value, lang));
                cell.appendChild(stateElement);
            }

        }
    };

    ClickColumn<Person> email = new ClickColumn<Person>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contactEmail());
        }

        @Override
        public void fillColumnValue(Element cell, Person value) {

            PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

            String emails = EmailRender.renderToHtml(infoFacade.emailsStream());
            if (StringUtils.isNotBlank(emails)) {
                com.google.gwt.dom.client.Element label = DOM.createLabel();
                label.setInnerHTML(emails);
                cell.appendChild(label);
            }

        }
    };

    ClickColumn<Person> phones = new ClickColumn<Person>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.contactPhone());
        }

        @Override
        public void fillColumnValue(Element cell, Person value) {
            PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(value.getContactInfo());

            Stream<ContactItem> contactItems = infoFacade.allPhonesStream();

            String phones = contactItems
                    .map(p -> "<span class=\"nowrap\">" + p.value() + "</span>")
                    .collect(Collectors.joining(", "));

            if (StringUtils.isNotBlank(phones)) {
                com.google.gwt.dom.client.Element label = DOM.createLabel();
                label.setInnerHTML(phones);
                cell.appendChild(label);
            }

        }
    };


    ClickColumnProvider<Person> columnProvider = new ClickColumnProvider<>();
    EditClickColumn<Person> editClickColumn;
    RemoveClickColumn<Person> removeClickColumn;
    List<ClickColumn> columns = new ArrayList<>();

    AbstractColumn editColumn;
    AbstractColumn removeColumn;
    AbstractContactConciseTableActivity activity;

    private static ContactConciseTableViewUiBinder ourUiBinder = GWT.create(ContactConciseTableViewUiBinder.class);
    interface ContactConciseTableViewUiBinder extends UiBinder<HTMLPanel, ContactConciseTableView> {}
}
