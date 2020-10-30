package ru.protei.portal.ui.sitefolder.client.view.server.list.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractServerListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.item.AbstractServerListItemView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ServerListItemView extends Composite implements AbstractServerListItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
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

    @Override
    public void setCopyVisible(boolean visible) {
        copy.setVisible(visible);
    }

    @Override
    public void setRemoveVisible(boolean visible) {
        remove.setVisible(visible);
    }

    @Override
    public void setParams(String params) {
        this.params.setInnerText(params);
    }

    @UiHandler("edit")
    public void editClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onEditClicked(this);
        }
    }

    @UiHandler("copy")
    public void copyClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onCopyClicked(this);
        }
    }

    @UiHandler("remove")
    public void removeClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onRemoveClicked(this);
        }
    }

    private void setTestAttributes() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.ITEM);
        name.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.NAME);
        ip.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.IP);
        comment.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.COMMENT);
        params.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.PARAMS);
        edit.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.EDIT_BUTTON);
        copy.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.COPY_BUTTON);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.REMOVE_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @UiField
    SpanElement name;
    @UiField
    SpanElement ip;
    @UiField
    SpanElement comment;
    @UiField
    SpanElement params;
    @UiField
    Anchor edit;
    @UiField
    Anchor copy;
    @UiField
    Anchor remove;

    private AbstractServerListItemActivity activity;

    interface SiteFolderServerListItemViewUiBinder extends UiBinder<HTMLPanel, ServerListItemView> {}
    private static SiteFolderServerListItemViewUiBinder ourUiBinder = GWT.create(SiteFolderServerListItemViewUiBinder.class);
}
