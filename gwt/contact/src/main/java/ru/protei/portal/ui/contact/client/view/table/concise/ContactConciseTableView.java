package ru.protei.portal.ui.contact.client.view.table.concise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contact.client.activity.table.concise.AbstractContactConciseTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.concise.AbstractContactConciseTableView;
import ru.protei.portal.ui.contact.client.view.table.ContactTableViewBase;

import java.util.ArrayList;
import java.util.List;

public class ContactConciseTableView extends ContactTableViewBase implements AbstractContactConciseTableView {

    @Inject
    public void onInit(EditClickColumn<Person> editClickColumn, RemoveClickColumn<Person> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
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

        ClickColumn<Person> displayName = getDisplayNameColumn(lang);
        columns.add(displayName);

        ClickColumn<Person> company = getCompanyColumn(lang);
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
    EditClickColumn<Person> editClickColumn;
    RemoveClickColumn<Person> removeClickColumn;
    List<ClickColumn> columns = new ArrayList<>();

    AbstractContactConciseTableActivity activity;

    private static ContactConciseTableViewUiBinder ourUiBinder = GWT.create(ContactConciseTableViewUiBinder.class);
    interface ContactConciseTableViewUiBinder extends UiBinder<HTMLPanel, ContactConciseTableView> {}
}
