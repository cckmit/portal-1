package ru.protei.portal.ui.documentation.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class DocumentationEditActivity
        implements Activity, AbstractDocumentationEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(DocumentationEvents.Edit event) {

    }

    @Override
    public void onSaveClicked() {

    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Inject
    AbstractDocumentationEditView view;

    @Inject
    Lang lang;

    Documentation documentation;

    private AppEvents.InitDetails initDetails;
}
