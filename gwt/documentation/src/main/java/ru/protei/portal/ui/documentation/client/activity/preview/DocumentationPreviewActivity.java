package ru.protei.portal.ui.documentation.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.DocumentationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentationServiceAsync;
import ru.protei.portal.ui.documentation.client.common.DocumentationUtils;

import java.util.stream.Collectors;

// TODO implement stub
public abstract class DocumentationPreviewActivity implements Activity, AbstractDocumentationPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(DocumentationEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        Long documentationId = event.documentation.getId();
        if (documentationId == null) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
        } else {
            fillView(event.documentation);
        }
    }

    @Override
    public void onCopyClicked() {/* stub */}

    @Override
    public void onRemoveClicked() {/* stub */}

    private void fillView(Documentation documentation) {
        this.documentation = documentation;
        view.setHeader(lang.documentationDescription() + " #" + documentation.getId());
        view.setName(documentation.getName());
        view.setCreatedDate(documentation.getCreated() == null ? "" : DateFormatter.formatDateTime(documentation.getCreated()));
        view.setType(documentation.getType());
        view.setAnnotation(documentation.getAnnotation());
        view.setProject(documentation.getProject() == null ? "" : documentation.getProject());
        view.setManager(documentation.getManagerShortName() == null ? "" : documentation.getManagerShortName());
        view.setNumberDecimal(DocumentationUtils.formatNumber(documentation.getDecimalNumber()));
        view.setNumberInventory(documentation.getInventoryNumber() == null ? "" : documentation.getInventoryNumber().toString());
        view.setKeyWords(documentation.getTags().stream().collect(Collectors.joining(", ")));
        view.setCopyBtnEnabledStyle(policyService.hasPrivilegeFor(En_Privilege.DOCUMENTATION_CREATE));
        view.setRemoveBtnEnabledStyle(policyService.hasPrivilegeFor(En_Privilege.DOCUMENTATION_REMOVE));
    }

    private Documentation documentation;

    @Inject Lang lang;
    @Inject AbstractDocumentationPreviewView view;
    @Inject DocumentationServiceAsync documentationService;
    @Inject PolicyService policyService;

}
