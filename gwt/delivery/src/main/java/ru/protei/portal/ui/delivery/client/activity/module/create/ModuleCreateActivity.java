package ru.protei.portal.ui.delivery.client.activity.module.create;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.ModuleEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ModuleControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Objects;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public abstract class ModuleCreateActivity implements Activity, AbstractModuleCreateActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ModuleEvents.Create event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(event.parent));
            return;
        }
        event.parent.clear();
        event.parent.add(view.asWidget());
        this.kitId = event.kitId;

        prepare();
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }
        Module module = fillDto();
        save(module);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onUpdateSerialNumberClicked() {
        updateSerialNumber();
    }

    private void updateSerialNumber() {
        moduleService.generateSerialNumber(kitId, new FluentCallback<String>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(serialNumber -> view.setSerialNumber(serialNumber))
        );
    }

    private void prepare() {
        updateSerialNumber();

    }

    private Module fillDto() {
        Module module = new Module();

        return module;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        if (isBlank(view.name().getValue())) {
            return lang.deliveryValidationEmptyName();
        }

//        String error = view.getValidationError();
//        if (error != null) {
//            return error;
//        }
        CaseState state = view.state().getValue();
        if (!Objects.equals(CrmConstants.State.PRELIMINARY, state.getId())) {
            return lang.deliveryValidationInvalidStateAtCreate();
        }


        return null;
    }

    private void save(Module module) {
        save(module, throwable -> {}, () -> {
            fireEvent(new Back());
        });
    }

    private void save(Module module, Consumer<Throwable> onFailure, Runnable onSuccess) {
        view.saveEnabled().setEnabled(false);
        moduleService.saveModule(module, new FluentCallback<Module>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    defaultErrorHandler.accept(throwable);
                    onFailure.accept(throwable);
                })
                .withSuccess(id -> {
                    view.saveEnabled().setEnabled(true);
                    onSuccess.run();
                }));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractModuleCreateView view;
    @Inject
    private ModuleControllerAsync moduleService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    private PolicyService policyService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private Long kitId;
}

