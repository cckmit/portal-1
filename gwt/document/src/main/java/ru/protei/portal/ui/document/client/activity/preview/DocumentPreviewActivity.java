package ru.protei.portal.ui.document.client.activity.preview;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
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
import ru.protei.portal.ui.common.client.widget.document.uploader.UploadHandler;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        loadDocument(event.documentId, this::fillView);
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

    @Override
    public void onUploadDocFileClicked() {
        String comment = view.documentDocComment().getValue();
        boolean isDocFileSet = view.documentDocUploader().isFileSet();
        if (!isDocFileSet || StringUtils.isEmpty(comment)) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
            return;
        }
        view.documentDocUploadContainerLoading().setVisible(true);
        uploadDoc(document, () ->
            documentService.updateDocumentDocFileByMember(document.getId(), comment, new FluentCallback<Document>()
                    .withError(throwable -> {
                        view.documentDocUploadContainerLoading().setVisible(false);
                        defaultErrorHandler.accept(throwable);
                    })
                    .withSuccess(doc -> {
                        view.documentDocUploadContainerLoading().setVisible(false);
                        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                        fillView(doc);
                    }))
        );
    }

    private void loadDocument(Long documentId, Consumer<Document> onSuccess) {
        documentService.getDocument(documentId, new FluentCallback<Document>().withSuccess(onSuccess));
    }

    private void fillView(Document document) {
        this.document = document;
        boolean hasAccessToPdf = hasAccessToPdf();
        boolean hasAccessToDoc = hasAccessToDoc(document);
        boolean hasAccessToDocModification = hasAccessToDocModification(document);
        view.setHeader(document.getName() + " (#" + document.getId() + ")");
        view.setVersion(lang.documentVersion() + " " + document.getVersion());
        view.setCreatedBy(lang.createBy("", DateFormatter.formatDateTime(document.getCreated())));
        view.setType(document.getType().getName());
        view.setAnnotation(document.getAnnotation());
        view.setNumberDecimal(document.getDecimalNumber());
        view.setNumberInventory(document.getInventoryNumber() == null ? "" : document.getInventoryNumber().toString());
        view.setExecutionType(document.getExecutionType() == null ? "" : executionTypeLang.getName(document.getExecutionType()));
        view.setKeyWords(document.getKeywords() == null ? "" : HelperFunc.join(", ", document.getKeywords()));
        view.setDownloadLinkPdf(hasAccessToPdf ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/pdf" : null);
        view.setDownloadLinkDoc(hasAccessToDoc ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/doc" : null);
        view.setContractor(document.getContractor() == null ? "" : document.getContractor().getDisplayShortName());
        view.setRegistrar(document.getRegistrar() == null ? "" : document.getRegistrar().getDisplayShortName());
        view.documentDocUploader().resetForm();
        view.documentDocComment().setValue("");
        view.documentDocVisibility().setVisible(hasAccessToDocModification);
        view.documentDocUploadContainerLoading().setVisible(false);
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

    private void uploadDoc(Document document, Runnable andThen) {
        if (!view.documentDocUploader().isFileSet()) {
            andThen.run();
            return;
        }
        view.documentDocUploader().setUploadHandler(new UploadHandler() {
            @Override
            public void onError() { new NotifyEvents.Show(lang.errSaveDocumentFile(), NotifyEvents.NotifyType.ERROR); }
            @Override
            public void onSuccess() { andThen.run(); }
        });
        view.documentDocUploader().uploadBindToDocument(document);
    }

    private boolean hasAccessToDoc(Document document) {
        if (policyService.hasGrantAccessFor(En_Privilege.DOCUMENT_EDIT)) {
            return true;
        }
        if (!policyService.hasGrantAccessFor(En_Privilege.DOCUMENT_VIEW)) {
            return false;
        }
        Long currentPersonId = policyService.getProfile().getId();
        return CollectionUtils.stream(document.getMembers())
                .map(Person::getId)
                .collect(Collectors.toList())
                .contains(currentPersonId);
    }

    private boolean hasAccessToDocModification(Document document) {
        return hasAccessToDoc(document) && !document.getApproved();
    }

    private boolean hasAccessToPdf() {
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
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Document document;
    private AppEvents.InitDetails initDetails;
}
