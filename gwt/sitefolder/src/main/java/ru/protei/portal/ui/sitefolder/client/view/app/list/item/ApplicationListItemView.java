package ru.protei.portal.ui.sitefolder.client.view.app.list.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.item.AbstractApplicationListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.item.AbstractApplicationListItemView;

public class ApplicationListItemView extends Composite implements AbstractApplicationListItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractApplicationListItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setComment(String comment) {
        this.comment.setInnerText(comment);
    }

    @Override
    public void setEditVisible(boolean visible) {
        edit.setVisible(visible);
    }

    @UiHandler("edit")
    public void editClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onEditClicked(this);
        }
    }

    @UiHandler("remove")
    public void removeClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onRemoveClicked(this);
        }
    }

    @UiField
    SpanElement name;
    @UiField
    SpanElement comment;
    @UiField
    Anchor edit;
    @UiField
    Anchor remove;

    private AbstractApplicationListItemActivity activity;

    interface SiteFolderAppListItemViewUiBinder extends UiBinder<HTMLPanel, ApplicationListItemView> {}
    private static SiteFolderAppListItemViewUiBinder ourUiBinder = GWT.create(SiteFolderAppListItemViewUiBinder.class);
}
