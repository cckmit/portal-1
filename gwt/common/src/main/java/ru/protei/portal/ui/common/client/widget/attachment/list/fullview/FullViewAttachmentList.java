package ru.protei.portal.ui.common.client.widget.attachment.list.fullview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.AttachmentType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentActivity;
import ru.protei.portal.ui.common.client.activity.attachment.AbstractAttachmentView;
import ru.protei.portal.ui.common.client.activity.attachment.fullview.AbstractAttachmentFullView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PersonControllerAsync;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.common.client.view.attachment.fullview.document.DocumentAttachmentView;
import ru.protei.portal.ui.common.client.view.attachment.fullview.image.ImageAttachmentView;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.HasAttachmentListHandlers;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveHandler;
import ru.protei.portal.ui.common.client.widget.attachment.preview.AttachmentPreview;
import ru.protei.portal.ui.common.client.widget.selector.event.HasRemoveHandlers;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class FullViewAttachmentList extends Composite implements HasAttachments, HasAttachmentListHandlers, AbstractAttachmentActivity {
    public FullViewAttachmentList() {
        initWidget(ourUiBinder.createAndBindUi(this));
        viewToAttachment = new HashMap<>();
    }

    @Override
    public void add(Attachment attachment) {
        if (attachment == null) {
            return;
        }

        AbstractAttachmentView view = createView(attachment);

        personService.getPersonsByIds(Collections.singletonList(attachment.getCreatorId()), new FluentCallback<List<Person>>()
                .withSuccess(persons -> fillPersonDependentFields(view, attachment, persons.iterator().next()))
        );
    }


    @Override
    public void add(Collection<Attachment> attachments) {
        if (CollectionUtils.isEmpty(attachments)) {
            return;
        }

        attachments.forEach(this::createView);

        Set<Long> attachmentCreatorIds = attachments.stream().map(Attachment::getCreatorId).collect(Collectors.toSet());

        personService.getPersonsByIds(attachmentCreatorIds, new FluentCallback<List<Person>>()
                .withSuccess(persons -> fillPersonDependentFields(attachments, persons))
        );
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
        documentsContainer.clear();
        imagesContainer.clear();
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
        if (Window.confirm(lang.attachmentRemoveConfirmMessage())) {
            RemoveEvent.fire(this, viewToAttachment.get(attachment));
        }
    }

    @Override
    public void onShowPreview(Image attachment) {
        attachmentPreview.show(attachment);
    }

    public void setHiddenControls(boolean hideControls){
        this.isHiddenControls = hideControls;
    }

    public void setEnsureDebugId(String debugId) {
        attachmentList.ensureDebugId(debugId);
    }

    private AbstractAttachmentView createView(Attachment attachment) {
        AttachmentType.AttachmentCategory category = AttachmentType.getCategory(attachment.getMimeType());
        boolean isImage = category == AttachmentType.AttachmentCategory.IMAGE;
        String attachmentUrl = DOWNLOAD_PATH + attachment.getExtLink();

        AbstractAttachmentView view = isImage ? imageAttachmentViewFactory.get() : documentAttachmentViewFactory.get();

        view.setActivity(this);
        view.setFileName(attachment.getFileName());
        view.setFileSize(attachment.getDataSize());

        view.setDownloadUrl(attachmentUrl);
        view.setPicture(attachmentUrl);

        view.removeButtonVisibility().setVisible(!isHiddenControls);

        viewToAttachment.put(view, attachment);

        if (isImage) {
            imagesContainer.add(view.asWidget());
        } else {
            documentsContainer.add(view.asWidget());
        }

        return view;
    }

    private void fillPersonDependentFields(Collection<Attachment> attachments, List<Person> persons) {
        viewToAttachment.forEach((view, attachment) -> {
            if (attachments.contains(attachment)) {
                stream(persons).filter(person -> person.getId().equals(attachment.getCreatorId())).findFirst().ifPresent(person -> {
                    fillPersonDependentFields(view, attachment, person);
                });
            }
        });
    }

    private void fillPersonDependentFields(AbstractAttachmentView view, Attachment attachment, Person person) {
        view.setCreationInfo(person.getDisplayShortName(), attachment.getCreated());
        ((AbstractAttachmentFullView) view).setAuthorAvatarUrl(AvatarUtils.getAvatarUrl(person));
    }

    @Inject
    Provider<DocumentAttachmentView> documentAttachmentViewFactory;
    @Inject
    Provider<ImageAttachmentView> imageAttachmentViewFactory;
    @UiField
    HTMLPanel attachmentList;
    @UiField
    HTMLPanel documentsContainer;
    @UiField
    HTMLPanel imagesContainer;
    @Inject
    Lang lang;
    @Inject
    AttachmentPreview attachmentPreview;
    @Inject
    PersonControllerAsync personService;


    private boolean isHiddenControls;
    private Map<AbstractAttachmentView, Attachment> viewToAttachment;
    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/files/";

    interface AttachmentListUiBinder extends UiBinder<HTMLPanel, FullViewAttachmentList> {}
    private static AttachmentListUiBinder ourUiBinder = GWT.create(AttachmentListUiBinder.class);
}
