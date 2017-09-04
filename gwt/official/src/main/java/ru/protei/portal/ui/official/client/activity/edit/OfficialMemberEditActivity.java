package ru.protei.portal.ui.official.client.activity.edit;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.OfficialMemberEvents;
import ru.protei.portal.ui.common.client.service.OfficialServiceAsync;

/**
 * Активность на форме редактирования должностного лица
 */
public abstract class OfficialMemberEditActivity implements AbstractOfficialMemberEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShowEdit(OfficialMemberEvents.Edit event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        this.event = event;

        if(event.id == null) {
            officialMember = new OfficialMember();
            fillView();
            return;
        }

        requestData(event.id);
    }

    private void fillView() {
        view.firstName().setValue(officialMember.getFirstName());
        view.lastName().setValue(officialMember.getLastName());
        view.secondName().setValue(officialMember.getSecondName());
        view.organization().setValue(officialMember.getCompany());
        view.position().setValue(officialMember.getPosition());
        view.amplua().setValue(officialMember.getAmplua());
        view.relations().setValue(officialMember.getRelations());
    }

    private void requestData(Long id) {
        officialService.getOfficialMember(id, new AsyncCallback<OfficialMember>() {

            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(OfficialMember value) {
                officialMember = value;
                fillView();
            }
        });
    }

    @Override
    public void onSaveClicked() {
        applyChangesOfficialMember();

        if (officialMember.getId() != null) {
            officialService.saveOfficialMember(officialMember, new AsyncCallback<OfficialMember>() {
                @Override
                public void onFailure(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(OfficialMember officialMember) {
                    fireEvent(new OfficialMemberEvents.ReloadPage());
                    fireEvent(new Back());
                }
            });
        } else {
            officialService.createOfficialMember(officialMember, event.parentId, new AsyncCallback<Long>() {
                @Override
                public void onFailure(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(Long result) {
                    fireEvent(new OfficialMemberEvents.ReloadPage());
                    fireEvent(new Back());
                }
            });
        }
    }

    private void applyChangesOfficialMember() {
        officialMember.setFirstName(view.firstName().getValue());
        officialMember.setLastName(view.lastName().getValue());
        officialMember.setSecondName(view.secondName().getValue());
        officialMember.setCompany(view.organization().getValue());
        officialMember.setPosition(view.position().getValue());
        officialMember.setAmplua(view.amplua().getValue());
        officialMember.setRelations(view.relations().getValue());
//        officialMember.setComments(view.comments().getValue());
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private OfficialMemberEvents.Edit event;

    private OfficialMember officialMember;


    @Inject
    OfficialServiceAsync officialService;

    @Inject
    private AbstractOfficialMemberEditView view;

    private AppEvents.InitDetails initDetails;
}
