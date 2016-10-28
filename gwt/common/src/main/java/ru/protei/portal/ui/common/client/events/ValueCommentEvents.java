package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;

import java.util.List;

/**
 * События для активити ValueComment
 */
public class ValueCommentEvents {
    public static class ShowList {
        public ShowList(HasWidgets parent, List<ValueComment> data) {
            this.parent = parent;
            this.data = data;
        }

        public HasWidgets parent;
        public List<ValueComment> data;
    }
}
