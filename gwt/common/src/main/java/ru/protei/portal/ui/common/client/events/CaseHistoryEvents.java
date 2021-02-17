package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.History;

import java.util.List;
import java.util.function.Consumer;

public class CaseHistoryEvents {
    public static class Init {
        public Init(FlowPanel historyContainer) {
            this.historyContainer = historyContainer;
        }

        public FlowPanel historyContainer;
    }

    public static class Fill {
        public Fill(List<History> histories) {
            this.histories = histories;
        }

        public List<History> histories;
    }

    public static class Clear {}

    public static class Show {}

    public static class Hide {}
}
