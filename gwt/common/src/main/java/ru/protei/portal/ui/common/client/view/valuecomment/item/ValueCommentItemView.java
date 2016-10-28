package ru.protei.portal.ui.common.client.view.valuecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentItemActivity;
import ru.protei.portal.ui.common.client.activity.valuecomment.AbstractValueCommentItemView;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueCommentPair;

/**
 * Created by bondarenko on 28.10.16.
 */
public class ValueCommentItemView extends Composite implements AbstractValueCommentItemView, ValueCommentPair{
    public ValueCommentItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractValueCommentItemActivity activity) {
        this.activity = activity;
    }


    @Override
    public HasText value() {
        return null;
    }

    @Override
    public HasText comment() {
        return null;
    }

    @UiField
    TextBox value;
    @UiField
    TextBox comment;
    @UiField
    Button button;
    @UiField
    Element icon;

    AbstractValueCommentItemActivity activity;

    private static ValueCommentItemViewUiBinder ourUiBinder = GWT.create(ValueCommentItemViewUiBinder.class);
    interface ValueCommentItemViewUiBinder extends UiBinder<HTMLPanel, ValueCommentItemView> {}
}
