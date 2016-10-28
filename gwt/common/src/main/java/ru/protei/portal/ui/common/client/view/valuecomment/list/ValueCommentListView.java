package ru.protei.portal.ui.common.client.view.valuecomment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentListActivity;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentListView;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueCommentDataList;
import ru.protei.portal.ui.common.client.view.valuecomment.item.ValueCommentItemView;
import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.ValueCommentPair;

import java.util.List;


/**
 * Created by bondarenko on 28.10.16.
 */
public class ValueCommentListView extends Composite implements AbstractValueCommentListView, ValueCommentDataList{
    public ValueCommentListView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractValueCommentListActivity activity) {
        this.activity = activity;
    }

    @Override
    public List<ValueCommentPair> getDataList() {
        return null;
    }

    @Override
    public void setDataList(List<ValueCommentPair> dataList) {

    }



    @UiField
    HTMLPanel root;
    @Inject
    Provider<ValueCommentItemView> itemFactory;

    AbstractValueCommentListActivity activity;


    private static ValueCommentListViewUiBinder ourUiBinder = GWT.create(ValueCommentListViewUiBinder.class);
    interface ValueCommentListViewUiBinder extends UiBinder<HTMLPanel, ValueCommentListView> {}

}