package ru.protei.portal.ui.company.client.widget.buttonselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CaseServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Created by bondarenko on 10.11.16.
 */
public class IssueStatesButtonSelector extends ButtonSelector<En_CaseState> implements ModelSelector<En_CaseState> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        requestOptions();
    }

    private void requestOptions() {
        commonService.getStatesByCaseType(
                En_CaseType.CRM_SUPPORT,
                new RequestCallback<List<En_CaseState>>() {
                    @Override
                    public void onError(Throwable throwable) {}

                    @Override
                    public void onSuccess(List<En_CaseState> caseStates) {
                        fillOptions(caseStates);
                    }
                }
        );
    }

    @Override
    public void fillOptions(List<En_CaseState> options){
        clearOptions();
        options.forEach(option -> addOption(option.getName(), option));
    }

    @Inject
    CaseServiceAsync commonService;
}
