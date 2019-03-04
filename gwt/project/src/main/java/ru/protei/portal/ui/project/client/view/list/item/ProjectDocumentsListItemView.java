package ru.protei.portal.ui.project.client.view.list.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;

import static ru.protei.portal.ui.common.client.common.UiConstants.Icons;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles;
import ru.protei.portal.ui.project.client.activity.list.item.AbstractProjectDocumentsListItemActivity;
import ru.protei.portal.ui.project.client.activity.list.item.AbstractProjectDocumentsListItemView;

public class ProjectDocumentsListItemView extends Composite implements AbstractProjectDocumentsListItemView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractProjectDocumentsListItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setApproved(Boolean isApproved) {
        approve.addClassName(isApproved ? Icons.APPROVED : Icons.NOT_APPROVED);
    }

    @Override
    public void setDecimalNumber(String number) {
        if (StringUtils.isNotBlank(number)) {
            decimalNumber.setInnerText(number.trim());
            decimalNumber.removeClassName(Styles.HIDE);
        } else {
            decimalNumber.addClassName(Styles.HIDE);
        }
    }

    @Override
    public void setInfo(String info) {
        if (StringUtils.isNotBlank(info)) {
            information.setInnerText(info.trim());
            information.removeClassName(Styles.HIDE);
        } else {
            information.addClassName(Styles.HIDE);
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

    @UiHandler("download")
    public void copyClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onDownloadClicked(this);
        }
    }

    @UiField
    Element approve;
    @UiField
    SpanElement decimalNumber;
    @UiField
    SpanElement information;
    @UiField
    Anchor edit;
    @UiField
    Anchor download;

    private AbstractProjectDocumentsListItemActivity activity;

    interface ProjectDocumentsListItemUiBinder extends UiBinder<HTMLPanel, ProjectDocumentsListItemView> {}
    private static ProjectDocumentsListItemUiBinder ourUiBinder = GWT.create(ProjectDocumentsListItemUiBinder.class);
}
