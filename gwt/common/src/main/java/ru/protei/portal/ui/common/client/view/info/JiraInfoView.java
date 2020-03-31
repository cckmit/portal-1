package ru.protei.portal.ui.common.client.view.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.ui.common.client.activity.info.AbstractJiraInfoActivity;
import ru.protei.portal.ui.common.client.activity.info.AbstractJiraInfoView;
import ru.protei.portal.ui.common.client.activity.info.JiraInfoActivity.JiraStatusInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.DynamicColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;

public class JiraInfoView extends Composite implements AbstractJiraInfoView {
    public JiraInfoView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractJiraInfoActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setData(List<JiraStatusInfo> infos) {
        for (JiraStatusInfo info : infos) {
            table.addRow(info);
        }
    }

    @Override
    public void setImage(String imageUrl) {
        image.setSrc(imageUrl);
    }

    @UiHandler("backButton")
    public void onBackButtonClicked(ClickEvent event) {
        activity.onBackButtonClicked();
    }

    private void initTable() {
        ClickColumn<JiraStatusInfo> jiraStatus = new DynamicColumn<>(lang.jiraInfoJiraStatus(), "jira-status", jiraStatusInfo -> jiraStatusInfo.jiraStatus);
        ClickColumn<JiraStatusInfo> crmStatus = new DynamicColumn<>(lang.jiraInfoCrmStatus(), "crm-status", jiraStatusInfo -> jiraStatusInfo.crmStatus);
        ClickColumn<JiraStatusInfo> definition = new DynamicColumn<>(lang.jiraInfoStatusDefinition(), "status-definition", jiraStatusInfo -> jiraStatusInfo.definition);
        ClickColumn<JiraStatusInfo> comment = new DynamicColumn<>(lang.jiraInfoStatusComment(), "status-comment", jiraStatusInfo -> jiraStatusInfo.comment);

        table.addColumn(jiraStatus.header, jiraStatus.values);
        table.addColumn(crmStatus.header, crmStatus.values);
        table.addColumn(definition.header, definition.values);
        table.addColumn(comment.header, comment.values);
    }

    @UiField
    TableWidget<JiraStatusInfo> table;

    @UiField
    ImageElement image;

    @UiField
    Lang lang;

    @UiField
    Button backButton;

    private AbstractJiraInfoActivity activity;

    interface JiraInfoViewUiBinder extends UiBinder<HTMLPanel, JiraInfoView> {
    }
    private static JiraInfoViewUiBinder ourUiBinder = GWT.create(JiraInfoViewUiBinder.class);
}