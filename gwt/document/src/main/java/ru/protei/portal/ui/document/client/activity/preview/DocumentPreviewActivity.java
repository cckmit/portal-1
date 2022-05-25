package ru.protei.portal.ui.document.client.activity.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_DocumentExecutionTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.document.uploader.UploadHandler;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.emptyIfNull;

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
            fireEvent(new ErrorPageEvents.ShowForbidden(event.parent));
            return;
        }
        event.parent.clear();
        event.parent.add(view.asWidget());
        view.showFullScreen(false);
        loadDocument(event.documentId, this::fillView);
    }

    @Event
    public void onShow(DocumentEvents.ShowPreviewFullScreen event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.showFullScreen(true);
        loadDocument(event.documentId, this::fillView);
    }

    @Override
    public void onBackClicked() {
        fireEvent(new DocumentEvents.Show(true));
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
            documentController.updateDocumentDocFileByMember(document.getId(), comment, new FluentCallback<Document>()
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

    @Override
    public void onFullScreenClicked() {
        fireEvent(new DocumentEvents.ShowPreviewFullScreen(document.getId()));
    }

    private void loadDocument(Long documentId, Consumer<Document> onSuccess) {
        documentController.getDocument(documentId, new FluentCallback<Document>().withSuccess(onSuccess));
    }

    private void fillView(Document document) {
        this.document = document;
        boolean hasAccessToPdf = hasAccessToPdf();
        boolean hasAccessToDoc = hasAccessToDoc(document);
        boolean hasAccessToDocModification = hasAccessToDocModification(document);
        view.setDocumentNumber(document.getName() + " (#" + document.getId() + ")");
        view.setDocumentNumberHref(LinkUtils.makePreviewLink(Document.class, document.getId()));
        view.setVersion(lang.documentVersion() + " " + emptyIfNull(document.getVersion()));
        view.setCreatedBy(lang.createBy("", DateFormatter.formatDateTime(document.getCreated())));
        view.setType(document.getType().getName());
        view.setAnnotation(document.getAnnotation());
        view.setNumberDecimal(document.getDecimalNumber());
        view.setNumberInventory(document.getInventoryNumber() == null ? "" : document.getInventoryNumber().toString());
        view.setExecutionType(document.getExecutionType() == null ? "" : executionTypeLang.getName(document.getExecutionType()));
        view.setKeyWords(document.getKeywords() == null ? "" : HelperFunc.join(", ", document.getKeywords()));
        view.setDownloadLinkPdf(hasAccessToPdf ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/pdf" : null);
        view.setDownloadLinkDoc(hasAccessToDoc ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/doc" : null);
        view.setDownloadLinkApprovalSheet(hasAccessToPdf ? DOWNLOAD_PATH + document.getProjectId() + "/" + document.getId() + "/as" : null);
        view.setContractor(document.getContractor() == null ? "" : document.getContractor().getDisplayShortName());
        view.setRegistrar(document.getRegistrar() == null ? "" : document.getRegistrar().getDisplayShortName());
        view.setMembers(StringUtils.join( document.getMembers(), PersonShortView::getDisplayShortName, ", "));
        view.documentDocUploader().resetForm();
        view.documentDocComment().setValue("");
        view.documentDocVisibility().setVisible(hasAccessToDocModification);
        view.approvalContainerVisibility().setVisible(document.getApproved());
        view.setApprovedBy(!document.getApproved() || document.getApprovedBy() == null ? "" : document.getApprovedBy().getDisplayShortName());
        view.setApprovalDate(!document.getApproved() || document.getApprovalDate() == null ? "" : DateTimeFormat.getFormat("dd.MM.yyyy").format(document.getApprovalDate()));
        view.documentDocUploadContainerLoading().setVisible(false);
        fillProject(document);
    }

    private void fillProject(Document document) {
        if (document.getProjectId() == null || !policyService.hasPrivilegeFor(En_Privilege.PROJECT_VIEW)) {
            view.setProject("");
            view.setManager("");
        } else {
            regionService.getProjectInfo(document.getProjectId(), new ShortRequestCallback< ProjectInfo >()
                    .setOnSuccess(project -> {
                        view.setProject(project.getName());
                        view.setManager(project.getManager() == null ? "" : project.getManager().getName());
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
        if (policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_EDIT)) {
            return true;
        }
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_VIEW)) {
            return false;
        }
        Long currentPersonId = policyService.getProfile().getId();
        return CollectionUtils.stream(document.getMembers())
                .map(PersonShortView::getId)
                .collect(Collectors.toList())
                .contains(currentPersonId);
    }

    private boolean hasAccessToDocModification(Document document) {
        return hasAccessToDoc(document) && !document.getApproved() && !document.isDeprecatedUnit();
    }

    private boolean hasAccessToPdf() {
        return policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_VIEW);
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
    DocumentControllerAsync documentController;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Document document;
    private AppEvents.InitDetails initDetails;
}
