package ru.protei.portal.ui.sitefolder.client.view.app.table;

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
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.sitefolder.client.activity.app.table.AbstractApplicationTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.table.AbstractApplicationTableView;

import java.util.Collection;
import java.util.LinkedList;

public class ApplicationTableView extends Composite implements AbstractApplicationTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractApplicationTableActivity activity) {
        editClickColumn.setEditHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        table.setLoadHandler(activity);
        table.setPagerListener(activity);
        columns.forEach(c -> {
            c.setHandler(activity);
            c.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
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
    public void updateRow(Application item) {
        if (item != null) {
            table.updateRow(item);
        }
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
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    private void initTable() {
        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT) );
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_REMOVE) );

        columns.add(nameColumn);
        columns.add(serverColumn);
        columns.add(pathsColumn);
        columns.add(editClickColumn);
        columns.add(removeClickColumn);

        columns.forEach(c -> table.addColumn(c.header, c.values));
    }

    @UiField
    InfiniteTableWidget<Application> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    PolicyService policyService;

    @Inject
    private EditClickColumn<Application> editClickColumn;
    @Inject
    private RemoveClickColumn<Application> removeClickColumn;
    private ClickColumn<Application> nameColumn = new ClickColumn<Application>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setClassName("application-name");
            columnHeader.setInnerText(lang.siteFolderName());
        }
        @Override
        public void fillColumnValue(Element cell, Application value) {
            Element element = DOM.createDiv();

            element.setInnerText(value.getName());

            cell.appendChild(element);
        }
    };
    private ClickColumn<Application> serverColumn = new ClickColumn<Application>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setClassName("application-server");
            columnHeader.setInnerText(lang.siteFolderServer());
        }

        @Override
        public void fillColumnValue(Element cell, Application value) {
            Element element = DOM.createDiv();

            element.setInnerText(value.getServer() == null ? "?" : value.getServer().getName());

            cell.appendChild(element);
        }
    };
    private ClickColumn<Application> pathsColumn = new ClickColumn<Application>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setClassName("application-paths");
            columnHeader.setInnerText(lang.siteFolderPaths());
        }

        @Override
        public void fillColumnValue(Element cell, Application value) {
            Element element = DOM.createDiv();

            element.setInnerText((value.getPaths() == null || value.getPaths().getPaths() == null ? "0" : String.valueOf(value.getPaths().getPaths().size())) + " " + lang.amountShort());

            cell.appendChild(element);
        }
    };
    private Collection<ClickColumn<Application>> columns = new LinkedList<>();
    private ClickColumnProvider<Application> columnProvider = new ClickColumnProvider<>();

    interface SiteFolderAppTableViewUiBinder extends UiBinder<HTMLPanel, ApplicationTableView> {}
    private static SiteFolderAppTableViewUiBinder ourUiBinder = GWT.create(SiteFolderAppTableViewUiBinder.class);
}
