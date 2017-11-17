package ru.protei.portal.ui.company.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.DynamicColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyTableActivity;
import ru.protei.portal.ui.company.client.activity.list.AbstractCompanyTableView;

/**
 * Created by bondarenko on 30.10.17.
 */
public class CompanyTableView extends Composite implements AbstractCompanyTableView{
    @Inject
    public void onInit(EditClickColumn<Company> editClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractCompanyTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        name.setHandler( activity );
        name.setColumnProvider( columnProvider );
        category.setHandler( activity );
        category.setColumnProvider( columnProvider );
        group.setHandler( activity );
        group.setColumnProvider( columnProvider );
        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public void setIssuesCount(Long issuesCount) {
        table.setTotalRecords( issuesCount.intValue() );
    }

    @Override
    public int getPageSize() {
        return table.getPageSize();
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage( page );
    }

    @Override
    public void updateRow(Company item) {
        if(item != null)
            table.updateRow(item);
    }

    private void initTable () {
        editClickColumn.setPrivilege( En_Privilege.COMPANY_EDIT );
        name = new DynamicColumn<>(lang.companyName(), "company-main-info", this::getCompanyInfoBlock);
        category = new DynamicColumn<>(
            lang.companyCategory(),
            "company-category",
            company -> company.getCategory() != null? company.getCategory().getName(): ""
        );
        group = new DynamicColumn<>(
                lang.companyGroup(),
                "company-group",
                company -> company.getCompanyGroup() != null? company.getCompanyGroup().getName(): ""
        );

        table.addColumn( name.header, name.values );
        table.addColumn( category.header, category.values );
        table.addColumn( group.header, group.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
    }

    private String getCompanyInfoBlock(Company company){
        Element companyInfo = DOM.createDiv();

        Element cName = DOM.createDiv();
        cName.addClassName("company-name");
        cName.setInnerText(company.getCname());
        companyInfo.appendChild(cName);

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(company.getContactInfo());
        String phones = infoFacade.allPhonesAsString();
        String emails = infoFacade.allEmailsAsString();
        String website = infoFacade.getWebSite();

        if(!phones.isEmpty())
            companyInfo.appendChild(buildContactsElement("fa fa-phone", phones));

        if(!emails.isEmpty())
            companyInfo.appendChild(buildContactsElement("fa fa-envelope", emails));

        if(!website.isEmpty())
            companyInfo.appendChild(buildContactsElement("fa fa-globe", website));

        return companyInfo.getString();
    }

    private Element buildContactsElement(String iconClass, String contacts){
        Element icon = DOM.createElement("i");
        icon.addClassName(iconClass);

        Element data = DOM.createSpan();
        data.setInnerText(contacts);

        Element wrapper = DOM.createDiv();
        wrapper.addClassName("contacts");
        wrapper.appendChild(icon);
        wrapper.appendChild(data);

        return wrapper;
    }

    @UiField
    InfiniteTableWidget<Company> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    Lang lang;


    ClickColumnProvider< Company > columnProvider = new ClickColumnProvider<>();
    EditClickColumn< Company > editClickColumn;
    DynamicColumn<Company> name;
    DynamicColumn<Company> category;
    DynamicColumn<Company> group;

    AbstractCompanyTableActivity activity;

    private static CompanyTableViewUiBinder ourUiBinder = GWT.create(CompanyTableViewUiBinder.class);
    interface CompanyTableViewUiBinder extends UiBinder<HTMLPanel, CompanyTableView> {}
}