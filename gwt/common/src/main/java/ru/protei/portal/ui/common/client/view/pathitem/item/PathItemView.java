package ru.protei.portal.ui.common.client.view.pathitem.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.pathitem.item.AbstractPathItemActivity;
import ru.protei.portal.ui.common.client.activity.pathitem.item.AbstractPathItemView;
import ru.protei.portal.ui.common.client.lang.Lang;

public class PathItemView extends Composite implements AbstractPathItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        path.getElement().setPropertyString("placeholder", lang.siteFolderPath());
        desc.getElement().setPropertyString("placeholder", lang.description());
    }

    @Override
    public void setActivity(AbstractPathItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasText path() {
        return path;
    }

    @Override
    public HasText desc() {
        return desc;
    }

    @Override
    public void focused() {
        path.setFocus(true);
    }

    @UiHandler("path")
    public void onChangeCommentField(KeyUpEvent event) {
        if (activity != null) {
            activity.onChangePath(this);
        }
    }

    @UiHandler("desc")
    public void onChangeInputField(KeyUpEvent event) {
        if (activity != null) {
            activity.onChangeDesc(this);
        }
    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    TextBox path;
    @UiField
    TextBox desc;

    private AbstractPathItemActivity activity;

    interface PathItemViewUiBinder extends UiBinder<HTMLPanel, PathItemView> {}
    private static PathItemViewUiBinder ourUiBinder = GWT.create(PathItemViewUiBinder.class);
}
