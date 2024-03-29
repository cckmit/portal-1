package ru.protei.portal.ui.documenttype.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.DocumentTypeEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentTypeControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

/**
 * Активность превью проекта
 */
public abstract class DocumentTypePreviewActivity implements AbstractDocumentTypePreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( DocumentTypeEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.documentType = event.type;

        fillView();
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new DocumentTypeEvents.ClosePreview());
    }

    @Override
    public void onSaveClicked() {
        boolean isNew = documentType.getId() == null;
        if ( isNew && !policyService.hasPrivilegeFor( En_Privilege.DOCUMENT_TYPE_CREATE ) ) {
            return;
        }

        if (!isNew && !policyService.hasPrivilegeFor( En_Privilege.DOCUMENT_TYPE_EDIT ) ) {
            return;
        }

        documentType.setDocumentCategory(view.category().getValue());
        documentType.setShortName(view.shortName().getValue());
        documentType.setName(view.name().getValue());
        documentType.setGost(view.gost().getValue());

        String error;
        if ((error = getValidationError(documentType)) != null) {
            showValidationError(error);
            return;
        }

        service.saveDocumentType(documentType, new RequestCallback<DocumentType>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(DocumentType type) {
                fireEvent( new DocumentTypeEvents.Changed(type, isNew));
                fireEvent(new DocumentTypeEvents.ClosePreview());
                fireEvent(new DocumentTypeEvents.ChangeModel());
            }
        });
    }

    private void fillView() {
        if (documentType == null) {
            documentType = new DocumentType();
            view.nameValidation().setValid(false);
        } else {
            view.nameValidation().setValid(true);
        }

        view.name().setValue(documentType.getName());
        view.shortName().setValue(documentType.getShortName());
        view.category().setValue(documentType.getDocumentCategory());
        view.gost().setValue(documentType.getGost());

        view.shortNameValidation().setValid(true);
        view.gostValidation().setValid(true);
    }

    private String getValidationError(DocumentType documentType) {
        if (StringUtils.isBlank(documentType.getName()))
            return lang.documentTypeNameValidationError();

        return null;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentTypePreviewView view;
    @Inject
    PolicyService policyService;
    @Inject
    DocumentTypeControllerAsync service;

    private DocumentType documentType;
}
