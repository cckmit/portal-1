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
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.OfficialControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.official.client.OfficialUtils;
import ru.protei.portal.ui.official.client.activity.table.OfficialTableActivity;

import java.util.*;

/**
 * Активность карточки должностных лиц
 */
public abstract class OfficialPreviewActivity implements AbstractOfficialPreviewActivity, AbstractOfficialItemActivity,  Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) { addAttachments(Collections.singleton(attachment));
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

//        attachmentService.getAttachmentsByCaseId(officialId, new RequestCallback<List<Attachment>>() {
//            @Override
//            public void onError(Throwable throwable) {
//                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
//            }
//
//            @Override
//            public void onSuccess(List<Attachment> result) {
//                view.attachmentsContainer().clear();
//                result.forEach(attachmentCollection::addAttachment);
//            }
//        });
    }

    private void fillView(Official official) {
        view.setCreationDate(official.getCreated() == null ? "" : DateFormatter.formatDateTime( official.getCreated() ));
        view.setProduct(official.getProduct().getName());
        view.setRegion(official.getRegion().getDisplayText());
        view.setInfo(official.getInfo());

        fillMembers(OfficialUtils.createMembersByRegionsMap(official));
        fireEvent( new OfficialMemberEvents.ShowComments( view.getCommentsContainer(), official.getId()) );

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
                view.attachmentsContainer().remove(attachment);
                if(view.attachmentsContainer().isEmpty())
                    fireEvent(new IssueEvents.ChangeIssue(officialId));
                fireEvent( new OfficialMemberEvents.ShowComments( view.getCommentsContainer(), officialId ) );
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

    private void addAttachments(Collection<Attachment> attachs){
        if(view.attachmentsContainer().isEmpty())
            fireEvent(new IssueEvents.ChangeIssue(officialId));

        view.attachmentsContainer().add(attachs);
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
    OfficialControllerAsync officialService;

    @Inject
    OfficialTableActivity officialTableActivity;

    @Inject
    Lang lang;

    @Inject
    En_PersonRoleTypeLang roleTypeLang;

    private AppEvents.InitDetails initDetails;

    @Inject
    private AbstractOfficialPreviewView view;

    private Long officialId;

    private Map<AbstractOfficialItemView, OfficialMember> itemViewToModel
            = new HashMap<AbstractOfficialItemView, OfficialMember>();
}
