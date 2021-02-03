package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.History;

import java.util.List;
import java.util.function.Consumer;

public class CaseHistoryEvents {
    public static class Fill {
        public Fill(FlowPanel historyContainer, List<History> histories) {
            this.historyContainer = historyContainer;
            this.histories = histories;
        }

        public FlowPanel historyContainer;
        public List<History> histories;
    }
}
