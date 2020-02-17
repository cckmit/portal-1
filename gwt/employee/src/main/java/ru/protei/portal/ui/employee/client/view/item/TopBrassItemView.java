package ru.protei.portal.ui.employee.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import ru.protei.portal.ui.employee.client.activity.item.AbstractTopBrassItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractTopBrassItemView;

public class TopBrassItemView extends Composite implements AbstractTopBrassItemView {
    public TopBrassItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractTopBrassItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setImage(String url) {
        image.setUrl(url);
    }

    @Override
    public void setPosition(String position) {

    }

    @UiField
    Image image;

    @UiField
    DivElement position;

    private AbstractTopBrassItemActivity activity;

    interface TopBrassItemViewUiBinder extends UiBinder<HTMLPanel, TopBrassItemView> {}
    private static TopBrassItemViewUiBinder ourUiBinder = GWT.create(TopBrassItemViewUiBinder.class);
}