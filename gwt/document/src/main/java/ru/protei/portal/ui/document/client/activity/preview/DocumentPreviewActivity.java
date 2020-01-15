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
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_DocumentExecutionTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.function.Consumer;

public abstract class DocumentPreviewActivity implements Activity, AbstractDocumentPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(DocumentEvents.ShowPreview event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }
        event.parent.clear();
        event.parent.add(view.asWidget());
        view.footerVisibility().setVisible(false);
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

    @Event
    public void onShow(DocumentEvents.ShowPreviewFullScreen event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.footerVisibility().setVisible(true);
        loadDocument(event.documentId, this::fillView);
    }

    @Override
    public void onBackClicked() {
        fireEvent(new DocumentEvents.Show());
    }

    private void loadDocument(Long documentId, Consumer<Document> onSuccess) {
        documentService.getDocument(documentId, new FluentCallback<Document>().withSuccess(onSuccess));
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
        view.setDownloadLinkPdf(canView() ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/pdf" : null);
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

    private boolean canView() {
        return policyService.hasGrantAccessFor(En_Privilege.DOCUMENT_VIEW);
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
    @Inject
    DocumentControllerAsync documentService;

    private AppEvents.InitDetails initDetails;
}
