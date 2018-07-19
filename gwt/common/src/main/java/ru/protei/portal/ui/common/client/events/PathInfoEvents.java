package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.struct.PathItem;

import java.util.List;

public class PathInfoEvents {

    public static class ShowList {
        public ShowList(HasWidgets parent, List<PathItem> data) {
            this.parent = parent;
            this.data = data;
        }
        public HasWidgets parent;
        public List<PathItem> data;
    }
}
