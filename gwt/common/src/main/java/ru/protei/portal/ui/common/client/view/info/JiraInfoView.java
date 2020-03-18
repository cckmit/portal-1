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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.view.info.column.SimpleClickColumn;

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
        ClickColumn<JiraStatusInfo> jiraStatus = new SimpleClickColumn<JiraStatusInfo>()
                .withColumnHeaderConsumer(columnHeader -> columnHeader.setInnerText(lang.jiraInfoJiraStatus()))
                .withColumnValueConsumer((cell, value) -> cell.setInnerHTML(value.jiraStatus))
                .withClassName("jira-status");

        ClickColumn<JiraStatusInfo> crmStatus = new SimpleClickColumn<JiraStatusInfo>()
                .withColumnHeaderConsumer(columnHeader -> columnHeader.setInnerText(lang.jiraInfoCrmStatus()))
                .withColumnValueConsumer((cell, value) -> cell.setInnerHTML(value.crmStatus))
                .withClassName("crm-status");

        ClickColumn<JiraStatusInfo> definition = new SimpleClickColumn<JiraStatusInfo>()
                .withColumnHeaderConsumer(columnHeader -> columnHeader.setInnerText(lang.jiraInfoStatusDefinition()))
                .withColumnValueConsumer((cell, value) -> cell.setInnerHTML(value.definition))
                .withClassName("status-definition");

        ClickColumn<JiraStatusInfo> comment = new SimpleClickColumn<JiraStatusInfo>()
                .withColumnHeaderConsumer(columnHeader -> columnHeader.setInnerText(lang.jiraInfoStatusComment()))
                .withColumnValueConsumer((cell, value) -> cell.setInnerHTML(value.comment))
                .withClassName("status-comment");

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