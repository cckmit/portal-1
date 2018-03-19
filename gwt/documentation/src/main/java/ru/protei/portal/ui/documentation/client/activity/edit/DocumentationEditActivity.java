package ru.protei.portal.ui.documentation.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentationServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import static ru.protei.portal.core.model.helper.DocumentHelper.isDocumentValid;

public abstract class DocumentationEditActivity
        implements Activity, AbstractDocumentationEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(DocumentationEvents.Edit event) {
        if (event.id == null) {
            fillView(new Documentation());
            return;
        }

        documentationService.getDocumentation(event.id, new RequestCallback<Documentation>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(lang.errGetList());
            }

            @Override
            public void onSuccess(Documentation result) {
                fillView(result);
            }
        });
    }

    private void fireErrorMessage(String msg) {
        fireEvent(new NotifyEvents.Show(msg, NotifyEvents.NotifyType.ERROR));
    }

    @Override
    public void onSaveClicked() {
        Documentation documentation = applyChanges();
        if (!isDocumentValid(documentation)) {
            fireEvent(new NotifyEvents.Show(getValidationErrorMessage(documentation), NotifyEvents.NotifyType.ERROR));
            return;
        } else if (!view.isDecimalNumbersCorrect()) {
            return;
        }

        documentationService.saveDocumentation(documentation, new RequestCallback<Documentation>() {
            @Override
            public void onError(Throwable throwable) {
                fireErrorMessage(throwable.getMessage());
            }

            @Override
            public void onSuccess(Documentation result) {
                fireEvent(new DocumentationEvents.ChangeDocumentTypeModel());
                fireEvent(new Back());
            }
        });
    }

    private String getValidationErrorMessage(Documentation doc) {
        if (isDocumentValid(doc)) {
            return null;
        }
        if (doc.getDecimalNumber() == null) {
            return lang.decimalNumberNotSet();
        }
        if (doc.getType() == null) {
            return lang.documentTypeIsEmpty();
        }
        if (HelperFunc.isEmpty(doc.getProject())) {
            return lang.documentProjectIsEmpty();
        }
        if (doc.getManagerId() == null) {
            return lang.customerNotSet();
        }
        if (doc.getInventoryNumber() == null) {
            return lang.inventoryNumberIsEmpty();
        }
        if (doc.getInventoryNumber() <= 0) {
            return lang.negativeInventoryNumber();
        }
        if (HelperFunc.isEmpty(doc.getName())) {
            return lang.documentNameIsNotSet();
        }
        return null;
    }


    private Documentation applyChanges() {
        documentation.setName(view.name().getValue());
        documentation.setAnnotation(view.annotation().getValue());
        documentation.setDecimalNumber(view.decimalNumber().getValue());
        documentation.setType(view.documentType().getValue());
        documentation.setInventoryNumber(view.inventoryNumber().getValue());
        documentation.setKeywords(view.keywords().getValue());
        documentation.setManagerId(view.manager().getValue() == null ? null : view.manager().getValue().getId());
        documentation.setProject(view.project().getValue());
        return documentation;
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void fillView(Documentation documentation) {
        this.documentation = documentation;

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        PersonShortView manager = new PersonShortView();
        manager.setId(documentation.getManagerId());

        view.name().setValue(documentation.getName());
        view.annotation().setValue(documentation.getAnnotation());
        view.created().setValue(DateFormatter.formatDateTime(documentation.getCreated()));
        view.decimalNumber().setValue(documentation.getDecimalNumberId() == null ? null : documentation.getDecimalNumber());
        view.documentType().setValue(documentation.getType());
        view.inventoryNumber().setValue(documentation.getInventoryNumber());
        view.keywords().setValue(documentation.getKeywords());
        view.manager().setValue(manager);
        view.project().setValue(documentation.getProject());
    }

    @Inject
    AbstractDocumentationEditView view;

    @Inject
    Lang lang;

    Documentation documentation;

    @Inject
    DocumentationServiceAsync documentationService;

    private AppEvents.InitDetails initDetails;
}
