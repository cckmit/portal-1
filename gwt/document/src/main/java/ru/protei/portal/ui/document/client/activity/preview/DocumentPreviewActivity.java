package ru.protei.portal.ui.document.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class DocumentPreviewActivity implements Activity, AbstractDocumentPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(DocumentEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        if (event.document == null) {
            invalidDocument();
            return;
        }
        Long documentId = event.document.getId();
        if (documentId == null) {
            invalidDocument();
            return;
        }
        fillView(event.document);
    }

    private void invalidDocument() {
        fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
    }

    private void fillView(Document document) {
        view.setHeader(lang.documentDescription() + " #" + document.getId());
        view.setName(document.getName());
        view.setCreatedDate(document.getCreated() == null ? "" : DateFormatter.formatDateTime(document.getCreated()));
        view.setType(document.getType().getName());
        view.setAnnotation(document.getAnnotation());
        view.setProject(document.getProject() == null ? "" : document.getProject());
        view.setManager(document.getManagerShortName() == null ? "" : document.getManagerShortName());
        view.setNumberDecimal(DecimalNumberFormatter.formatNumber(document.getDecimalNumber()));
        view.setNumberInventory(document.getInventoryNumber() == null ? "" : document.getInventoryNumber().toString());
        view.setKeyWords(document.getKeywords() == null ? "" : HelperFunc.join(", ", document.getKeywords()));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentPreviewView view;
}