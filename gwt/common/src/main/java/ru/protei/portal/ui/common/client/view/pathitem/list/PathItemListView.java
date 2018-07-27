package ru.protei.portal.ui.common.client.view.pathitem.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.common.client.activity.pathitem.list.AbstractPathItemListView;

public class PathItemListView  extends Composite implements AbstractPathItemListView {

    public PathItemListView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public HasWidgets getItemsContainer() {
        return root;
    }

    @UiField
    HTMLPanel root;

    interface PathItemListViewUiBinder extends UiBinder<HTMLPanel, PathItemListView> {}
    private static PathItemListViewUiBinder ourUiBinder = GWT.create(PathItemListViewUiBinder.class);
}
