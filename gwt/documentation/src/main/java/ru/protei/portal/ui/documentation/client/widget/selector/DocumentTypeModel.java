package ru.protei.portal.ui.documentation.client.widget.selector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentationServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.LinkedList;
import java.util.List;

public abstract class DocumentTypeModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshOptions();
    }

    @Event
    public void onDocumentTypeListChanged(DocumentationEvents.ChangeDocumentTypeModel event) {
        refreshOptions();
    }

    public void subscribe(ModelSelector<DocumentType> documentTypeSelector) {
        subscribers.add(documentTypeSelector);
        documentTypeSelector.fillOptions(list);
    }

    private void notifySubscribers() {
        subscribers.forEach(selector -> {
            selector.fillOptions(list);
            selector.refreshValue();
        });
    }

    private void refreshOptions() {
        documentationService.getDocumentTypeList(new RequestCallback<List<DocumentType>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<DocumentType> options) {
                list.clear();
                list.addAll(options);
                notifySubscribers();
            }
        });
    }

    @Inject
    DocumentationServiceAsync documentationService;

    @Inject
    Lang lang;

    private List<DocumentType> list = new LinkedList<>();

    List<ModelSelector<DocumentType>> subscribers = new LinkedList<>();
}
