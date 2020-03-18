package ru.protei.portal.ui.common.client.view.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.ui.common.client.activity.info.AbstractJiraInfoActivity;
import ru.protei.portal.ui.common.client.activity.info.AbstractJiraInfoView;
import ru.protei.portal.ui.common.client.activity.info.JiraInfoActivity.JiraStatusInfo;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;

public class JiraJiraInfoView extends Composite implements AbstractJiraInfoView {
    public JiraJiraInfoView() {
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

    private void initTable() {
        table.addColumn(jiraStatus.header, jiraStatus.values);
        table.addColumn(crmStatus.header, crmStatus.values);
        table.addColumn(definition.header, definition.values);
        table.addColumn(comment.header, comment.values);
    }

    private ClickColumn<JiraStatusInfo> jiraStatus = new ClickColumn<JiraStatusInfo>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.jiraInfoJiraStatus());
            columnHeader.addClassName("jira-status");
        }

        @Override
        public void fillColumnValue(Element cell, JiraStatusInfo value) {
            Element jiraStatusElement = DOM.createDiv();
            jiraStatusElement.setInnerHTML(value.jiraStatus);
            cell.appendChild(jiraStatusElement);
            cell.addClassName("jira-status");
        }
    };

    private ClickColumn<JiraStatusInfo> crmStatus = new ClickColumn<JiraStatusInfo>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.jiraInfoCrmStatus());
            columnHeader.addClassName("crm-status");
        }

        @Override
        public void fillColumnValue(Element cell, JiraStatusInfo value) {
            Element crmStatus = DOM.createDiv();
            crmStatus.setInnerHTML(value.crmStatus);
            cell.appendChild(crmStatus);
            cell.addClassName("crm-status");
        }
    };

    private ClickColumn<JiraStatusInfo> definition = new ClickColumn<JiraStatusInfo>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.jiraInfoStatusDefinition());
            columnHeader.addClassName("status-definition");
        }

        @Override
        public void fillColumnValue(Element cell, JiraStatusInfo value) {
            Element definition = DOM.createDiv();
            definition.setInnerHTML(value.definition);
            cell.appendChild(definition);
            cell.addClassName("status-definition");
        }
    };

    private ClickColumn<JiraStatusInfo> comment = new ClickColumn<JiraStatusInfo>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.jiraInfoStatusComment());
            columnHeader.addClassName("status-comment");
        }

        @Override
        public void fillColumnValue(Element cell, JiraStatusInfo value) {
            Element comment = DOM.createDiv();
            comment.setInnerHTML(value.comment);
            cell.appendChild(comment);
            cell.addClassName("status-comment");
        }
    };

    @UiField
    TableWidget<JiraStatusInfo> table;

    @UiField
    ImageElement image;

    @UiField
    Lang lang;

    private AbstractJiraInfoActivity activity;

    interface JiraInfoViewUiBinder extends UiBinder<HTMLPanel, JiraJiraInfoView> {
    }
    private static JiraInfoViewUiBinder ourUiBinder = GWT.create(JiraInfoViewUiBinder.class);
}