package ru.protei.portal.ui.sitefolder.client.view.server.listdetailed.item;

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
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.item.AbstractServerDetailedListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.item.AbstractServerDetailedListItemView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class ServerDetailedListItemView extends Composite implements AbstractServerDetailedListItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractServerDetailedListItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String name) {
        if (HelperFunc.isNotEmpty(name)) {
            this.name.setInnerHTML("<b>" + name + "</b>");
        } else {
            this.name.addClassName(HIDE);
        }
    }

    @Override
    public void setParameters(String parameters) {
        if (HelperFunc.isNotEmpty(parameters)) {
            this.param.setInnerText(parameters);
        } else {
            this.param.addClassName(HIDE);
        }
    }

    @Override
    public void setApps(String apps) {
        if (HelperFunc.isNotEmpty(apps)) {
            this.apps.setInnerText(apps);
            this.apps.removeClassName(HIDE);
        } else {
            this.apps.addClassName(HIDE);
        }
    }

    @Override
    public void setComment(String comment) {
        if (HelperFunc.isNotEmpty(comment)) {
            this.comment.setInnerText(comment);
            this.comment.removeClassName(HIDE);
        } else {
            this.comment.addClassName(HIDE);
        }
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

    private void setTestAttributes() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.ITEM);
        name.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.NAME);
        apps.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.APPS);
        comment.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.COMMENT);
        param.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.PARAMS);
        edit.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SITE_FOLDER.SERVER.EDIT_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @UiField
    Anchor edit;
    @UiField
    SpanElement name;
    @UiField
    SpanElement apps;
    @UiField
    SpanElement comment;
    @UiField
    SpanElement param;

    private String nameValue = null;
    private AbstractServerDetailedListItemActivity activity;

    interface ServerDetailedListItemViewUiBinder extends UiBinder<HTMLPanel, ServerDetailedListItemView> {}
    private static ServerDetailedListItemViewUiBinder ourUiBinder = GWT.create(ServerDetailedListItemViewUiBinder.class);
}
