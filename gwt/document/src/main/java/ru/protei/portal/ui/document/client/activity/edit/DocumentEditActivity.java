package ru.protei.portal.ui.document.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class DocumentEditActivity implements Activity, AbstractDocumentEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(DocumentEvents.Edit event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        requestDocument(event.id, document -> {
            fireEvent(new DocumentEvents.Form.Show(view.documentContainer(), document, TAG));
        });
    }

    @Override
    public void onCloseClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onSaveClicked() {
        fireEvent(new DocumentEvents.Form.Save(TAG));
    }

    @Event
    public void onSaved(DocumentEvents.Form.Saved event) {
        if (!Objects.equals(TAG, event.tag)) {
            return;
        }
        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
        fireEvent(new DocumentEvents.ChangeModel());
        fireEvent(new Back());
    }

    private void requestDocument(Long documentId, Consumer<Document> onSuccess) {
        documentService.getDocument(documentId, new FluentCallback<Document>()
            .withErrorMessage(lang.errGetObject())
            .withSuccess(onSuccess));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentEditView view;
    @Inject
    DocumentControllerAsync documentService;

    private AppEvents.InitDetails initDetails;
    private final String TAG = "DocumentEditActivity";
}
