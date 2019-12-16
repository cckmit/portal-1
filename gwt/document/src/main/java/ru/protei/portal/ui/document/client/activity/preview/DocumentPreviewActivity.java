package ru.protei.portal.ui.document.client.activity.preview;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_DocumentExecutionTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

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
        view.setHeader(document.getName() + " (#" + document.getId() + ")");
        view.setVersion(lang.documentVersion() + " " + document.getVersion());
        view.setCreatedBy(lang.createBy("", DateFormatter.formatDateTime(document.getCreated())));
        view.setType(document.getType().getName());
        view.setAnnotation(document.getAnnotation());
        view.setNumberDecimal(document.getDecimalNumber());
        view.setNumberInventory(document.getInventoryNumber() == null ? "" : document.getInventoryNumber().toString());
        view.setExecutionType(document.getExecutionType() == null ? "" : executionTypeLang.getName(document.getExecutionType()));
        view.setKeyWords(document.getKeywords() == null ? "" : HelperFunc.join(", ", document.getKeywords()));
        view.setDownloadLinkPdf(canEdit() ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/pdf" : null);
        view.setDownloadLinkDoc(canEdit() ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/doc" : null);
        view.setContractor(document.getContractor() == null ? "" : document.getContractor().getDisplayShortName());
        view.setRegistrar(document.getRegistrar() == null ? "" : document.getRegistrar().getDisplayShortName());
        fillProject(document);
    }

    private void fillProject(Document document) {
        if (document.getProjectId() == null) {
            view.setProject("");
            view.setManager("");
        } else {
            regionService.getProject(document.getProjectId(), new ShortRequestCallback<Project>()
                    .setOnSuccess(project -> {
                        view.setProject(project.getName());
                        view.setManager(project.getLeader() == null ? "" : project.getLeader().getName());
                    } ));
        }
    }

    private boolean canEdit() {
        return policyService.hasGrantAccessFor(En_Privilege.DOCUMENT_EDIT);
    }

    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/download/document/";

    @Inject
    RegionControllerAsync regionService;
    @Inject
    Lang lang;
    @Inject
    En_DocumentExecutionTypeLang executionTypeLang;
    @Inject
    AbstractDocumentPreviewView view;
    @Inject
    PolicyService policyService;
}
