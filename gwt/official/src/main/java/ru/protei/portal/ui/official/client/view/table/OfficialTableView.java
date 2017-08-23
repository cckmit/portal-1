package ru.protei.portal.ui.official.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.AttachClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.official.client.activity.table.AbstractOfficialTableView;
import ru.protei.portal.ui.official.client.activity.table.AbstractOfficialsTableActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by serebryakov on 21/08/17.
 */
public class OfficialTableView extends Composite implements AbstractOfficialTableView {

    @Inject
    private void onInit(EditClickColumn<Official> editClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        initTable();
    }

    private void initTable() {
        attachColumn = new AttachClickColumn<Official>(lang) {};
        editClickColumn.setPrivilege( En_Privilege.OFFICIAL_EDIT);

        ClickColumn<Official> productColumn = new ClickColumn<Official>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.officialTableProduct());
            }

            @Override
            public void fillColumnValue(Element cell, Official value) {
                Element root = DOM.createDiv();
                cell.appendChild( root );

                Element productElement = DOM.createDiv();
                productElement.setInnerHTML(value.getProductName() );
                root.appendChild( productElement );
            }
        };
        columns.add(productColumn);

        ClickColumn<Official> infoColumn = new ClickColumn<Official>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("info");
                columnHeader.setInnerText(lang.officialTableInfo());
            }

            @Override
            public void fillColumnValue(Element cell, Official value) {
                cell.addClassName( "info" );

                Element divElement = DOM.createDiv();

                Element infoElement = DOM.createLabel();

                infoElement.setInnerText( value == null ? "" : value.getInfo() == null ? "" : value.getInfo() );

                divElement.appendChild( infoElement );

                Date createTime = value == null ? null : value.getCreated();

                if ( createTime != null ) {
                    Element groupElement = DOM.createElement( "p" );
                    groupElement.addClassName( "text-semimuted" );
                    groupElement.addClassName( "no-margin" );

                    Element i = DOM.createElement( "i" );
                    i.addClassName( "fa fa-clock-o" );
                    groupElement.appendChild( i );

                    Element createdElement = DOM.createSpan();
                    createdElement.setInnerText( " " + DateFormatter.formatDateTime( createTime ) );
                    groupElement.appendChild( createdElement );

                    divElement.appendChild( groupElement );
                }

                cell.appendChild( divElement );
            }
        };
        columns.add(infoColumn);

        ClickColumn<Official> numberEmployeesColumn = new ClickColumn<Official>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.officialTableNumberEmployees() );
            }

            @Override
            public void fillColumnValue(Element cell, Official value) {
                Element rootElement = DOM.createDiv();
                rootElement.setInnerText(value.getNumberEmployees());
                cell.appendChild(rootElement);
            }
        };
        columns.add(numberEmployeesColumn);

        table.addColumn(productColumn.header, productColumn.values);
        table.addColumn(infoColumn.header, infoColumn.values);
        table.addColumn(numberEmployeesColumn.header, numberEmployeesColumn.values);
        table.addColumn(attachColumn.header, attachColumn.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
    }


    @Override
    public void setActivity(AbstractOfficialsTableActivity activity) {
        this.activity = activity;
        attachColumn.setAttachHandler(activity);
        table.setLoadHandler( activity );
    }

    @Override
    public void setAnimation(TableAnimation animation) {

    }

    @Override
    public void hideElements() {

    }

    @Override
    public void showElements() {

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
    public void setRecordCount(Long count) {
        table.setTotalRecords(count.intValue());
    }

    @Override
    public int getPageSize() {
        return 0;
    }

    @Override
    public int getPageCount() {
        return 0;
    }


    private AbstractOfficialsTableActivity activity;
    private List<ClickColumn> columns = new ArrayList<ClickColumn>();

    @Inject
    Lang lang;

    AttachClickColumn<Official> attachColumn;

    private EditClickColumn<Official> editClickColumn;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    InfiniteTableWidget<Official> table;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    interface OfficialTableViewUiBinder extends UiBinder<HTMLPanel, OfficialTableView> {}
    private static OfficialTableViewUiBinder ourUiBinder = GWT.create(OfficialTableViewUiBinder.class);

}