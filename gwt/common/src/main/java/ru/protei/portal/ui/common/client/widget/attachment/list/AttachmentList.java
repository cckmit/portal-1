package ru.protei.portal.ui.common.client.widget.attachment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentActivity;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentView;
import ru.protei.portal.ui.common.client.activity.attachment.AttachmentType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;
import ru.protei.portal.ui.common.client.widget.attachment.preview.AttachmentPreview;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.stream.Collectors;

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

        AbstractAttachmentView view = createView(attachment);
        personService.getPersonNames(Collections.singletonList(attachment.getCreatorId()), new RequestCallback<Map<Long, String>>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Map<Long, String> names) {
                view.setCreationInfo(names.get(attachment.getCreatorId()), attachment.getCreated());
            }
        });
    }


    @Override
    public void add(Collection<Attachment> attachments){
        if(attachments == null || attachments.isEmpty())
            return;

        if(attachments.size() == 1) {
            add((Attachment) attachments.toArray()[0]);
        }else {
            attachments.forEach(this::createView);
            Set<Long> attachIds = attachments.stream().map(Attachment::getCreatorId).collect(Collectors.toSet());

            personService.getPersonNames(attachIds, new RequestCallback<Map<Long, String>>() {
                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onSuccess(Map<Long, String> names) {
                    viewToAttachment.forEach((view, attachment) -> {
                        if (attachments.contains(attachment)) {
                            view.setCreationInfo(names.get(attachment.getCreatorId()), attachment.getCreated());
                        }
                    });
                }
            });
        }
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
    public boolean isEmpty() {
        return viewToAttachment.isEmpty();
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
        if(Window.confirm(lang.attachmentRemoveConfirmMessage())) {
            RemoveEvent.fire(this, viewToAttachment.get(attachment));
        }
    }

    @Override
    public void onShowPreview(Image attachment) {
        attachmentPreview.show(attachment);
    }

    public void setSimpleMode(boolean isSimpleMode){
        this.isSimpleMode = isSimpleMode;
    }

    public void setHiddenControls(boolean hideControls){
        this.isHiddenControls = hideControls;
    }

    public void setEnsureDebugId(String debugId) {
        attachmentList.ensureDebugId(debugId);
    }

    private AbstractAttachmentView createView(Attachment attachment){
        AbstractAttachmentView view = attachmentViewFactory.get();
        view.setActivity(this);
        view.setFileName(attachment.getFileName());
        view.setFileSize(attachment.getDataSize());
        view.setDownloadUrl(DOWNLOAD_PATH + attachment.getExtLink());

        if(!isSimpleMode) {
            AttachmentType.AttachmentCategory category = AttachmentType.getCategory(attachment.getMimeType());
            if (category == AttachmentType.AttachmentCategory.IMAGE) {
                view.setPicture(DOWNLOAD_PATH + attachment.getExtLink());
            }else {
                view.setPicture(category.picture);
                view.asWidget().addStyleName("attach-preview-disabled");
            }
        }else
            view.asWidget().addStyleName("attach-minimize");

        if(isHiddenControls){
            view.asWidget().addStyleName("attach-hide-remove-btn");
        }

        viewToAttachment.put(view, attachment);
        add(view.asWidget());

        return view;
    }

    @Inject
    Provider<AbstractAttachmentView> attachmentViewFactory;
    @UiField
    HTMLPanel attachmentList;
    @Inject
    Lang lang;
    @Inject
    AttachmentPreview attachmentPreview;
    @Inject
    PersonControllerAsync personService;


    private boolean isSimpleMode;
    private boolean isHiddenControls;
    private Map<AbstractAttachmentView, Attachment> viewToAttachment;
    private static final String DOWNLOAD_PATH = "Crm/springApi/files/";

    interface AttachmentListUiBinder extends UiBinder<HTMLPanel, AttachmentList> {}
    private static AttachmentListUiBinder ourUiBinder = GWT.create(AttachmentListUiBinder.class);
}