package ru.protei.portal.ui.casestate.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.logging.Logger;

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
    }

    private void fillView(CaseState value) {
        view.setHeader(lang.previewCaseStatesHeader());
        String stateName = caseStateLang.getStateName(En_CaseState.getById(value.getId()));
        view.setName(stateName);
        view.setDescription(defaultString(value.getInfo(), ""));
    }


    @Inject
    Lang lang;
    @Inject
    En_CaseStateLang caseStateLang;
    @Inject
    AbstractCaseStatePreviewView view;

    private static final Logger log = Logger.getLogger(CaseStatePreviewActivity.class.getName());


    private AppEvents.InitDetails initDetails;
}
