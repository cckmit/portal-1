package ru.protei.portal.ui.documentation.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.events.DocumentationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.stream.Collectors;

public abstract class DocumentationPreviewActivity implements Activity, AbstractDocumentationPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(DocumentationEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        if (event.documentation == null) {
            invalidDocument();
            return;
        }
        Long documentationId = event.documentation.getId();
        if (documentationId == null) {
            invalidDocument();
            return;
        }
        fillView(event.documentation);
    }

    private void invalidDocument() {
        fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
    }

    private void fillView(Documentation documentation) {
        view.setHeader(lang.documentationDescription() + " #" + documentation.getId());
        view.setName(documentation.getName());
        view.setCreatedDate(documentation.getCreated() == null ? "" : DateFormatter.formatDateTime(documentation.getCreated()));
        view.setType(documentation.getType());
        view.setAnnotation(documentation.getAnnotation());
        view.setProject(documentation.getProject() == null ? "" : documentation.getProject());
        view.setManager(documentation.getManagerShortName() == null ? "" : documentation.getManagerShortName());
        view.setNumberDecimal(DecimalNumberFormatter.formatNumber(documentation.getDecimalNumber()));
        view.setNumberInventory(documentation.getInventoryNumber() == null ? "" : documentation.getInventoryNumber().toString());
        view.setKeyWords(documentation.getTags() == null ? "" : documentation.getTags().stream().collect(Collectors.joining(", ")));
    }

    @Inject Lang lang;
    @Inject AbstractDocumentationPreviewView view;
}
