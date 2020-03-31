package ru.protei.portal.ui.education.client.view.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.EducationEntryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.education.client.activity.admin.AbstractEducationAdminActivity;
import ru.protei.portal.ui.education.client.activity.admin.AbstractEducationAdminView;

public class EducationAdminView extends Composite implements AbstractEducationAdminView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractEducationAdminActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
    }

    private void initTable() {
        table.setLoadHandler(activity);
        table.setPagerListener(activity);
        columnProvider = new ClickColumnProvider<>();

        table.addColumn(approveColumn.header, approveColumn.values);
        approveColumn.setColumnProvider(columnProvider);

        table.addColumn(titleColumn.header, titleColumn.values);
        titleColumn.setColumnProvider(columnProvider);

        table.addColumn(typeColumn.header, typeColumn.values);
        typeColumn.setColumnProvider(columnProvider);

        table.addColumn(coinsColumn.header, coinsColumn.values);
        coinsColumn.setColumnProvider(columnProvider);

        table.addColumn(editClickColumn.header, editClickColumn.values);
        editClickColumn.setActionHandler(value -> activity.onEditClicked(value));
        editClickColumn.setColumnProvider(columnProvider);
    }

    @Inject
    @UiField
    Lang lang;
    @Inject
    EducationEntryTypeLang typeLang;

    @UiField
    InfiniteTableWidget<EducationEntry> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject private EditClickColumn<EducationEntry> editClickColumn;
    private final ClickColumn<EducationEntry> approveColumn = new ClickColumn<EducationEntry>() {
        protected void fillColumnHeader(Element columnHeader) {}
        protected void fillColumnValue(Element cell, EducationEntry value) {
//            String icon = value.isApproved() ? "fas fa-thumbs-up" : "far fa-question-circle"; // TODO will be changed to attendance entry
            String icon = "fas fa-thumbs-up";
            cell.setInnerHTML("<i class='" + icon + "'></i>");
        }
    };
    private ClickColumn<EducationEntry> titleColumn = new ClickColumn<EducationEntry>() {
        protected void fillColumnHeader(Element columnHeader) { columnHeader.setInnerText(lang.educationEntryTitle()); }
        public void fillColumnValue(Element cell, EducationEntry value) { cell.setInnerText(value.getTitle()); }
    };
    private ClickColumn<EducationEntry> typeColumn = new ClickColumn<EducationEntry>() {
        protected void fillColumnHeader(Element columnHeader) { columnHeader.setInnerText(lang.educationEntryType()); }
        public void fillColumnValue(Element cell, EducationEntry value) { cell.setInnerText(typeLang.getName(value.getType())); }
    };
    private ClickColumn<EducationEntry> coinsColumn = new ClickColumn<EducationEntry>() {
        protected void fillColumnHeader(Element columnHeader) { columnHeader.setInnerText(lang.educationEntryCoins()); }
        public void fillColumnValue(Element cell, EducationEntry value) { cell.setInnerText(String.valueOf(value.getCoins())); }
    };

    private ClickColumnProvider<EducationEntry> columnProvider = new ClickColumnProvider<>();
    private AbstractEducationAdminActivity activity;

    interface EducationAdminViewUiBinder extends UiBinder<HTMLPanel, EducationAdminView> {}
    private static EducationAdminViewUiBinder ourUiBinder = GWT.create(EducationAdminViewUiBinder.class);
}
