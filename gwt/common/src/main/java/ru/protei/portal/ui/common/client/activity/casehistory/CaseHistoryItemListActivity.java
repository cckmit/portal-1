package ru.protei.portal.ui.common.client.activity.casehistory;

import com.google.gwt.user.client.ui.FlowPanel;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.CaseHistoryEvents;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItemsContainer;

import java.util.LinkedList;
import java.util.List;

import static ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils.fillView;

public abstract class CaseHistoryItemListActivity implements AbstractCaseHistoryItemListActivity, Activity {
    @Event
    public void onInit(CaseHistoryEvents.Init event) {
        this.historyContainer = event.historyContainer;
    }

    @Event
    public void onFill(CaseHistoryEvents.Fill event) {
        historyItemsContainers.addAll(fillView(event.histories, historyContainer));
    }

    @Event
    public void onShow(CaseHistoryEvents.Show event) {
        historyItemsContainers.forEach(historyItemsContainer -> historyItemsContainer.setVisible(true));
    }

    @Event
    public void onHide(CaseHistoryEvents.Hide event) {
        historyItemsContainers.forEach(historyItemsContainer -> historyItemsContainer.setVisible(false));
    }

    @Event
    public void onClear(CaseHistoryEvents.Clear event) {
        historyItemsContainers.clear();
    }

    private FlowPanel historyContainer;
    private final List<CaseHistoryItemsContainer> historyItemsContainers = new LinkedList<>();
}
