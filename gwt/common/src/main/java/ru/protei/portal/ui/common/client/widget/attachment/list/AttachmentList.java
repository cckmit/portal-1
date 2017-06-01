package ru.protei.portal.ui.common.client.widget.attachment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentActivity;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentView;
import ru.protei.portal.ui.common.client.activity.attachment.AttachmentType;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bondarenko on 17.01.17.
 */
public class AttachmentList extends Composite implements HasAttachments, HasAttachmentListHandlers, AbstractAttachmentActivity{

    public AttachmentList() {
        initWidget(ourUiBinder.createAndBindUi(this));
        viewToAttachment = new HashMap<>();
    }

    @Override
    public void add(Attachment attachment){
        if(attachment == null)
            return;

        AbstractAttachmentView view = attachmentViewFactory.get();
        view.setActivity(this);
        view.setFileName(attachment.getFileName());
        view.setFileSize(attachment.getDataSize());
        view.setDownloadUrl(DOWNLOAD_PATH + attachment.getExtLink());

        if(!isSimpleMode) {
            AttachmentType.AttachmentCategory category = AttachmentType.getCategory(attachment.getMimeType());
            if (category == AttachmentType.AttachmentCategory.IMAGE)
                view.setPicture(DOWNLOAD_PATH + attachment.getExtLink());
            else
                view.setPicture(category.picture);
        }else
            view.asWidget().addStyleName("attach-minimize");

        if(isHiddenControls){
            view.asWidget().addStyleName("attach-hide-controls");
        }

        viewToAttachment.put(view, attachment);

        add(view.asWidget());
    }

    @Override
    public void remove(Attachment attachment) {
        for (AbstractAttachmentView view : viewToAttachment.keySet()) {
            if (viewToAttachment.get(view).equals(attachment)) {
                view.asWidget().removeFromParent();
                viewToAttachment.remove(view);
                return;
            }
        }
    }

    @Override
    public void add(Widget w) {
        attachmentList.add(w);
    }

    @Override
    public void clear() {
        attachmentList.clear();
        viewToAttachment.clear();
    }

    @Override
    public Collection<Attachment> getAll() {
        return viewToAttachment.values();
    }

    @Override
    public Iterator<Widget> iterator() {
        return attachmentList.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return attachmentList.remove(w);
    }

    @Override
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        return addHandler( handler, RemoveEvent.getType() );
    }

    @Override
    public void onAttachmentRemove(AbstractAttachmentView attachment) {
        RemoveEvent.fire( this, viewToAttachment.get(attachment) );
    }

    public void setSimpleMode(boolean isSimpleMode){
        this.isSimpleMode = isSimpleMode;
    }

    public void setHiddenControls(boolean isVisibleControls){
        this.isHiddenControls = isVisibleControls;
    }

    @Inject
    Provider<AbstractAttachmentView> attachmentViewFactory;
    @UiField
    HTMLPanel attachmentList;

    private boolean isSimpleMode;
    private boolean isHiddenControls;
    private Map<AbstractAttachmentView, Attachment> viewToAttachment;
    private static final String DOWNLOAD_PATH = "/Crm/springApi/files/";

    interface AttachmentListUiBinder extends UiBinder<HTMLPanel, AttachmentList> {}
    private static AttachmentListUiBinder ourUiBinder = GWT.create(AttachmentListUiBinder.class);
}