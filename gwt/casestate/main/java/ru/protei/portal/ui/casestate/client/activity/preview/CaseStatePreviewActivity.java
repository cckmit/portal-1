package ru.protei.portal.ui.casestate.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateUsageInCompaniesLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies.SELECTED;
import static ru.protei.portal.core.model.helper.StringUtils.defaultString;

/**
 * Активность превью
 */
public abstract class CaseStatePreviewActivity
        implements Activity,
        AbstractCaseStatePreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(CaseStateEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        fillView(event.caseState);
        if (SELECTED.equals(event.caseState.getUsageInCompanies())) {
            requestData(event.caseState.getId());
        }
    }

    private void requestData(Long id) {
        service.getCaseState(id, new RequestCallback<CaseState>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetItem(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(CaseState result) {
                fillView(result);
            }
        });

    }

    private void fillView(CaseState value) {
        view.setHeader(lang.previewCaseStatesHeader());
        view.setName(caseStateLang.getStateName(En_CaseState.getById(value.getId())));
        view.setDescription(defaultString(value.getInfo(), ""));
        view.setUsageInCompanies(caseStateUsageInCompaniesLang.getStateName(value.getUsageInCompanies()));
        view.setCompanies(value.getCompanies());
    }


    @Inject
    Lang lang;
    @Inject
    En_CaseStateLang caseStateLang;
    @Inject
    En_CaseStateUsageInCompaniesLang caseStateUsageInCompaniesLang;
    @Inject
    AbstractCaseStatePreviewView view;
    @Inject
    CaseStateControllerAsync service;

    private static final Logger log = Logger.getLogger(CaseStatePreviewActivity.class.getName());


    private AppEvents.InitDetails initDetails;
}
