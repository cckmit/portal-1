package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.AttachmentCollection;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.OfficialServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.official.client.OfficialUtils;
import ru.protei.portal.ui.official.client.activity.table.OfficialTableActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Активность карточки должностных лиц
 */
public abstract class OfficialPreviewActivity implements AbstractOfficialPreviewActivity, AbstractOfficialItemActivity,  Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setFileUploadHandler(new FileUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                attachmentCollection.addAttachment(attachment);
            }
            @Override
            public void onError() {
                fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });
    }

    @Event
    public void onShow(OfficialMemberEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        this.officialId = event.id;
        fillView(officialId);
        view.showFullScreen(false);
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onReloadPreview(OfficialMemberEvents.ReloadPage event) {
        fillView(officialId);
    }

    private void fillView(Long officialId) {
        if (officialId == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        officialService.getOfficial( officialId, new AsyncCallback<Official>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(Official official) {
                fillView( official );
            }
        } );

        attachmentService.getAttachmentsByCaseId(officialId, new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<Attachment> result) {
                view.attachmentsContainer().clear();
                result.forEach(attachmentCollection::addAttachment);
            }
        });
    }

    private void fillView(Official official) {
        view.setCreationDate(official.getCreated() == null ? "" : DateFormatter.formatDateTime( official.getCreated() ));
        view.setProduct(official.getProduct().getName());
        view.setRegion(official.getRegion().getDisplayText());
        view.setInfo(official.getInfo());

        fillMembers(OfficialUtils.createMembersByRegionsMap(official));
        fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), official.getId(), attachmentCollection ) );

    }

    private void fillMembers(Map<String, List<OfficialMember>> members) {
        view.clearMembers();
        for (Map.Entry<String, List<OfficialMember>> entry: members.entrySet()) {
            AbstractOfficialListView listView = listProvider.get();
            listView.setCompanyName(entry.getKey());
            for (OfficialMember member: entry.getValue()) {
                AbstractOfficialItemView itemView = itemProvider.get();
                itemView.setActivity(this);
                itemView.setName(member.getLastName() + " " +
                member.getFirstName() + " " + member.getSecondName());
                itemView.setAmplua(roleTypeLang.getName(member.getAmplua()));
                itemView.setPosition(member.getPosition());
                itemView.setRelations(lang.officialInRelationsWith() +  member.getRelations());
                itemView.setComments(member.getComments());
                itemView.setButtonsVisibility(policyService.hasPrivilegeFor(En_Privilege.OFFICIAL_EDIT));
                itemViewToModel.put(itemView, member);
                listView.getItemContainer().add(itemView.asWidget());
            }
            view.getMembersContainer().add(listView.asWidget());
        }
    }

    @Override
    public void onFullScreenClicked() {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.showFullScreen(true);
    }

    @Override
    public void onAddCLicked() {
        fireEvent(new OfficialMemberEvents.Edit(null, officialId));
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(attachment.getId(), new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if(!result){
                    onError(null);
                    return;
                }
                attachmentCollection.removeAttachment(attachment);
                fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), officialId, attachmentCollection ) );
            }
        });
    }


    @Override
    public void onEditClicked(AbstractOfficialItemView itemView) {
        fireEvent(new OfficialMemberEvents.Edit(itemViewToModel.get(itemView).getId(), null));
    }

    @Override
    public void onRemoveClicked(AbstractOfficialItemView itemView) {
        officialService.removeOfficialMember(itemViewToModel.get(itemView).getId(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errOfficialMemberRemove(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if (!result) {
                    fireEvent(new NotifyEvents.Show(lang.errOfficialMemberRemove(), NotifyEvents.NotifyType.ERROR));
                    return;
                }
                fireEvent(new OfficialMemberEvents.ReloadPage());
             }
        });
    }

    @Inject
    AttachmentServiceAsync attachmentService;

    @Inject
    PolicyService policyService;

    @Inject
    Provider<AbstractOfficialListView> listProvider;

    @Inject
    Provider<AbstractOfficialItemView> itemProvider;

    @Inject
    OfficialServiceAsync officialService;

    @Inject
    OfficialTableActivity officialTableActivity;

    @Inject
    Lang lang;

    @Inject
    En_PersonRoleTypeLang roleTypeLang;

    private AttachmentCollection attachmentCollection = new AttachmentCollection() {
        @Override
        public void addAttachment(Attachment attachment) {
            if(isEmpty())
                officialTableActivity.updateRow(officialId);

            put(attachment.getId(), attachment);
            view.attachmentsContainer().add(attachment);
        }

        @Override
        public void removeAttachment(Attachment attachment) {
            remove(attachment.getId());
            view.attachmentsContainer().remove(attachment);

            if(isEmpty())
                officialTableActivity.updateRow(officialId);
        }
    };

    private AppEvents.InitDetails initDetails;

    @Inject
    private AbstractOfficialPreviewView view;

    private Long officialId;

    private Map<AbstractOfficialItemView, OfficialMember> itemViewToModel
            = new HashMap<AbstractOfficialItemView, OfficialMember>();
}
