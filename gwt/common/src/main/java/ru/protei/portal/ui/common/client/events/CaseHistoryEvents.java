package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.History;

import java.util.List;
import java.util.function.Consumer;

public class CaseHistoryEvents {
    public static class Load {
        public Load(Long caseId, HasWidgets container) {
            this.caseId = caseId;
            this.container = container;
        }

        public Long caseId;
        public HasWidgets container;
    }

    public static class Fill {
        public Fill(List<History> histories, Consumer<IsWidget> historyItemConsumer) {
            this.historyItemConsumer = historyItemConsumer;
            this.histories = histories;
        }

        public List<History> histories;
        public Consumer<IsWidget> historyItemConsumer;
    }

    public static class Reload {
        public Reload(Long caseId) {
            this.caseId = caseId;
        }

        public Long caseId;
    }
}
