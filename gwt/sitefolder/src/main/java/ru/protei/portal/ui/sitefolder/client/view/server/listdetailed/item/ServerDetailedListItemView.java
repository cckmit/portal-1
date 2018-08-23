package ru.protei.portal.ui.sitefolder.client.view.server.listdetailed.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.item.AbstractServerDetailedListItemActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.item.AbstractServerDetailedListItemView;

public class ServerDetailedListItemView extends Composite implements AbstractServerDetailedListItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractServerDetailedListItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String name) {
        this.nameValue = name;
        if (HelperFunc.isNotEmpty(name)) {
            this.name.setInnerHTML("<b>" + name + "</b>");
        }
    }

    @Override
    public void setParameters(String parameters) {
        String value = "";
        if (HelperFunc.isNotEmpty(nameValue)) {
            value += "<b>" + nameValue + "</b>";
        }
        if (HelperFunc.isNotEmpty(parameters)) {
            if (HelperFunc.isNotEmpty(value)) {
                value += " - ";
            }
            value += parameters;
        }
        this.name.setInnerHTML(value);
    }

    @Override
    public void setApps(String apps) {
        if (HelperFunc.isNotEmpty(apps)) {
            this.apps.setInnerText(apps);
            this.apps.removeClassName("hide");
        } else {
            this.apps.addClassName("hide");
        }
    }

    @Override
    public void setComment(String comment) {
        if (HelperFunc.isNotEmpty(comment)) {
            this.comment.setInnerText(comment);
            this.comment.removeClassName("hide");
        } else {
            this.comment.addClassName("hide");
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

    private String nameValue = null;
    private AbstractServerDetailedListItemActivity activity;

    interface ServerDetailedListItemViewUiBinder extends UiBinder<HTMLPanel, ServerDetailedListItemView> {}
    private static ServerDetailedListItemViewUiBinder ourUiBinder = GWT.create(ServerDetailedListItemViewUiBinder.class);
}