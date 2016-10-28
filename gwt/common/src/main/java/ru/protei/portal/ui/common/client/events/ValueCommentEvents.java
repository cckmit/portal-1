package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueCommentPair;

import java.util.List;

/**
 * Created by turik on 27.09.16.
 */
public class ValueCommentEvents {
    public static class ShowList {
        public ShowList(HasWidgets parent, List<ValueCommentPair> data) {
            this.parent = parent;
            this.data = data;
        }

        public HasWidgets parent;
        public List<ValueCommentPair> data;
    }
}
