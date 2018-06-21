package ru.protei.portal.ui.casestate.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableActivity;
import ru.protei.portal.ui.casestate.client.activity.table.AbstractCaseStateTableView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;

import java.util.ArrayList;
import java.util.List;

public class CaseStateTableView extends Composite implements AbstractCaseStateTableView {

    private AbstractCaseStateTableActivity activity;

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractCaseStateTableActivity activity) {
        this.activity = activity;
        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public void setData(List<CaseState> result) {
        for ( CaseState role : result ) {
            table.addRow( role );
        }

    }


    private void initTable() {
        ClickColumn<CaseState> name = new ClickColumn<CaseState>() {
            @Override
            protected void fillColumnHeader(Element element) {
                element.setInnerText("Статус");
            }

            @Override
            public void fillColumnValue(Element cell, CaseState value) {
                cell.setInnerText(value.getState() == null ? "" : value.getState());
            }
        };
        columns.add(name);

        table.addColumn(name.header, name.values);

    }

    private ClickColumnProvider<CaseState> columnProvider = new ClickColumnProvider<>();
    private List<ClickColumn> columns = new ArrayList<>();
    @UiField
    TableWidget<CaseState> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    private static CaseStateTableViewUiBinder ourUiBinder = GWT.create(CaseStateTableViewUiBinder.class);


    interface CaseStateTableViewUiBinder extends UiBinder<HTMLPanel, CaseStateTableView> {
    }
}
