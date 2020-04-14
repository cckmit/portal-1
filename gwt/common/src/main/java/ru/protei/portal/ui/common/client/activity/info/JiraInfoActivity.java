package ru.protei.portal.ui.common.client.activity.info;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.JiraInfoEvents;
import ru.protei.portal.ui.common.client.service.JiraStatusControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.stream.Collectors;

public abstract class JiraInfoActivity implements AbstractJiraInfoActivity, Activity {
    @Event
    public void onInit(AppEvents.InitDetails init) {
        this.init = init;
    }

    @Event
    public void onSuccess(AuthEvents.Success event) {
        view.setActivity(this);
        fillView();
    }

    @Event
    public void onShow(JiraInfoEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());
    }

    @Override
    public void onBackButtonClicked() {
        fireEvent(new IssueEvents.Show());
    }

    private void fillView() {
        jiraStatusService.getJiraStatusMapEntryList(new FluentCallback<List<JiraStatusMapEntry>>()
                .withSuccess(result -> {
                    List<JiraStatusInfo> infos = result.stream().map(JiraStatusInfo::fromJiraSlaMapEntry).collect(Collectors.toList());
                    view.setData(infos);
                })
        );

        view.setImage(CrmConstants.Jira.WORKFLOW_IMAGE);
    }

    public static class JiraStatusInfo {
        public String jiraStatus;
        public String crmStatus;
        public String definition;
        public String comment;

        static JiraStatusInfo fromJiraSlaMapEntry(JiraStatusMapEntry entry) {
            if (entry == null) {
                return null;
            }

            JiraStatusInfo jiraStatusInfo = new JiraStatusInfo();
            jiraStatusInfo.jiraStatus = entry.getJiraStatusName();
            jiraStatusInfo.crmStatus = entry.getLocalStatusName();
            jiraStatusInfo.definition = entry.getInfo();
            jiraStatusInfo.comment = En_CaseState.getById((long) entry.getLocalStatusId()).getComment();

            return jiraStatusInfo;
        }
    }

    @Inject
    AbstractJiraInfoView view;

    @Inject
    JiraStatusControllerAsync jiraStatusService;

    private AppEvents.InitDetails init;
}
