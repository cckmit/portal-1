package ru.protei.portal.ui.official.client.activity.edit;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.OfficialEvents;
import ru.protei.portal.ui.common.client.service.OfficialServiceAsync;

/**
 * Created by serebryakov on 31/08/17.
 */
public abstract class OfficialEditActivity implements AbstractOfficialEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShowEdit(OfficialEvents.Edit event) {

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        if (event.id == null) {
            official = new Official();
            fillView();
            return;
        }
        requestOfficial(event.id);
    }

    private void requestOfficial(Long id) {
        officialService.getOfficial(id, new AsyncCallback<Official>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Official result) {
                official = result;
                fillView();
            }
        });
    }

    private void fillView() {
        view.region().setValue(official.getRegion() == null
                ? new EntityOption()
                : official.getRegion());
        view.product().setValue(official.getProduct() == null
                ? new ProductShortView()
                : official.getProduct().toProductShortView());
        view.info().setValue(official.getInfo());
    }

    @Override
    public void onSaveClicked() {
        applyChangesForOfficial();
        officialService.saveOfficial(official, new AsyncCallback<Official>() {
            @Override
            public void onFailure(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(throwable.getMessage(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Official official) {
                fireEvent( new Back() );
            }
        });

    }

    @Override
    public void onCancelClicked() {
        fireEvent( new Back() );
    }

    private void applyChangesForOfficial() {
        official.setProduct(DevUnit.fromProductShortView(view.product().getValue()));
        official.setRegion(view.region().getValue());
        official.setInfo(view.info().getValue());
    }

    private Official official;

    @Inject
    OfficialServiceAsync officialService;

    @Inject
    private AbstractOfficialEditView view;

    private AppEvents.InitDetails initDetails;
}
