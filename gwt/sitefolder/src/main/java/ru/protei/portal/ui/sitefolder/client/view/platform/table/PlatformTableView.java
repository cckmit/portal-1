package ru.protei.portal.ui.sitefolder.client.view.platform.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.AbstractPlatformTableActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.table.AbstractPlatformTableView;

import java.util.Collection;
import java.util.LinkedList;

public class PlatformTableView extends Composite implements AbstractPlatformTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractPlatformTableActivity activity) {
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
    public void setPlatformsCount(Long count) {
        table.setTotalRecords(count.intValue());
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
        table.scrollToPage(page);
    }

    @Override
    public void updateRow(Platform item) {
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

    private void initTable() {
        editClickColumn.setPrivilege(En_Privilege.SITE_FOLDER_EDIT);
        removeClickColumn.setPrivilege(En_Privilege.SITE_FOLDER_REMOVE);

        columns.add(nameColumn);
        columns.add(serversColumn);
        columns.add(editClickColumn);
        columns.add(removeClickColumn);

        columns.forEach(c -> table.addColumn(c.header, c.values));
    }

    @UiField
    InfiniteTableWidget<Platform> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    private EditClickColumn<Platform> editClickColumn;
    @Inject
    private RemoveClickColumn<Platform> removeClickColumn;
    private ClickColumn<Platform> nameColumn = new ClickColumn<Platform>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderName());
        }
        @Override
        public void fillColumnValue(Element cell, Platform value) {
            cell.setInnerText(value.getName());
        }
    };
    private ClickColumn<Platform> serversColumn = new ClickColumn<Platform>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.siteFolderServers());
        }

        @Override
        public void fillColumnValue(Element cell, Platform value) {
            cell.setInnerText((value.getServers() == null ? "0" : String.valueOf(value.getServers().size())) + " " +lang.amountShort());
        }
    };
    private Collection<ClickColumn<Platform>> columns = new LinkedList<>();
    private ClickColumnProvider<Platform> columnProvider = new ClickColumnProvider<>();

    interface SiteFolderTableViewUiBinder extends UiBinder<HTMLPanel, PlatformTableView> {}
    private static SiteFolderTableViewUiBinder ourUiBinder = GWT.create(SiteFolderTableViewUiBinder.class);
}
