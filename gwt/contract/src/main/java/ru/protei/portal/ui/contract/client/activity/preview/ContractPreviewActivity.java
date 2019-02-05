package ru.protei.portal.ui.contract.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.events.CaseCommentEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.*;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

public abstract class ContractPreviewActivity implements AbstractContractPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( ContractEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        loadDetails(event.id);
    }


    private void loadDetails(Long id) {
        contractController.getContract(id, new RequestCallback<Contract>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetItem(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Contract result) {
                if (result == null) {
                    onError(null);
                    return;
                }
                fillView(result);
            }
        });
    }

    private void fillView( Contract value ) {
        fireEvent( new CaseCommentEvents.Show( view.getCommentsContainer(), En_CaseType.CONTRACT, value.getId()) );
    }

    @Inject
    private Lang lang;

    @Inject
    private AbstractContractPreviewView view;
    @Inject
    private ContractControllerAsync contractController;
}
