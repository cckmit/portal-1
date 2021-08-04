package ru.protei.portal.ui.delivery.client.activity.module.meta;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.ui.common.client.events.ModuleEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.view.module.meta.ModuleMetaView;


public abstract class ModuleMetaActivity implements Activity, AbstractModuleMetaActivity {

    @PostConstruct
    public void onInit() {
        moduleMetaView.setActivity(this);
    }

    @Event
    public void onShow(ModuleEvents.EditModuleMeta event) {
        event.parent.clear();
        event.parent.add(moduleMetaView.asWidget());

        module = event.module;

        fillView( event.module );
    }

    private void fillView(Module module) {
        moduleMetaView.state().setValue(module.getState());
        moduleMetaView.hwManager().setValue(module.getHwManager());
        moduleMetaView.qcManager().setValue(module.getQcManager());

        moduleMetaView.setCustomerCompany(module.getCustomerName());
        moduleMetaView.setManager(module.getManagerName());

        moduleMetaView.buildDate().setValue(module.getBuildDate());
        moduleMetaView.setBuildDateValid(true);
        moduleMetaView.departureDate().setValue(module.getDepartureDate());
        moduleMetaView.setDepartureDateValid(true);
    }

    @Inject
    private Lang lang;
    @Inject
    private ModuleMetaView moduleMetaView;

    @ContextAware
    Module module;
}
