package ru.protei.portal.ui.official.client.activity.preview;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.OfficialEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.OfficialServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

/**
 * Created by serebryakov on 23/08/17.
 */
public abstract class OfficialPreviewActivity implements AbstractOfficialPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(OfficialEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        this.officialId = event.id;
        fillView(officialId);
        view.showFullScreen(false);
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
        view.setProduct(official.getProductName());
        view.setRegion(official.getRegion().getDisplayText());
        view.setInfo(official.getInfo());
    }

    @Inject
    OfficialServiceAsync officialService;

    @Override
    public void onFullScreenClicked() {

    }

    @Inject
    Lang lang;

    @Inject
    private AbstractOfficialPreviewView view;

    private Long officialId;


}
