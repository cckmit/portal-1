package ru.protei.portal.ui.casestate.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies.SELECTED;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
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
        if (!policyService.hasPrivilegeFor(En_Privilege.CASE_STATES_VIEW)) {
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        caseState = event.caseState;
        fillView(event.caseState);
        if (SELECTED.equals(event.caseState.getUsageInCompanies())) {
            requestData(event.caseState.getId());
        }
    }

    @Override
    public void onUsageInCompaniesChange() {
        En_CaseStateUsageInCompanies usage = view.usageInCompanies().getValue();
        view.companiesVisibility().setVisible(SELECTED.equals(usage));
    }

    @Override
    public void onSaveClicked() {
        if (!policyService.hasPrivilegeFor(En_Privilege.CASE_STATES_EDIT)) {
            return;
        }

        CaseState state = fillData(new CaseState(caseState.getId()));

        service.saveCaseState(state, new ShortRequestCallback<CaseState>().setErrorMessage(lang.errNotUpdated())
                .setOnSuccess(result -> {
                    caseState = result;
                    fillView(result);
                    fireEvent(new CaseStateEvents.UpdateItem(caseState));
                }));
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new CaseStateEvents.ClosePreview());
    }

    private void requestData(Long id) {
        service.getCaseState(id, new ShortRequestCallback<CaseState>().setErrorMessage(lang.errGetItem())
                .setOnSuccess(result -> {
                    caseState = result;
                    fillView(result);
                }));
    }

    private void fillView(CaseState state) {
        view.setName(state.getState());
        view.description().setValue(defaultString(state.getInfo(), ""));
        view.usageInCompanies().setValue(state.getUsageInCompanies());
        view.companiesVisibility().setVisible(SELECTED.equals(state.getUsageInCompanies()));
        view.companies().setValue(makeOptionsFromCompanies(state.getCompanies()));

        view.setViewEditable(policyService.hasPrivilegeFor(En_Privilege.CASE_STATES_EDIT));
    }

    private CaseState fillData(CaseState state) {
        state.setState(caseState.getState());
        state.setInfo(view.description().getValue());
        state.setUsageInCompanies(view.usageInCompanies().getValue());
        state.setColor(caseState.getColor());

        if (SELECTED.equals(state.getUsageInCompanies())) {
            List<Company> companies = makeCompaniesFromOptions(view.companies().getValue());
            state.setCompanies(companies);
        }

        return state;
    }

    private static List<Company> makeCompaniesFromOptions(Set<EntityOption> options) {
        if (isEmpty(options)) return null;
        return options.stream().map(o -> Company.fromEntityOption(o)).collect(Collectors.toList());
    }

    private static Set<EntityOption> makeOptionsFromCompanies(List<Company> companies) {
        if (isEmpty(companies)) return null;
        return companies.stream().map(c -> c.toEntityOption()).collect(Collectors.toSet());
    }


    @Inject
    Lang lang;
    @Inject
    AbstractCaseStatePreviewView view;
    @Inject
    CaseStateControllerAsync service;
    @Inject
    PolicyService policyService;

    @ContextAware
    CaseState caseState;

    private AppEvents.InitDetails initDetails;
}
