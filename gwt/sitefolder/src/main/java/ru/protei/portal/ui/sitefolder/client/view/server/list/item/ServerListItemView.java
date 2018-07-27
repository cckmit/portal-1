package ru.protei.portal.ui.sitefolder.client.view.server.list.item;

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
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractServerListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractServerListItemView;

public class ServerListItemView extends Composite implements AbstractServerListItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractServerListItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setIp(String ip) {
        this.ip.setInnerText(ip);
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
    SpanElement ip;
    @UiField
    SpanElement comment;
    @UiField
    Anchor edit;
    @UiField
    Anchor remove;

    private AbstractServerListItemActivity activity;

    interface SiteFolderServerListItemViewUiBinder extends UiBinder<HTMLPanel, ServerListItemView> {}
    private static SiteFolderServerListItemViewUiBinder ourUiBinder = GWT.create(SiteFolderServerListItemViewUiBinder.class);
}
