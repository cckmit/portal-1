package ru.protei.portal.ui.common.client.widget.attachment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.AttachmentType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentList;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentView;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.view.attachment.AttachmentView;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;
import ru.protei.portal.ui.common.client.widget.attachment.preview.AttachmentPreview;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.util.TransliterationUtils.transliterate;
import static ru.protei.portal.ui.common.client.util.LocaleUtils.isLocaleEn;

/**
 * Created by bondarenko on 17.01.17.
 */
public class AttachmentList extends Composite implements HasAttachments, HasAttachmentListHandlers, AbstractAttachmentList {
    public AttachmentList() {
        initWidget(ourUiBinder.createAndBindUi(this));
        viewToAttachment = new HashMap<>();
    }

    @Override
    public void add(Attachment attachment) {
        if (attachment == null) {
            return;
        }

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
    public void add(Collection<Attachment> attachments) {
        if (CollectionUtils.isEmpty(attachments)) {
            return;
        }

        attachments.forEach(this::createView);
        Set<Long> attachCreatorIds = attachments.stream().map(Attachment::getCreatorId).collect(Collectors.toSet());

        personService.getPersonNames(attachCreatorIds, new RequestCallback<Map<Long, String>>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Map<Long, String> names) {
                viewToAttachment.forEach((view, attachment) -> {
                    if (attachments.contains(attachment)) {
                        String name = names.get(attachment.getCreatorId());
                        view.setCreationInfo(isLocaleEn() ? transliterate(name) : name, attachment.getCreated());
                    }
                });
            }
        });
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
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        return addHandler( handler, RemoveEvent.getType() );
    }

    @Override
    public void onAttachmentRemove(AbstractAttachmentView attachment) {
        if (activity == null) {
            return;
        }

        activity.fireEvent(new ConfirmDialogEvents.Show(lang.attachmentRemoveConfirmMessage(), () -> RemoveEvent.fire(this, viewToAttachment.get(attachment))));
    }

    @Override
    public void onShowPreview(Image attachment) {
        attachmentPreview.show(attachment);
    }

    @Override
    public void setActivity(Activity activity) {
        this.activity = activity;
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

    private AbstractAttachmentView createView(Attachment attachment) {
        AbstractAttachmentView view = attachmentViewFactory.get();
        view.setActivity(this);
        view.setFileName(attachment.getFileName());
        view.setFileSize(attachment.getDataSize());
        view.setDownloadUrl(DOWNLOAD_PATH + attachment.getExtLink());
        view.removeButtonVisibility().setVisible(!isHiddenControls);

        if (isSimpleMode) {
            view.asWidget().addStyleName("attach-minimize");
        } else {
            AttachmentType.AttachmentCategory category = AttachmentType.getCategory(attachment.getMimeType());
            if (category == AttachmentType.AttachmentCategory.IMAGE) {
                view.setPicture(DOWNLOAD_PATH + attachment.getExtLink());
                view.asWidget().addStyleName("attach-image");
            } else {
                view.setPicture(category.picture);
                view.asWidget().addStyleName("attach-file");
            }
        }

        viewToAttachment.put(view, attachment);
        attachmentList.add(view.asWidget());

        return view;
    }

//    public void setCreationInfo(AbstractAttachmentView view, Attachment attachment, Map<Long, String> names) {
//        if (LocaleUtils.isLocaleEn()) {
//            view.setCreationInfo(TransliterationUtils.transliterate(names.get(attachment.getCreatorId())), attachment.getCreated());
//            return;
//        }
//        view.setCreationInfo(names.get(attachment.getCreatorId()), attachment.getCreated());
//    }

    @Inject
    Provider<AttachmentView> attachmentViewFactory;
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
    private Activity activity;
    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/files/";

    interface AttachmentListUiBinder extends UiBinder<HTMLPanel, AttachmentList> {}
    private static AttachmentListUiBinder ourUiBinder = GWT.create(AttachmentListUiBinder.class);
}
