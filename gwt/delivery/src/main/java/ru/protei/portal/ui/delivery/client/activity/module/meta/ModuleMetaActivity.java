package ru.protei.portal.ui.delivery.client.activity.module.meta;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.CommentAndHistoryEvents;
import ru.protei.portal.ui.common.client.events.ModuleEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ModuleControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.module.meta.ModuleMetaView;

import java.util.Date;
import java.util.Objects;


public abstract class ModuleMetaActivity implements Activity, AbstractModuleMetaActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ModuleEvents.EditModuleMeta event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        module = event.module;

        fillView( event.module );
    }

    @Override
    public void onStateChange() {
        CaseState caseState = view.state().getValue();
        module.setState(caseState);
        module.setStateId(caseState.getId());
        onCaseMetaChanged(module);
    }

    @Override
    public void onHwManagerChange() {
        PersonShortView hwManager = view.hwManager().getValue();
        module.setHwManagerId(hwManager == null ? null : hwManager.getId());
        module.setHwManager(hwManager);
        onCaseMetaChanged(module);
    }

    @Override
    public void onQcManagerChange() {
        PersonShortView qcManager = view.qcManager().getValue();
        module.setQcManagerId(qcManager == null ? null : qcManager.getId());
        module.setQcManager(qcManager);
        onCaseMetaChanged(module);
    }

    @Override
    public void onDepartureDateChanged() {
        if (isDepartureDateEquals(view.departureDate().getValue(), module.getDepartureDate())) {
            view.setDepartureDateValid(isDepartureDateFieldValid());
            return;
        }

        if (!isDepartureDateFieldValid()) {
            view.setDepartureDateValid(false);
            return;
        }

        module.setDepartureDate(view.departureDate().getValue());
        view.setDepartureDateValid(true);
        onCaseMetaChanged(module);
    }

    @Override
    public String getValidationError() {
        CaseState state = view.state().getValue();
        if (state == null) {
            return lang.deliveryValidationEmptyState();
        }

        return null;
    }

    private void fillView(Module module) {
        view.state().setValue(module.getState());
        view.hwManager().setValue(module.getHwManager());
        view.qcManager().setValue(module.getQcManager());

        view.setCustomerCompany(module.getCustomerName());
        view.setManager(module.getManager().getDisplayName());

        view.buildDate().setValue(module.getBuildDate());
        view.setBuildDateValid(true);
        view.departureDate().setValue(module.getDepartureDate());
        view.setDepartureDateValid(true);
    }

    private void onCaseMetaChanged(Module module) {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }

        moduleService.updateMeta(module, new FluentCallback<Module>()
                .withSuccess(caseMetaUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ModuleEvents.ChangeModule(module.getId()));
                    fireEvent(new CommentAndHistoryEvents.Reload());
                    fillView( caseMetaUpdated );
                }));
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private boolean isDepartureDateEquals(Date departureDateField, Date departureDateMeta) {
        if (departureDateField == null) {
            return departureDateMeta == null;
        } else {
            return Objects.equals(departureDateField, departureDateMeta);
        }
    }

    public boolean isDepartureDateFieldValid() {
        Date departureDate = view.departureDate().getValue();
        if (departureDate == null) {
            return view.isDepartureDateEmpty();
        }

        return departureDate.getTime() > System.currentTimeMillis();
    }


    @Inject
    private Lang lang;
    @Inject
    private ModuleMetaView view;
    @Inject
    private ModuleControllerAsync moduleService;

    @ContextAware
    Module module;
}
