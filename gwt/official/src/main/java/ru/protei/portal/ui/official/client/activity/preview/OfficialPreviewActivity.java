package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.OfficialMemberEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.OfficialServiceAsync;

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
    public void onReloadPreview(OfficialMemberEvents.ReloadPreview event) {
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
    }

    private void fillView(Official official) {
        view.setCreationDate(official.getCreated() == null ? "" : DateFormatter.formatDateTime( official.getCreated() ));
        view.setProduct(official.getProduct().getName());
        view.setRegion(official.getRegion().getDisplayText());
        view.setInfo(official.getInfo());

        officialService.getOfficialMembersByProducts(official.getId(), new AsyncCallback<Map<String, List<OfficialMember>>>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(Map<String, List<OfficialMember>> members) {
                fillMembers(members);
            }
        });
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
                itemView.setAmplua(member.getAmplua());
                itemView.setPosition(member.getPosition());
                itemView.setRelations(member.getRelations());
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


    private AppEvents.InitDetails initDetails;

    private Map<AbstractOfficialItemView, OfficialMember> itemViewToModel
            = new HashMap<AbstractOfficialItemView, OfficialMember>();

    @Override
    public void onEditClicked(AbstractOfficialItemView itemView) {
        fireEvent(new OfficialMemberEvents.Edit(itemViewToModel.get(itemView).getId()));
    }

    @Inject
    Provider<AbstractOfficialListView> listProvider;

    @Inject
    Provider<AbstractOfficialItemView> itemProvider;

    @Inject
    OfficialServiceAsync officialService;

    @Inject
    Lang lang;


    @Inject
    private AbstractOfficialPreviewView view;

    private Long officialId;
}
