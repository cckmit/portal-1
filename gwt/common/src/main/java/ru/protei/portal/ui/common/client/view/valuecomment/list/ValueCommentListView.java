package ru.protei.portal.ui.common.client.view.valuecomment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentListActivity;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentListView;

/**
 * Представление списка
 */
public class ValueCommentListView extends Composite implements AbstractValueCommentListView{
    public ValueCommentListView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public HasWidgets getItemsContainer() {
        return root;
    }

    @UiField
    HTMLPanel root;

    private static ValueCommentListViewUiBinder ourUiBinder = GWT.create(ValueCommentListViewUiBinder.class);
    interface ValueCommentListViewUiBinder extends UiBinder<HTMLPanel, ValueCommentListView> {}

}